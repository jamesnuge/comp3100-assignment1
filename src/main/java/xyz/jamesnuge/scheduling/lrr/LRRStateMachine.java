package xyz.jamesnuge.scheduling.lrr;

import fj.data.Either;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import xyz.jamesnuge.messaging.ClientMessagingService;
import xyz.jamesnuge.scheduling.StateMachine;
import xyz.jamesnuge.state.ServerStateItem;

public class LRRStateMachine implements StateMachine<LRRInternalState, String> {

    private final String largestServerType;
    private final ClientMessagingService clientMessagingService;
    private final BiFunction<Long, Integer, LRRInternalState> generateState;
    private final BiFunction<Long, Integer, LRRInternalState> generateFinalState;
    private Either<String, LRRInternalState> currentState;

    public LRRStateMachine(ClientMessagingService clientMessagingService, final List<ServerStateItem> config) {
        this.largestServerType = getHighestCapacityServerType(config);
        this.generateState = LRRInternalState.createInternalStateFactory(largestServerType);
        this.generateFinalState = LRRInternalState.createInternalStateFactory(largestServerType);
        this.clientMessagingService = clientMessagingService;
        this.currentState = clientMessagingService.getServerState()
                .rightMap((s) -> s.stream().filter((item) -> item.getType().equals(largestServerType)).count())
                .rightMap((s) -> this.generateState.apply(0L, s.intValue()));
    }


    @Override
    public LRRInternalState accept(String trigger) {
        return null;
    }

    private static String getHighestCapacityServerType(final List<ServerStateItem> config) {
        return config.stream().max(Comparator.comparingInt(ServerStateItem::getCores)).get().getType();
    }

    @Override
    public Either<String, LRRInternalState> getCurrentState() {
        return currentState;
    }
}
