package xyz.jamesnuge.scheduling.lrr;

import fj.data.Either;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import xyz.jamesnuge.MessageParser;
import xyz.jamesnuge.messaging.ClientMessagingService;
import xyz.jamesnuge.scheduling.StateMachine;
import xyz.jamesnuge.state.ServerStateItem;

public class LRRStateMachine implements StateMachine<LRRInternalState, String> {

    private final String largestServerType;
    private final ClientMessagingService clientMessagingService;
    private final BiFunction<Integer, Integer, LRRInternalState> generateState;
    private final BiFunction<Integer, Integer, LRRInternalState> generateFinalState;
    private Either<String, LRRInternalState> currentState;

    public LRRStateMachine(ClientMessagingService clientMessagingService) {
        Either<String, List<ServerStateItem>> serverState = clientMessagingService.getServerState();
        this.largestServerType = getHighestCapacityServerType(serverState.right().value());
        this.generateState = LRRInternalState.createInternalStateFactory(largestServerType);
        this.generateFinalState = LRRInternalState.createInternalStateFactory(largestServerType);
        this.clientMessagingService = clientMessagingService;
        this.currentState = serverState
                .rightMap((s) -> s.stream().filter((item) -> item.getType().equals(largestServerType)).count())
                .rightMap((s) -> this.generateState.apply(-1, s.intValue()));
    }


    @Override
    public void accept(String trigger) {
        if (trigger.contains(MessageParser.InboudMessage.JOBN.name())) {
            this.currentState = currentState.rightMap((state) -> {
                final Integer serverToAssignTo = state.getLastAssignedServerId() + 1;
                final Integer numberOfServers = state.getNumberOfServers();
                final List<String> params = Arrays.asList(trigger.substring(5).split(" "));
                clientMessagingService.scheduleJob(Integer.valueOf(params.get(1)), largestServerType, currentState.right().value().getLastAssignedServerId() + 1);
                return generateState.apply(serverToAssignTo, numberOfServers);
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
}
