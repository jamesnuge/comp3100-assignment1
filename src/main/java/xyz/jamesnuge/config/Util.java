package xyz.jamesnuge.config;

import java.util.Comparator;

public class Util {
    private Util() {}

    private static String getHighestCapacityServerType(SystemConfig config) {
        return config.servers.server.stream().max(Comparator.comparingInt(Server::getCores)).get().getType();
    }
}
