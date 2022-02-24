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
        JOBN
    }
    public static Either<String, String> getMessage(final BufferedReader reader, final Message message) {
        return tryUntil(Duration.of(1, ChronoUnit.SECONDS), () -> {
            try {
                if (reader.ready()) {
                    final String receivedMessage = reader.readLine();
                    if (receivedMessage.startsWith(message.name())) {
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
        if (writer.checkError()) {
            return Either.left("Write stream had an error");
        }
        writer.println(message);
        writer.flush();
        return Either.right("Sent message");
    }

    public static Either<String, String> sendMessage(final PrintWriter writer, Enum message) {
        if (writer.checkError()) {
            return Either.left("Write stream had an error");
        }
        writer.println(message.name());
        writer.flush();
        return Either.right("Sent message");
    }
}
