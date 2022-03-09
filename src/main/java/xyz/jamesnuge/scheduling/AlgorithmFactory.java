package xyz.jamesnuge.scheduling;

import fj.data.Either;
import xyz.jamesnuge.Pair;
import xyz.jamesnuge.messaging.ClientMessagingService;

public interface AlgorithmFactory<T extends State> {
    Pair<StateMachine<T, String>, Either<String, T>> createAlgorithm(ClientMessagingService cms);
}
