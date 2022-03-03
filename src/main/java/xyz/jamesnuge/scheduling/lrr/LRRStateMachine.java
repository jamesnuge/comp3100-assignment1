package xyz.jamesnuge.scheduling.lrr;

import fj.data.Either;
import fj.function.Try3;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import xyz.jamesnuge.MessageParser;
import xyz.jamesnuge.Pair;
import xyz.jamesnuge.messaging.ClientMessagingService;
import xyz.jamesnuge.scheduling.StateMachine;
import xyz.jamesnuge.state.ServerStateItem;

import static fj.data.Either.left;
import static java.util.Collections.emptyList;

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
        this.currentState = serverState
                .rightMap((s) -> s.stream().filter((item) -> item.getType().equals(largestServerType)).count())
                .rightMap((s) -> {
                    try {
                        return this.generateState.f(-1, s.intValue(), emptyList());
                    } catch (Exception e) {
                        // TODO: Fix this to return left of the error message
                        return null;
                    }
                });
    }


    @Override
    public void accept(String trigger) {
        if (trigger.contains(MessageParser.InboudMessage.JOBN.name())) {
            this.currentState = getNextServerId().rightMap((state) -> {
                final List<String> params = Arrays.asList(trigger.substring(5).split(" "));
                clientMessagingService.scheduleJob(Integer.valueOf(params.get(1)), largestServerType, state.getLeft());
                try {
                    return generateState.f(state.getLeft(), state.getRight(), emptyList());
                } catch (Exception e) {
                    // TODO: Fix same as above. Write wrapper
                    return null;
                }
            });
        }
    }

    private static String getHighestCapacityServerType(final List<ServerStateItem> config) {
        return config.stream().max(Comparator.comparingInt(ServerStateItem::getCores)).get().getType();
    }

    @Override
    public Either<String, LRRInternalState> getCurrentState() {
        return currentState;
    }

    private Either<String, Pair<Integer, Integer>> getNextServerId() {
        return currentState.rightMap((state) -> new Pair<>((state.getLastAssignedServerId() + 1) % state.getNumberOfServers(), state.getNumberOfServers()));
    }
}
