package xyz.jamesnuge;

import fj.data.Option;
import java.util.Map;
import xyz.jamesnuge.messaging.ClientMessagingService;
import xyz.jamesnuge.scheduling.SchedulingService;
import xyz.jamesnuge.scheduling.lrr.LRRStateMachine;

import static xyz.jamesnuge.SocketClientSystemFactory.generateClientSystem;
import static xyz.jamesnuge.Util.chain;

public class Main {
    public static void main(String[] args) {
        final Option<ClientMessagingService> maybeMessagingSystem = generateClientSystem(
                "127.0.0.1",
                50000
        );
        if (maybeMessagingSystem.isSome()) {
            final SchedulingService service = new SchedulingService(
                    maybeMessagingSystem.some(),
                    Map.of("LRR", LRRStateMachine::new)
            );
            chain(
                service.scheduleJobsUsingAlgorithm("LRR"),
                    Util::printString
            );
        } else {
            System.out.println("Unable to connect to server");
        }
    }
}
