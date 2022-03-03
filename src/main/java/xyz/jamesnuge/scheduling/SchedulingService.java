package xyz.jamesnuge.scheduling;

import fj.data.Either;
import java.util.Map;
import xyz.jamesnuge.messaging.ClientMessagingService;

import static fj.data.Either.left;
import static fj.data.Either.right;

public class SchedulingService {

    private final ClientMessagingService clientMessagingService;
    private final Map<String, StateMachine> algorithms;

    public SchedulingService(final ClientMessagingService clientMessagingService, Map<String, StateMachine> algorithms) {
        this.clientMessagingService = clientMessagingService;
        this.algorithms = algorithms;
    }

    public Either<String, String> scheduleJobsUsingAlgorithm(String algorithm) {
        if (algorithms.containsKey(algorithm)) {
            return right("Ran algorithm: " + algorithm);
        } else {
            return left("Algorithm " + algorithm + " not found");
        }
    }

}
