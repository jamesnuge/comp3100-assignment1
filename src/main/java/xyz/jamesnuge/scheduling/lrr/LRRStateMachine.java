package xyz.jamesnuge.scheduling.lrr;

import fj.Ord;
import fj.data.Either;
import fj.data.List;
import fj.function.Try2;
import xyz.jamesnuge.MessageParser;
import xyz.jamesnuge.Pair;
import xyz.jamesnuge.Util;
import xyz.jamesnuge.messaging.ClientMessagingService;
import xyz.jamesnuge.scheduling.StateMachine;
import xyz.jamesnuge.state.ServerStateItem;

import static fj.Ord.on;
import static fj.Ord.ordDef;
import static fj.data.Either.left;
import static fj.data.Either.right;
import static xyz.jamesnuge.MessageParser.Message.OK;
import static xyz.jamesnuge.Util.chain;
import static xyz.jamesnuge.Util.flatMap;
import static xyz.jamesnuge.Util.tryEither;

public class LRRStateMachine implements StateMachine<LRRInternalState, String> {

    private final String largestServerType;
    private final ClientMessagingService clientMessagingService;
    private final Try2<Integer, Integer, LRRInternalState, Exception> generateState;
    private final Try2<Integer, Integer, LRRInternalState, Exception> generateFinalState;
    private Either<String, LRRInternalState> currentState;

    public LRRStateMachine(final ClientMessagingService clientMessagingService) {
        final Either<String, List<ServerStateItem>> serverState = clientMessagingService.getServerState();
        this.largestServerType = getHighestCapacityServerType(serverState.right().value());
        this.generateState = LRRInternalState.createInternalStateFactory(largestServerType);
        this.generateFinalState = LRRInternalState.createFinalInternalStateFactory(largestServerType);
        this.clientMessagingService = clientMessagingService;
        this.currentState = Util.flatMap(
                serverState.rightMap((s) -> s.filter((item) -> item.getType().equals(largestServerType)).length()),
                (s) -> {
                    try {
                        return right(this.generateState.f(-1, s));
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
                        return flatMap(
                                chain(
                                        clientMessagingService.scheduleJob(Integer.valueOf(params.index(1)), largestServerType, nextServer.getLeft()),
                                        (_s) -> clientMessagingService.getMessage(),
                                        (s) -> s.equals(OK.name()) ? clientMessagingService.signalRedy() : chain(clientMessagingService.getMessage(), (_s) -> clientMessagingService.signalRedy())
                                ),
                                (_s) -> getCurrentState(),
                                (state) -> tryEither(() -> generateState.f(nextServer.getLeft(), nextServer.getRight()))
                        );
                    });
        } else if (trigger.contains(MessageParser.InboudMessage.RESF.name())) {
            this.currentState = Util.flatMap(
                    currentState,
                    (state) -> tryEither(() -> {
                        List<String> triggerParams = List.list(trigger.substring(5).split(" "));
                        return generateState.f(
                                state.getLastAssignedServerId(),
                                state.getNumberOfServers()
                        );
                    })
            );
        } else if (trigger.contains(MessageParser.InboudMessage.JCPL.name())) {
            clientMessagingService.signalRedy();
        } else if (trigger.contains(MessageParser.InboudMessage.NONE.name())) {
            this.currentState = tryEither(() -> generateFinalState.f(-1, -1));
        }

    }

    private static String getHighestCapacityServerType(final List<ServerStateItem> config) {
        return config.maximum(ordDef(on(ServerStateItem::getCores, Ord.intOrd))).getType();
    }

    @Override
    public Either<String, LRRInternalState> getCurrentState() {
        return currentState;
    }

    private Either<String, Pair<Integer, Integer>> getNextServerId() {
        return currentState.rightMap((state) -> new Pair<>((state.getLastAssignedServerId() + 1) % state.getNumberOfServers(), state.getNumberOfServers()));
    }
}
