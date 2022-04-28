package xyz.jamesnuge;

import fj.data.Either;
import fj.data.List;
import fj.data.Option;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.jamesnuge.messaging.ClientMessagingService;

import java.io.*;
import java.net.Socket;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static fj.data.Either.left;
import static fj.data.Either.right;
import static fj.data.List.list;
import static fj.data.List.nil;

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
                        return right("Written message: " + s);
                    },
                    // Read abstraction
                    () -> {
                        try {
                            if (reader.ready()) {
                                return right(reader.readLine());
                            } else {
                                return left("Unable to read socket: Reader not ready");
                            }
                        } catch (IOException e) {
                            return left(e.getMessage());
                        }
                    },
                    // Read lines abstraction
                    (numOfLines) -> {
                        List<String> readLines = nil();
                        int count = 0;
                        while (count < numOfLines) {
                            try {
                                String line = reader.readLine();
                                if (line == null) break;
                                LOGGER.info("Read message: " + line);
                                readLines = readLines.append(list(line));
                                count++;
                            } catch (IOException e) {
                                return left(e.getMessage());
                            }
                        }
                        return right(readLines);
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
