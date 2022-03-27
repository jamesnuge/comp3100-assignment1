package xyz.jamesnuge.scheduling;

import java.util.Objects;

class TestState implements State {
    private final Boolean isFinalState;
    private final Integer counter;

    public TestState(final Boolean isFinalState) {
        this.isFinalState = isFinalState;
        this.counter = 0;
    }

    public TestState(final Boolean isFinalState, final Integer counter) {
        this.isFinalState = isFinalState;
        this.counter = counter;
    }

    @Override
    public Boolean isFinalState() {
        return isFinalState;
    }

    public Integer getCounter() {
        return counter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestState testState = (TestState) o;
        return Objects.equals(isFinalState, testState.isFinalState) && Objects.equals(counter, testState.counter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isFinalState, counter);
    }

    @Override
    public String toString() {
        return "TestState{" +
                "isFinalState=" + isFinalState +
                ", counter=" + counter +
                '}';
    }
}
