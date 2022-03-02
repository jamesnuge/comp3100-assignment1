package xyz.jamesnuge.messaging;

public class ConfigRequest implements ToServerMessage {
    public static final ConfigRequest WHOLE_SYSTEM_REQUEST = new ConfigRequest("All", -1, -1);
    private final String serverType;
    private final Integer cores;
    private final Integer memory;

    private ConfigRequest(
            final String serverType,
            final Integer cores,
            final Integer memory
    ) {
        this.serverType = serverType;
        this.cores = cores;
        this.memory = memory;
    }

    @Override
    public String constructServerMessage() {
        return " " + serverType;
    }
}
