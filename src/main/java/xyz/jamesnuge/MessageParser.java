package xyz.jamesnuge;

import fj.data.Either;
import fj.data.Option;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static xyz.jamesnuge.Util.tryUntil;

public class MessageParser {
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

    public static Either<String, String> getMessage(final BufferedReader reader) {
        return tryUntil(Duration.of(1, ChronoUnit.SECONDS), () -> {
            try {
                if (reader.ready()) {
                    final String receivedMessage = reader.readLine();
                    System.out.println(receivedMessage);
                    return Option.some(receivedMessage);
                } else {
                    return Option.none();
                }
            } catch (IOException e) {
                return Option.none();
            }
        });
    }


    public static Either<String, String> getMessage(final BufferedReader reader, final String message) {
        return tryUntil(Duration.of(1, ChronoUnit.SECONDS), () -> {
            try {
                if (reader.ready()) {
                    final String receivedMessage = reader.readLine();
                    if (receivedMessage.startsWith(message)) {
                        System.out.println(receivedMessage);
                        return Option.some(receivedMessage);
                    } else {
                        return Option.none();
                    }
                } else {
                    return Option.none();
                }
            } catch (IOException e) {
                return Option.none();
            }
        });
    }

    public static Either<String, String> sendMessage(final PrintWriter writer, String message) {
        writer.println(message);
        writer.flush();
        System.out.println("Sent message: " + message);
        return Either.right("Sent message");
    }

    public static Either<String, String> sendMessage(final PrintWriter writer, Enum message) {
        return sendMessage(writer, message.name());
    }
}
