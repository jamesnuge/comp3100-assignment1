package xyz.jamesnuge;

import fj.data.Either;
import fj.data.Option;
import xyz.jamesnuge.messaging.ClientMessagingService;
import xyz.jamesnuge.scheduling.SchedulingService;
import xyz.jamesnuge.scheduling.StateConfigurationFactory;
import xyz.jamesnuge.scheduling.StateMachineFactory;
import xyz.jamesnuge.scheduling.firstCapable.FcFactory;
import xyz.jamesnuge.scheduling.lrr.LRRInternalState;
import xyz.jamesnuge.scheduling.lrr.LrrFactory;

import java.util.Map;

import static xyz.jamesnuge.SocketClientSystemFactory.generateClientSystem;
import static xyz.jamesnuge.Util.chain;
import static xyz.jamesnuge.Util.mapOf;

public class Main {
    public static void main(String[] args) {
        if (args.length >= 1) {
            String algorithm = args[0];
            final Option<ClientMessagingService> maybeMessagingSystem = generateClientSystem(
                    "127.0.0.1",
                    50000
            );
            if (maybeMessagingSystem.isSome()) {
                Map<String, Pair<StateMachineFactory<?>, StateConfigurationFactory<?>>> configMap = mapOf("LRR", Pair.of(LrrFactory.STATE_MACHINE, LrrFactory.CONFIGURATION));
                configMap.put("FC", Pair.of(FcFactory.STATE_MACHINE, FcFactory.CONFIGURATION));
                final SchedulingService service = new SchedulingService(
                        maybeMessagingSystem.some(),
                        configMap
                );
                final Either<String, String> result = chain(
                        service.scheduleJobsUsingAlgorithm(algorithm),
                        Util::printString
                );
                if (result.isLeft()) {
                    System.out.println("Could not schedule jobs using algorithm " + algorithm + ": " + result.left().value());
                }
            } else {
                System.out.println("Unable to connect to server");
            }
        } else {
            System.out.println("Could not run scheduler: no algorithm provided");
        }
    }
}
