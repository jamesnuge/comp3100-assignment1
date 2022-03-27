package xyz.jamesnuge.messaging;

import static fj.data.Either.left;
import static fj.data.Either.right;
import static fj.data.List.list;
import static fj.data.List.nil;
import static xyz.jamesnuge.MessageParser.Message.HELO;
import static xyz.jamesnuge.MessageParser.Message.OK;
import static xyz.jamesnuge.MessageParser.Message.PSHJ;
import static xyz.jamesnuge.MessageParser.Message.QUIT;
import static xyz.jamesnuge.MessageParser.Message.REDY;
import static xyz.jamesnuge.MessageParser.Message.SCHD;
import static xyz.jamesnuge.Util.chain;
import static xyz.jamesnuge.Util.toOption;

import java.util.function.Function;
import java.util.function.Supplier;

import fj.data.Either;
import fj.data.List;
import fj.data.Option;
import xyz.jamesnuge.MessageParser;
import xyz.jamesnuge.MessageParser.InboudMessage;
import xyz.jamesnuge.MessageParser.Message;
import xyz.jamesnuge.state.ServerStateItem;

public class ClientMessagingService {

    private final Function<String, Either<String, String>> write;
    private final Supplier<Either<String, String>> read;
    private final Runnable finish;

    public ClientMessagingService(final Function<String, Either<String, String>> write,
                                  final Supplier<Either<String, String>> read, final Runnable finish) {
        this.write = write;
        this.read = read;
        this.finish = finish;
    }

    public Either<String, List<ServerStateItem>> getServerState(ConfigRequest configRequest) {
        final Either<String, String> chain = chain(
                (s) -> sendMessage(configRequest.constructServerMessage()),
                (s) -> getMessage(InboudMessage.DATA),
                (s) -> sendMessage(Message.OK));
        final Either<String, List<ServerStateItem>> serverState = chain
                .rightMap((s) -> readMessages().map(ServerStateItem::parseFromString));
        Either<String, String> okMessage = sendMessage(OK);
        if (okMessage.isLeft()) {
            return left(okMessage.left().value());
        } else {
            return serverState;
        }
    }

    private List<String> readMessages() {
        Option<String> maybeMessage = toOption(getMessage());
        return maybeMessage.isSome() ? list(maybeMessage.some()).append(readMessages()) : nil();
    }

    public Either<String, List<ServerStateItem>> getServerState() {
        return getServerState(ConfigRequest.WHOLE_SYSTEM_REQUEST);
    }

    public Either<String, String> loginToServer(String userName) {
        return chain(
                (_s) -> sendMessage(HELO),
                (s) -> getMessage(Message.OK),
                (s) -> sendMessage("AUTH " + userName),
                (s) -> getMessage(Message.OK));
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

    public Either<String, String> signalRedy() {
        return sendMessage(REDY);
    }

    public Either<String, String> getMessage() {
        return MessageParser.getMessage(read);
    }

    public Either<String, String> quit() {
        return chain(
                sendMessage(QUIT),
                (s) -> getMessage(QUIT),
                (_s) -> {
                    finish.run();
                    return right("Successfully closed ClientMessagingService");
                }
        );
    }

    Either<String, String> sendMessage(Message message) {
        return this.sendMessage(message.name());
    }

    Either<String, String> sendMessage(String message) {
        return MessageParser.sendMessage(write, message);
    }

    Either<String, String> getMessage(Message message) {
        return MessageParser.getMessage(read, message.name());
    }

    Either<String, String> getMessage(InboudMessage message) {
        return MessageParser.getMessage(read, message.name());
    }

}