package xyz.jamesnuge.scheduling;

import fj.data.Either;

@FunctionalInterface
public interface StateMachine<T extends State, S> {
    Either<String, T> accept(S trigger, T currentState);
}
