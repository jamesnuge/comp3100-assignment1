package xyz.jamesnuge.scheduling.worstFit;

import java.util.Objects;
import xyz.jamesnuge.scheduling.State;

public class WfInternalState implements State {

    private final Boolean isFinalState;

    public WfInternalState(final Boolean isFinalState) {
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
        WfInternalState that = (WfInternalState) o;
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
