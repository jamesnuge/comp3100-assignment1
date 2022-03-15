package xyz.jamesnuge.scheduling;

import fj.data.Either;
import xyz.jamesnuge.messaging.ClientMessagingService;

@FunctionalInterface
public interface StateConfigurationFactory<S extends State> {
    Either<String, S> createInitialState(ClientMessagingService cms);
}
