package xyz.jamesnuge.scheduling.lrr;

import java.util.Objects;

import fj.F;
import fj.function.Try2;
import xyz.jamesnuge.scheduling.State;


public class LRRInternalState implements State {

    public static final LRRInternalState INITIAL_STATE = new LRRInternalState(-1, "", -1, false);

    public static final F<String, LRRInternalState> generateInitialState = (s) -> new LRRInternalState(-1, s, 0, false);

    private final Integer lastAssignedServerId;
    private final String serverType;
    private final Integer numberOfServers;
    private final Boolean isFinalState;

    public static Try2<Integer, Integer, LRRInternalState, Exception> createInternalStateFactory(String serverType) {
        return (Integer id, Integer numberOfServers) -> new LRRInternalState(id, serverType, numberOfServers, false);
    }

    public static Try2<Integer, Integer, LRRInternalState, Exception> createFinalInternalStateFactory(String serverType) {
        return (Integer id, Integer numberOfServers) -> new LRRInternalState(id, serverType, numberOfServers, true);
    }


    public LRRInternalState(Integer lastAssignedServerId, String serverType, Integer numberOfServers, Boolean isFinalState) {
        this.lastAssignedServerId = lastAssignedServerId;
        this.serverType = serverType;
        this.numberOfServers = numberOfServers;
        this.isFinalState = isFinalState;
    }

    public Integer getLastAssignedServerId() {
        return lastAssignedServerId;
    }

    public String getServerType() {
        return serverType;
    }

    public Integer getNumberOfServers() {
        return numberOfServers;
    }

    @Override
    public Boolean isFinalState() {
        return isFinalState;
    }

    public LRRInternalState copyWithServerTypeAndNumberOfServers(final Integer lastAssignedServerId, Boolean isFinalState) {
        return new LRRInternalState(lastAssignedServerId, this.getServerType(), this.getNumberOfServers(), isFinalState);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LRRInternalState that = (LRRInternalState) o;
        return Objects.equals(lastAssignedServerId, that.lastAssignedServerId) && Objects.equals(serverType, that.serverType) && Objects.equals(numberOfServers, that.numberOfServers) && Objects.equals(isFinalState, that.isFinalState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastAssignedServerId, serverType, numberOfServers, isFinalState);
    }

    @Override
    public String toString() {
        return "LRRInternalState{" +
                "lastAssignedServerId=" + lastAssignedServerId +
                ", serverType='" + serverType + '\'' +
                ", numberOfServers=" + numberOfServers +
                ", isFinalState=" + isFinalState +
                '}';
    }
}
