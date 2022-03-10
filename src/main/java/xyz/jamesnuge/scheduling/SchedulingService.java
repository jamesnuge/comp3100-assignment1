package xyz.jamesnuge.scheduling;

import fj.data.Either;
import java.util.Map;
import java.util.function.Function;
import xyz.jamesnuge.Pair;
import xyz.jamesnuge.messaging.ClientMessagingService;

import static fj.data.Either.left;
import static fj.data.Either.right;
import static xyz.jamesnuge.Util.chain;
import static xyz.jamesnuge.Util.flatMap;

public class SchedulingService {

    private final ClientMessagingService clientMessagingService;
    private final Map<String, AlgorithmFactory<? extends State>> algorithms;

    public SchedulingService(final ClientMessagingService clientMessagingService, Map<String, AlgorithmFactory<? extends State>> algorithms) {
        this.clientMessagingService = clientMessagingService;
        this.algorithms = algorithms;
    }

    public Either<String, String> scheduleJobsUsingAlgorithm(String algorithm) {
        if (algorithms.containsKey(algorithm)) {
            return chain(
                    clientMessagingService.loginToServer("user"),
                    (s) -> clientMessagingService.beginScheduling(),
                    (_s) -> clientMessagingService.getMessage(),
                    (s) -> {
                        Pair<? extends StateMachine<? extends State, String>, ? extends Either<String, ? extends State>> stateMachineAndState = algorithms.get(algorithm).createAlgorithm(clientMessagingService);
                        return flatMap(
                                stateMachineAndState.getValue(),
                                (state) -> process((StateMachine)stateMachineAndState.getLeft(), state, s)
                        );
                    },
                    (_s) -> clientMessagingService.quit(),
                    (_s) -> right("Successfully ran algorithm")
            );
        } else {
            return left("Algorithm " + algorithm + " not found");
        }
    }

    private Either<String, String> process(StateMachine<State, String> stateMachine, State initialState, String message) {
        Either<String, ? extends State> accept = stateMachine.accept(message, initialState);
        if (accept.isLeft()) {
            return left("Failed to process message: " + message);
        } else {
            State state = accept.right().value();
            if (state.isFinalState()) {
                return right("Successfully ran algorithm");
            } else {
                return run(stateMachine, state);
            }
        }
    }

    private Either<String, String> run(StateMachine<State, String> stateMachine, State state) {
        while (true) {
            Either<String, String> message = clientMessagingService.getMessage();
            if (message.isLeft()) {
                return message;
            } else {
                Either<String, ? extends State> accept = stateMachine.accept(message.right().value(), state);
                if (accept.isLeft()) {
                    return left("Failed to process message: " + message + ". " + accept.left().value());
                } else {
                    State value = accept.right().value();
                    if (value.isFinalState()) {
                        return right("Successfully ran algorithm");
                    }
                }
            }
        }
    }

}
