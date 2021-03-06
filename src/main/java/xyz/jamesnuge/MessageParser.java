package xyz.jamesnuge;

import fj.data.Either;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static fj.data.Either.right;
import static xyz.jamesnuge.Util.*;

public class MessageParser {

    public static Logger LOGGER = LogManager.getLogger(MessageParser.class);

    public static enum Message {
        HELO,
        OK,
        REDY,
        GETS,
        SCHD,
        CNTJ,
        EJWT,
        LSTJ,
        PSHJ,
        MIGJ,
        KILJ,
        TERM,
        QUIT
    }

    public static enum InboudMessage {
        OK,
        DATA,
        JOBN,
        JOBP,
        JCPL,
        RESF,
        RESR,
        NONE,
        QUIT
    }

    public static Either<String, String> getMessage(final Supplier<Either<String, String>> read) {
        return getMessage(read, (s) -> true);
    }

    public static Either<String, String> getMessage(final Supplier<Either<String, String>> read, final String matchingString) {
        return getMessage(read, (s) -> s.contains(matchingString));
    }

    public static Either<String, String> getMessage(final Supplier<Either<String, String>> read, final Predicate<String> predicate) {
        return chain(tryUntil(Duration.of(1, ChronoUnit.SECONDS), () -> toOption(
                flatMap(
                        read.get(),
                        match(predicate)
                )
        )), MessageParser::printString);
    }

    public static Either<String, String> sendMessage(final Function<String, Either<String, String>> write, String message) {
        LOGGER.info("Sent message: " + message);
        return write.apply(message);
    }

    public static Either<String, String> sendMessage(final Function<String, Either<String, String>> write, Message message) {
        return sendMessage(write, message.name());
    }

    private static Either<String, String> printString(String s) {
        LOGGER.debug(s);
        return right(s);
    }
}
