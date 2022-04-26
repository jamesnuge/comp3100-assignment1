package xyz.jamesnuge.scheduling.firstCapable;

import xyz.jamesnuge.scheduling.State;

import java.util.Objects;

public class FcInternalState implements State {
    private final Boolean isFinalState;

    public FcInternalState(final Boolean isFinalState) {
        this.isFinalState = isFinalState;
    }

    @Override
    public Boolean isFinalState() {
        return this.isFinalState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FcInternalState that = (FcInternalState) o;
        return Objects.equals(isFinalState, that.isFinalState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isFinalState);
    }

    @Override
    public String toString() {
        return "FcInternalState{" +
                "isFinalState=" + isFinalState +
                '}';
    }
}
