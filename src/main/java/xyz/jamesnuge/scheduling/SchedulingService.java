package xyz.jamesnuge.scheduling;

import xyz.jamesnuge.messaging.ClientMessagingSystem;

public class SchedulingService {

    private final ClientMessagingSystem clientMessagingSystem;

    public SchedulingService(final ClientMessagingSystem clientMessagingSystem) {
        this.clientMessagingSystem = clientMessagingSystem;
    }

}
