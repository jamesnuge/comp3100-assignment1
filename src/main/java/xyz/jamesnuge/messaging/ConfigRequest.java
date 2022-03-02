package xyz.jamesnuge.messaging;

import static xyz.jamesnuge.MessageParser.Message.GETS;

public class ConfigRequest implements ToServerMessage {

    public static final ConfigRequest WHOLE_SYSTEM_REQUEST = new ConfigRequest("All", -1, -1L, -1L);

    private final String serverType;
    private final Integer cores;
    private final Long memory;
    private final Long disk;

    private ConfigRequest(
            final String serverType,
            final Integer cores,
            final Long memory,
            final Long disk
    ) {
        this.serverType = serverType;
        this.cores = cores;
        this.memory = memory;
        this.disk = disk;
    }

    public static ConfigRequest createServerTypeConfigRequest(final String serverType) {
        return new ConfigRequest("Type " + serverType, -1, -1L, -1L);
    }

    public static ConfigRequest createCapableTypeConfigRequest(
            final Integer cores,
            final Long memory,
            final Long disk
    ) {
        return new ConfigRequest("Capable", cores, memory, disk);
    }

    public static ConfigRequest createAvailTypeConfigRequest(
            final Integer cores,
            final Long memory,
            final Long disk
    ) {
        return new ConfigRequest("Avail", cores, memory, disk);
    }

    @Override
    public String constructServerMessage() {
        return GETS.name() + " " + serverType +
                (cores != -1 ? " " + cores : "") +
                (memory != -1 ? " " + memory : "") +
                (disk != -1 ? " " + disk : "");
    }
}
