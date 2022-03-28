package xyz.jamesnuge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import fj.data.Either;
import fj.data.Option;
import xyz.jamesnuge.messaging.ClientMessagingService;

public class SocketClientSystemFactory {

    private SocketClientSystemFactory() {
    }

    // This factory is used to extract away the socket/stream details from the messaging service.
    // The service only needs to know how to send and read messages, along with closing.
    public static Option<ClientMessagingService> generateClientSystem(String host, Integer port) {
        System.out.println("Connecting to server...");
        try {
            final Socket socket = new Socket(host, port);
            System.out.println("Connected to server");
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
                            System.out.println("Unable to close socket: " + e.getMessage());
                        }
                    }
            ));
        } catch (IOException e) {
            return Option.none();
        }
    }
}
