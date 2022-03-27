package xyz.jamesnuge.scheduling;

import static fj.data.Either.left;
import static fj.data.Either.right;
import static xyz.jamesnuge.Util.chain;
import static xyz.jamesnuge.Util.flatMap;

import java.util.Map;

import fj.data.Either;
import xyz.jamesnuge.Pair;
import xyz.jamesnuge.messaging.ClientMessagingService;

public class SchedulingService {

    private final ClientMessagingService clientMessagingService;
    private final Map<String, Pair<StateMachineFactory<? extends State>, StateConfigurationFactory<? extends State>>> algorithms;

    public SchedulingService(final ClientMessagingService clientMessagingService, Map<String, Pair<StateMachineFactory<? extends State>, StateConfigurationFactory<? extends State>>> algorithms) {
        this.clientMessagingService = clientMessagingService;
        this.algorithms = algorithms;
    }

    @SuppressWarnings("unchecked")
    public Either<String, String> scheduleJobsUsingAlgorithm(String algorithm) {
        if (algorithms.containsKey(algorithm)) {
            return chain(
                    clientMessagingService.loginToServer("user"),
                    (s) -> clientMessagingService.beginScheduling(),
                    (_s) -> clientMessagingService.getMessage(),
                    (s) -> {
                        StateMachine<? extends State, String> stateMachine = algorithms.get(algorithm).getLeft().createStateMachine(clientMessagingService);
                        Either<String, ? extends State> eitherState = algorithms.get(algorithm).getRight().createInitialState(clientMessagingService);
                        return flatMap(
                                eitherState,
                                (state) -> process((StateMachine<State, String>)stateMachine, state, s)
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
                // clientMessagingService.quit();
                return right("Successfully ran algorithm");
            } else {
                return run(stateMachine, state);
            }
        }
    }

    private Either<String, String> run(StateMachine<State, String> stateMachine, State state) {
        State currentState = state;
        while (true) {
            Either<String, String> message = clientMessagingService.getMessage();
            if (message.isLeft()) {
                return message;
            } else {
                Either<String, ? extends State> accept = stateMachine.accept(message.right().value(), currentState);
                if (accept.isLeft()) {
                    return left("Failed to process message: " + message + ". " + accept.left().value());
                } else {
                    currentState = accept.right().value();
                    if (currentState.isFinalState()) {
                        return right("Successfully ran algorithm");
                    }
                }
            }
        }
    }

}
