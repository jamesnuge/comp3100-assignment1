package xyz.jamesnuge;

import fj.Unit;
import fj.data.Option;
import xyz.jamesnuge.MessageParser.Message;
import xyz.jamesnuge.messaging.ClientMessagingSystem;

import static xyz.jamesnuge.MessageParser.Message.REDY;
import static xyz.jamesnuge.SocketClientSystemFactory.generateClientSystem;
import static xyz.jamesnuge.SystemInformationUtil.loadSystemConfig;
import static xyz.jamesnuge.Util.chain;

public class Main {
    public static void main(String[] args) {
        final Option<ClientMessagingSystem> maybeMessagingSystem = generateClientSystem("127.0.0.1", 50000);
        if (maybeMessagingSystem.isSome()) {
            maybeMessagingSystem
                    .foreach((clientSystem -> {
                        chain(
                                clientSystem.loginToServer("test"),
                                (_s) -> clientSystem.sendMessage(REDY),
                                (s) -> clientSystem.getMessage(),
//                                (_s) -> loadSystemConfig(System.getProperty("user.dir")).rightMap(Object::toString),
//                                Util::printString,
                                (_s) -> clientSystem.getServerState().rightMap(Object::toString),
                                Util::printString,
                                (s) -> clientSystem.sendMessage(Message.QUIT)
                        );
                        return Unit.unit();
                    }));
        } else {
            System.out.println("Unable to connect to server");
        }
    }
}
