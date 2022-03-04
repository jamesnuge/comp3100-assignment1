package xyz.jamesnuge.scheduling.lrr;

import fj.data.List;
import fj.function.Try3;
import java.util.Objects;
import xyz.jamesnuge.scheduling.State;

import static fj.data.List.nil;

public class LRRInternalState implements State {

    public static final LRRInternalState INITIAL_STATE = new LRRInternalState(-1, "", -1, nil(), false);

    private final Integer lastAssignedServerId;
    private final String serverType;
    private final Integer numberOfServers;
    //TODO: remove this
    private final List<Integer> unavailableServers;
    private final Boolean isFinalState;

    public static Try3<Integer, Integer, List<Integer>, LRRInternalState, Exception> createInternalStateFactory(String serverType) {
        return (Integer id, Integer numberOfServers, List<Integer> unavailable) -> new LRRInternalState(id, serverType, numberOfServers, unavailable, false);
    }

    public static Try3<Integer, Integer, List<Integer>, LRRInternalState, Exception> createFinalInternalStateFactory(String serverType) {
        return (Integer id, Integer numberOfServers, List<Integer> list) -> new LRRInternalState(id, serverType, numberOfServers, list, true);
    }


    private LRRInternalState(Integer lastAssignedServerId, String serverType, Integer numberOfServers, List<Integer> unavailableServers, Boolean isFinalState) {
        this.lastAssignedServerId = lastAssignedServerId;
        this.serverType = serverType;
        this.numberOfServers = numberOfServers;
        this.unavailableServers = unavailableServers;
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

    public List<Integer> getUnavailableServers() {
        return unavailableServers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LRRInternalState that = (LRRInternalState) o;
        return Objects.equals(lastAssignedServerId, that.lastAssignedServerId) && Objects.equals(serverType, that.serverType) && Objects.equals(numberOfServers, that.numberOfServers) && Objects.equals(unavailableServers, that.unavailableServers) && Objects.equals(isFinalState, that.isFinalState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastAssignedServerId, serverType, numberOfServers, unavailableServers, isFinalState);
    }

    @Override
    public String toString() {
        return "LRRInternalState{" +
                "lastAssignedServerId=" + lastAssignedServerId +
                ", serverType='" + serverType + '\'' +
                ", numberOfServers=" + numberOfServers +
                ", unavailableServers=" + unavailableServers +
                ", isFinalState=" + isFinalState +
                '}';
    }
}
