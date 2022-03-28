package xyz.jamesnuge.messaging;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class NewJobRequest {
    public final Integer jobId;
    public final Integer submitTime;
    public final Long estimatedRunTime;
    public final Integer core;
    public final Long memory;
    public final Long disk;

    public static NewJobRequest parseFromJOBNMessage(String message) {
        List<String> list = Arrays.asList(message.split(" "));
        return new NewJobRequest(
                Integer.parseInt(list.get(2)),
                Integer.parseInt(list.get(1)),
                Long.parseLong(list.get(3)),
                Integer.parseInt(list.get(4)),
                Long.parseLong(list.get(5)),
                Long.parseLong(list.get(6))
        );
    }

    public NewJobRequest(
            final Integer jobId,
            final Integer submitTime,
            final Long estimatedRunTime,
            final Integer core,
            final Long memory,
            final Long disk
    ) {
        this.jobId = jobId;
        this.submitTime = submitTime;
        this.estimatedRunTime = estimatedRunTime;
        this.core = core;
        this.memory = memory;
        this.disk = disk;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewJobRequest that = (NewJobRequest) o;
        return Objects.equals(jobId, that.jobId) && Objects.equals(submitTime, that.submitTime) && Objects.equals(estimatedRunTime, that.estimatedRunTime) && Objects.equals(core, that.core) && Objects.equals(memory, that.memory) && Objects.equals(disk, that.disk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobId, submitTime, estimatedRunTime, core, memory, disk);
    }

    @Override
    public String toString() {
        return "NewJobRequest{" +
                "jobId=" + jobId +
                ", submitTime=" + submitTime +
                ", estimatedRunTime=" + estimatedRunTime +
                ", core=" + core +
                ", memory=" + memory +
                ", disk=" + disk +
                '}';
    }
}
