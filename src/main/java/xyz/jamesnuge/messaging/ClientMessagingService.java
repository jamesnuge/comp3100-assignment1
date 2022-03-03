package xyz.jamesnuge.messaging;

import fj.data.Either;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import xyz.jamesnuge.MessageParser;
import xyz.jamesnuge.MessageParser.InboudMessage;
import xyz.jamesnuge.MessageParser.Message;
import xyz.jamesnuge.state.ServerState;
import xyz.jamesnuge.state.ServerStateItem;

import static xyz.jamesnuge.MessageParser.Message.HELO;
import static xyz.jamesnuge.MessageParser.Message.PSHJ;
import static xyz.jamesnuge.MessageParser.Message.QUIT;
import static xyz.jamesnuge.MessageParser.Message.REDY;
import static xyz.jamesnuge.MessageParser.Message.SCHD;
import static xyz.jamesnuge.Util.chain;

public class ClientMessagingService {

    private final Function<String, Either<String, String>> write;
    private final Supplier<Either<String, String>> read;

    public ClientMessagingService(final Function<String, Either<String, String>> write, final Supplier<Either<String, String>> read) {
        this.write = write;
        this.read = read;
    }

    public Either<String, List<ServerStateItem>> getServerState(ConfigRequest configRequest) {
        return chain(
                (s) -> sendMessage(configRequest.constructServerMessage()),
                (s) -> getMessage(InboudMessage.DATA),
                (s) -> sendMessage(Message.OK),
                (s) -> getMessage()
        ).rightMap(ServerState::parseServerStateFromString);
    }

    public Either<String, List<ServerStateItem>> getServerState() {
        return getServerState(ConfigRequest.WHOLE_SYSTEM_REQUEST);
    }

    public Either<String, String> loginToServer(String userName) {
        return chain(
                (_s) -> sendMessage(HELO),
                (s) -> getMessage(Message.OK),
                (s) -> sendMessage("AUTH " + userName),
                (s) -> getMessage(Message.OK)
        );
    }

    public Either<String, String> beginScheduling() {
        return sendMessage(REDY);
    }

    public Either<String, String> scheduleJob(Integer jobId, String serverType, Integer serverId) {
        return sendMessage(SCHD.name() + " " + jobId + " " + serverType + " " + serverId);
    }

    public Either<String, String> pushJob() {
        return sendMessage(PSHJ);
    }

    Either<String, String> sendMessage(Message message) {
        return this.sendMessage(message.name());
    }

    Either<String, String> sendMessage(String message) {
        return MessageParser.sendMessage(write, message);
    }

    Either<String, String> getMessage() {
        return MessageParser.getMessage(read);
    }

    Either<String, String> getMessage(Message message) {
        return MessageParser.getMessage(read, message.name());
    }

    Either<String, String> getMessage(InboudMessage message) {
        return MessageParser.getMessage(read, message.name());
    }

    public Either<String, String> quit() {
        return sendMessage(QUIT);
    }
}
