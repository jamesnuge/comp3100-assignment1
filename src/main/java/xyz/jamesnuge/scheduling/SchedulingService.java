package xyz.jamesnuge.scheduling;

import fj.data.Either;
import java.util.Map;
import java.util.function.Function;
import xyz.jamesnuge.messaging.ClientMessagingService;

import static fj.data.Either.left;
import static fj.data.Either.right;
import static xyz.jamesnuge.Util.chain;
import static xyz.jamesnuge.Util.flatMap;

public class SchedulingService {

    private final ClientMessagingService clientMessagingService;
    private final Map<String, Function<ClientMessagingService, StateMachine<? extends State, String>>> algorithms;

    public SchedulingService(final ClientMessagingService clientMessagingService, Map<String, Function<ClientMessagingService, StateMachine<? extends State, String>>> algorithms) {
        this.clientMessagingService = clientMessagingService;
        this.algorithms = algorithms;
    }

    public Either<String, String> scheduleJobsUsingAlgorithm(String algorithm) {
        if (algorithms.containsKey(algorithm)) {
            return chain(
                    clientMessagingService.loginToServer("user"),
                    (s) -> clientMessagingService.beginScheduling(),
                    (_s) -> clientMessagingService.getMessage(),
                    (s) -> process(algorithms.get(algorithm).apply(clientMessagingService), s),
                    (_s) -> clientMessagingService.quit(),
                    (_s) -> right("Successfully ran algorithm")
            );
        } else {
            return left("Algorithm " + algorithm + " not found");
        }
    }

    private Either<String, String> process(StateMachine<?, String> stateMachine) {
        Either<String, String> message = clientMessagingService.getMessage();
        if (message.isLeft()) {
            return message;
        } else {
            return process(stateMachine, message.right().value());
        }
    }

    private Either<String, String> process(StateMachine<?, String> stateMachine, String message) {
        stateMachine.accept(message);
        Either<String, ? extends State> currentState = stateMachine.getCurrentState();
        return flatMap(
                currentState,
                (state) -> {
                    if (state.isFinalState()) {
                        return right("Successfully ran algorithm");
                    } else {
                        return process(stateMachine);
                    }
                }
        );
    }



}
