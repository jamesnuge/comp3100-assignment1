package xyz.jamesnuge;

import fj.data.Either;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import xyz.jamesnuge.MessageParser.InboudMessage;
import xyz.jamesnuge.MessageParser.Message;
import xyz.jamesnuge.state.ServerState;
import xyz.jamesnuge.state.ServerStateItem;

import static xyz.jamesnuge.Util.chain;

public class ClientMessagingSystem {

    private final Function<String, Either<String, String>> write;
    private final Supplier<Either<String, String>> read;

    ClientMessagingSystem(final Function<String, Either<String, String>> write, final Supplier<Either<String, String>> read) {
        this.write = write;
        this.read = read;
    }

    public Either<String, List<ServerStateItem>> getServerState() {
        return chain(
                (s) -> sendMessage(Message.GETS),
                (s) -> getMessage(InboudMessage.DATA),
                (s) -> sendMessage(Message.OK),
                (s) -> getMessage()
        ).rightMap(ServerState::parseServerStateFromString);
    }

    public Either<String, String> sendMessage(Message message) {
        return this.sendMessage(message.name());
    }

    public Either<String, String> sendMessage(String message) {
        return MessageParser.sendMessage(write, message);
    }

    public Either<String, String> getMessage() {
        return MessageParser.getMessage(read);
    }

    public Either<String, String> getMessage(Message message) {
        return MessageParser.getMessage(read, message.name());
    }

    public Either<String, String> getMessage(InboudMessage message) {
        return MessageParser.getMessage(read, message.name());
    }

}
