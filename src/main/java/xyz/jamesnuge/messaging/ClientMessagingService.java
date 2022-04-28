package xyz.jamesnuge.messaging;

import fj.data.Either;
import fj.data.List;
import fj.data.Option;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.jamesnuge.MessageParser;
import xyz.jamesnuge.MessageParser.InboudMessage;
import xyz.jamesnuge.MessageParser.Message;
import xyz.jamesnuge.state.ServerStateItem;

import java.util.function.Function;
import java.util.function.Supplier;

import static fj.data.Either.left;
import static fj.data.Either.right;
import static fj.data.List.list;
import static fj.data.List.nil;
import static xyz.jamesnuge.MessageParser.Message.*;
import static xyz.jamesnuge.Util.*;

public class ClientMessagingService {

    private static final Logger LOGGER = LogManager.getLogger(ClientMessagingService.class);
    private final Function<String, Either<String, String>> write;
    private final Supplier<Either<String, String>> read;
    private final Function<Integer, Either<String, List<String>>> readLines;
    private final Runnable finish;

    public ClientMessagingService(
            final Function<String, Either<String, String>> write,
            final Supplier<Either<String, String>> read,
            final Function<Integer, Either<String, List<String>>> readLines,
            final Runnable finish
    ) {
        this.write = write;
        this.read = read;
        this.readLines = readLines;
        this.finish = finish;
    }

    public Either<String, List<ServerStateItem>> getServerState(ConfigRequest configRequest) {
        final Either<String, String> initiateServerStateCall = chain(
                (s) -> sendMessage(configRequest.constructServerMessage()),
                (s) -> getMessage(InboudMessage.DATA),
                (s) -> {
                    String[] dataResults = s.split(" ");
                    return sendMessage(Message.OK).rightMap((_s) -> dataResults[1]);
                });
        LOGGER.debug("Reading server state messages");
        final Either<String, List<String>> serverStateString = flatMap(initiateServerStateCall, (s) -> {
            return readLines.apply(Integer.parseInt(s));
        });
        LOGGER.debug("Finished reading server state messages");
        final Either<String, List<ServerStateItem>> serverState = serverStateString.rightMap((s) -> s.map(ServerStateItem::parseFromString));
        LOGGER.debug("Finished converting server state messages");
        Either<String, String> okMessage = sendMessage(OK);
        if (okMessage.isLeft()) {
            return left(okMessage.left().value());
        } else {
            return flatMap(getMessage(), (_s) -> serverState);
        }
    }

//    private List<String> readMessages() {
//        Option<String> maybeMessage = toOption(getMessage());
//        return maybeMessage.isSome() ? list(maybeMessage.some()).append(readMessages()) : nil();
//    }

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