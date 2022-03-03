package xyz.jamesnuge.scheduling;

import fj.data.Either;

public interface StateMachine<T extends State, S> {
    void accept(S trigger) throws Exception;
    Either<String, T> getCurrentState();
}
