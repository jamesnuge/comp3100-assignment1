package xyz.jamesnuge;

import fj.data.Either;
import java.util.List;
import xyz.jamesnuge.MessageParser.Message;

import static xyz.jamesnuge.MessageParser.InboudMessage.*;
import static xyz.jamesnuge.MessageParser.Message.*;
import static xyz.jamesnuge.MessageParser.Message.GETS;
import static xyz.jamesnuge.MessageParser.Message.HELO;
import static xyz.jamesnuge.Util.chain;


public class Main {
    public static void main(String[] args) {
        final MessageSystem messageSystem = new MessageSystem("127.0.0.1", 50000);
            final Either<String, String> result = chain(
                    messageSystem.init(),
                    List.of(
                            (s) -> messageSystem.sendMessage(HELO),
                            (s) -> messageSystem.getMessage(Message.OK),
                            (s) -> messageSystem.sendMessage("AUTH test"),
                            (s) -> messageSystem.getMessage(Message.OK)
                    )
            );
        final SchedulingService schedulingService = new SchedulingService(System.getProperty("user.dir"));
        schedulingService.init();
        chain(
                messageSystem.sendMessage(REDY),
                List.of(
                        (s) -> messageSystem.getMessage(),
                        (s) -> messageSystem.sendMessage(GETS),
                        (s) -> messageSystem.getMessage(DATA),
                        Util::printString,
                        (s) -> messageSystem.sendMessage(Message.QUIT)
                )
        );
    }
}
