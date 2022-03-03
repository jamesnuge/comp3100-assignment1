package xyz.jamesnuge.scheduling.lrr;

import fj.Ord;
import fj.data.Either;
import fj.data.List;
import fj.function.Try3;
import xyz.jamesnuge.MessageParser;
import xyz.jamesnuge.Pair;
import xyz.jamesnuge.Util;
import xyz.jamesnuge.messaging.ClientMessagingService;
import xyz.jamesnuge.scheduling.StateMachine;
import xyz.jamesnuge.state.ServerStateItem;

import static fj.data.Either.left;
import static fj.data.Either.right;

public class LRRStateMachine implements StateMachine<LRRInternalState, String> {

    private final String largestServerType;
    private final ClientMessagingService clientMessagingService;
    private final Try3<Integer, Integer, List<Integer>, LRRInternalState, Exception> generateState;
    private final Try3<Integer, Integer, List<Integer>, LRRInternalState, Exception> generateFinalState;
    private Either<String, LRRInternalState> currentState;

    public LRRStateMachine(ClientMessagingService clientMessagingService) {
        Either<String, List<ServerStateItem>> serverState = clientMessagingService.getServerState();
        this.largestServerType = getHighestCapacityServerType(serverState.right().value());
        this.generateState = LRRInternalState.createInternalStateFactory(largestServerType);
        this.generateFinalState = LRRInternalState.createInternalStateFactory(largestServerType);
        this.clientMessagingService = clientMessagingService;
        this.currentState = Util.flatMap(
                serverState.rightMap((s) -> s.filter((item) -> item.getType().equals(largestServerType)).length()),
                (s) -> {
                    try {
                        return right(this.generateState.f(-1, s, List.nil()));
                    } catch (Exception e) {
                        return left(e.getMessage());
                    }
                });
    }


    @Override
    public void accept(String trigger) {
        if (trigger.contains(MessageParser.InboudMessage.JOBN.name())) {
            this.currentState = Util.flatMap(
                    getNextServerId(),
                    (nextServer) -> {
                        final List<String> params = List.list(trigger.substring(5).split(" "));
                        clientMessagingService.scheduleJob(Integer.valueOf(params.index(1)), largestServerType, nextServer.getLeft());
                        return Util.flatMap(
                                getCurrentState(),
                                (state) -> {
                                    try {
                                        return right(generateState.f(nextServer.getLeft(), nextServer.getRight(), state.getUnavailableServers()));
                                    } catch (Exception e) {
                                        return left(e.getMessage());
                                    }
                                });
                    });
        } else if (trigger.contains(MessageParser.InboudMessage.RESF.name())) {
            this.currentState =
                    Util.flatMap(
                            currentState,
                            (state) -> {
                                List<String> triggerParams = List.list(trigger.substring(5).split(" "));
                                final List<Integer> unavailableServers = state.getUnavailableServers();
                                try {
                                    return right(generateState.f(
                                            state.getLastAssignedServerId(),
                                            state.getNumberOfServers(),
                                            unavailableServers.append(List.list(Integer.parseInt(triggerParams.index(1))))
                                    ));
                                } catch (Exception e) {
                                    return left(e.getMessage());
                                }
                            }
                    );
        }

    }

    private static String getHighestCapacityServerType(final List<ServerStateItem> config) {
        return config.maximum(Ord.ordDef(Ord.on(ServerStateItem::getCores, Ord.intOrd))).getType();
    }

    @Override
    public Either<String, LRRInternalState> getCurrentState() {
        return currentState;
    }

    private Either<String, Pair<Integer, Integer>> getNextServerId() {
        return currentState.rightMap((state) -> new Pair<>((state.getLastAssignedServerId() + 1) % state.getNumberOfServers(), state.getNumberOfServers()));
    }
}
