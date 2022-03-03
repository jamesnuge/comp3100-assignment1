package xyz.jamesnuge.scheduling;

import fj.data.Either;

public interface StateMachine<T extends State, S> {
    T accept(S trigger);
    Either<String, T> getCurrentState();
}
