package xyz.jamesnuge.scheduling.bestFit;

import java.util.Objects;
import xyz.jamesnuge.scheduling.State;

public class BfInternalState implements State {

    private final Boolean isFinalState;

    public BfInternalState(final Boolean isFinalState) {
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
        BfInternalState that = (BfInternalState) o;
        return Objects.equals(isFinalState, that.isFinalState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isFinalState);
    }

    @Override
    public String toString() {
        return "BfInternalState{" +
                "isFinalState=" + isFinalState +
                '}';
    }
}
