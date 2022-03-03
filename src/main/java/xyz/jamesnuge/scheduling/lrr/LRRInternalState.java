package xyz.jamesnuge.scheduling.lrr;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import xyz.jamesnuge.scheduling.State;

import static java.util.Collections.emptyList;

public class LRRInternalState implements State {

    private final Integer lastAssignedServerId;
    private final String serverType;
    private final Integer numberOfServers;
    private final List<Integer> unavailableServers;
    private final Boolean isFinalState;

    public static BiFunction<Integer, Integer, LRRInternalState> createInternalStateFactory(String serverType) {
        return (Integer id, Integer numberOfServers) -> new LRRInternalState(id, serverType, numberOfServers, emptyList(), false);
    }

    public static BiFunction<Integer, Integer, LRRInternalState> createFinalInternalStateFactory(String serverType) {
        return (Integer id, Integer numberOfServers) -> new LRRInternalState(id, serverType, numberOfServers, emptyList(), true);
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
