package xyz.jamesnuge;

import fj.Unit;
import fj.data.Option;
import xyz.jamesnuge.messaging.ClientMessagingService;

import static xyz.jamesnuge.SocketClientSystemFactory.generateClientSystem;
import static xyz.jamesnuge.Util.chain;

public class Main {
    public static void main(String[] args) {
        final Option<ClientMessagingService> maybeMessagingSystem = generateClientSystem("127.0.0.1", 50000);
        if (maybeMessagingSystem.isSome()) {
            maybeMessagingSystem
                    .foreach((clientSystem -> {
                        chain(
                                clientSystem.loginToServer("test"),
                                (_s) -> clientSystem.beginScheduling(),
                                (_s) -> clientSystem.getServerState().rightMap(Object::toString),
                                Util::printString,
                                (s) -> clientSystem.quit()
                        );
                        return Unit.unit();
                    }));
        } else {
            System.out.println("Unable to connect to server");
        }
    }
}
