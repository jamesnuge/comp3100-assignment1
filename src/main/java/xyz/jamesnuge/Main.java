package xyz.jamesnuge;

import fj.Unit;
import fj.data.Either;
import xyz.jamesnuge.MessageParser.Message;
import xyz.jamesnuge.config.SystemConfig;

import static xyz.jamesnuge.MessageParser.InboudMessage.*;
import static xyz.jamesnuge.MessageParser.Message.*;
import static xyz.jamesnuge.MessageParser.Message.GETS;
import static xyz.jamesnuge.MessageParser.Message.HELO;
import static xyz.jamesnuge.SocketClientSystemFactory.generateClientSystem;
import static xyz.jamesnuge.Util.chain;


public class Main {
    public static void main(String[] args) {
        generateClientSystem("127.0.0.1", 50000)
                .foreach((clientSystem -> {
                    final Either<String, String> result = chain(
                            (_s) -> clientSystem.sendMessage(HELO),
                            (s) -> clientSystem.getMessage(Message.OK),
                            (s) -> clientSystem.sendMessage("AUTH test"),
                            (s) -> clientSystem.getMessage(Message.OK)
                    );
                    final Either<String, SystemConfig> systemConfig = SystemInformationUtil.loadSystemConfig(System.getProperty("user.dir"));
                    chain(
                            (_s) -> clientSystem.sendMessage(REDY),
                            (s) -> clientSystem.getMessage(),
                            (s) -> clientSystem.sendMessage("GETS All"),
                            (s) -> clientSystem.getMessage(DATA),
                            (s) -> clientSystem.sendMessage(Message.OK),
                            (s) -> clientSystem.getMessage(),
                            Util::printString,
                            (s) -> clientSystem.sendMessage(Message.QUIT)
                    );
                    return Unit.unit();
                }));
    }
}
