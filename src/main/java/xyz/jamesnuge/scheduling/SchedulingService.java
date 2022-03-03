package xyz.jamesnuge.scheduling;

import xyz.jamesnuge.messaging.ClientMessagingService;

public class SchedulingService {

    private final ClientMessagingService clientMessagingService;

    public SchedulingService(final ClientMessagingService clientMessagingService) {
        this.clientMessagingService = clientMessagingService;
    }

}
