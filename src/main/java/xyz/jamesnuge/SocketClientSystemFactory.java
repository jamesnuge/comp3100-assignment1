package xyz.jamesnuge;

import fj.data.Either;
import fj.data.Option;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.jamesnuge.messaging.ClientMessagingService;

import java.io.*;
import java.net.Socket;

public class SocketClientSystemFactory {

    private static final Logger LOGGER = LogManager.getLogger(SocketClientSystemFactory.class);

    private SocketClientSystemFactory() {
    }

    // This factory is used to extract away the socket/stream details from the messaging service.
    // The service only needs to know how to send and read messages, along with closing.
    public static Option<ClientMessagingService> generateClientSystem(String host, Integer port) {
        LOGGER.info("Connecting to server...");
        try {
            final Socket socket = new Socket(host, port);
            LOGGER.info("Connected to server");
            final InputStream is = socket.getInputStream();
            final OutputStream os = socket.getOutputStream();
            final PrintWriter writer = new PrintWriter(os);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return Option.some(new ClientMessagingService(
                    // Write abstraction
                    (s) -> {
                        writer.println(s);
                        writer.flush();
                        return Either.right("Written message: " + s);
                    },
                    // Read abstraction
                    () -> {
                        try {
                            if (reader.ready()) {
                                return Either.right(reader.readLine());
                            } else {
                                return Either.left("Unable to read socket: Reader not ready");
                            }
                        } catch (IOException e) {
                            return Either.left(e.getMessage());
                        }
                    },
                    // Close function
                    () -> {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            LOGGER.error("Unable to close socket: ", e);
                        }
                    }
            ));
        } catch (IOException e) {
            return Option.none();
        }
    }
}
