package xyz.jamesnuge.scheduling.lrr;

import java.util.Objects;
import java.util.function.BiFunction;
import xyz.jamesnuge.scheduling.State;

public class LRRInternalState implements State {

    private final Long lastAssignedServerId;
    private final String serverType;
    private final Integer numberOfServers;
    private final Boolean isFinalState;

    public static BiFunction<Long, Integer, LRRInternalState> createInternalStateFactory(String serverType) {
        return (Long id, Integer numberOfServers) -> new LRRInternalState(id, serverType, numberOfServers, false);
    }

    public static BiFunction<Long, Integer, LRRInternalState> createFinalInternalStateFactory(String serverType) {
        return (Long id, Integer numberOfServers) -> new LRRInternalState(id, serverType, numberOfServers, true);
    }


    private LRRInternalState(Long lastAssignedServerId, String serverType, Integer numberOfServers, Boolean isFinalState) {
        this.lastAssignedServerId = lastAssignedServerId;
        this.serverType = serverType;
        this.numberOfServers = numberOfServers;
        this.isFinalState = isFinalState;
    }

    public Long getLastAssignedServerId() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LRRInternalState that = (LRRInternalState) o;
        return Objects.equals(lastAssignedServerId, that.lastAssignedServerId) && Objects.equals(serverType, that.serverType) && Objects.equals(numberOfServers, that.numberOfServers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastAssignedServerId, serverType, numberOfServers);
    }

    @Override
    public String toString() {
        return "LRRInternalState{" +
                "lastAssignedServerId=" + lastAssignedServerId +
                ", serverType='" + serverType + '\'' +
                ", numberOfServers=" + numberOfServers +
                '}';
    }

}
