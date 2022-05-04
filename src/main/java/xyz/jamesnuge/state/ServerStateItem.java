package xyz.jamesnuge.state;

import java.util.Objects;

public class ServerStateItem {

    public enum ServerStatus {
        INACTIVE, BOOTING, IDLE, ACTIVE, UNAVAILABLE
    }

    public static ServerStateItem parseFromString(final String rawString) {
        final String[] individualItems = rawString.split(" ");
        return new ServerStateItem(
                individualItems[0],
                Integer.valueOf(individualItems[1]),
                ServerStatus.valueOf(individualItems[2].toUpperCase()),
                Long.valueOf(individualItems[3]),
                Integer.valueOf(individualItems[4]),
                Long.valueOf(individualItems[5]),
                Long.valueOf(individualItems[6]),
                Integer.valueOf(individualItems[7]),
                Integer.valueOf(individualItems[8])
        );
    }

    private final String type;
    private final Integer id;
    private final ServerStatus stats;
    private final Long currentStartTime;
    private final Integer cores;
    private final Long memory;
    private final Long disk;
    private final Integer waitingJobs;
    private final Integer runningJobs;

public ServerStateItem(
            final String type,
            final Integer id,
            final ServerStatus stats,
            final Long currentStartTime,
            final Integer cores,
            final Long memory,
            final Long disk,
            final Integer waitingJobs,
            final Integer runningJobs
    ) {
        this.type = type;
        this.id = id;
        this.stats = stats;
        this.currentStartTime = currentStartTime;
        this.cores = cores;
        this.memory = memory;
        this.disk = disk;
        this.waitingJobs = waitingJobs;
        this.runningJobs = runningJobs;
    }

    public String getType() {
        return type;
    }

    public Integer getId() {
        return id;
    }

    public ServerStatus getStats() {
        return stats;
    }

    public Long getCurrentStartTime() {
        return currentStartTime;
    }

    public Integer getCores() {
        return cores;
    }

    public Long getMemory() {
        return memory;
    }

    public Long getDisk() {
        return disk;
    }

    public Integer getWaitingJobs() {
        return waitingJobs;
    }

    public Integer getRunningJobs() {
        return runningJobs;
    }

    public boolean hasNoJobs() {
        return this.runningJobs == 0 && this.waitingJobs == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerStateItem that = (ServerStateItem) o;
        return Objects.equals(type, that.type) && Objects.equals(id, that.id) && stats == that.stats && Objects.equals(currentStartTime, that.currentStartTime) && Objects.equals(cores, that.cores) && Objects.equals(memory, that.memory) && Objects.equals(disk, that.disk) && Objects.equals(waitingJobs, that.waitingJobs) && Objects.equals(runningJobs, that.runningJobs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id, stats, currentStartTime, cores, memory, disk, waitingJobs, runningJobs);
    }

    @Override
    public String toString() {
        return "ServerStateItem{" +
                "type='" + type + '\'' +
                ", id=" + id +
                ", stats=" + stats +
                ", currentStartTime=" + currentStartTime +
                ", core=" + cores +
                ", memory=" + memory +
                ", disk=" + disk +
                ", waitingJobs=" + waitingJobs +
                ", runningJobs=" + runningJobs +
                '}';
    }
}
