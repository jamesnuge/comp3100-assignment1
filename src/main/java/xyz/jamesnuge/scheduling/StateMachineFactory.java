package xyz.jamesnuge.scheduling;

import xyz.jamesnuge.messaging.ClientMessagingService;

@FunctionalInterface
public interface  StateMachineFactory<S extends State> {
    StateMachine<S, String> createStateMachine(ClientMessagingService cms);
}
