package xyz.jamesnuge;

import fj.data.Either;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Supplier;
import xyz.jamesnuge.MessageParser.Message;

import static fj.data.Either.left;
import static fj.data.Either.right;

public class MessageSystem {

    private static final Either<String, String> UNITILIAZED_LEFT = left("Message system uninitialized");

    private Boolean initialized = false;
    private PrintWriter writer;
    private BufferedReader reader;

    private final String host;
    private final Integer port;

    MessageSystem(final String host, final Integer port) {
        this.host = host;
        this.port = port;
    }

    public Either<String, String> init() {
        System.out.println("Connect to server...");
        try {
            final Socket socket = new Socket(host, port);
            System.out.println("Connected to server");
            final InputStream is = socket.getInputStream();
            final OutputStream os = socket.getOutputStream();
            writer = new PrintWriter(os);
            reader = new BufferedReader(new InputStreamReader(is));
            initialized = true;
            return right("Initialised system");
        } catch (IOException e) {
            return left("Unable to initialize message system: " + e.getMessage());
        }
    }

    public Either<String, String> sendMessage(Message message) {
        return this.sendMessage(message.name());
    }

    public Either<String, String> sendMessage(String message) {
        return ifInitialized(() -> MessageParser.sendMessage(writer, message));
    }

    public Either<String, String> getMessage(Message message) {
        return ifInitialized(() -> MessageParser.getMessage(reader, message));
    }

    private Either<String, String> ifInitialized(Supplier<Either<String, String>> supplier) {
        if (initialized) {
            return supplier.get();
        } else {
            return UNITILIAZED_LEFT;
        }
    }
}
