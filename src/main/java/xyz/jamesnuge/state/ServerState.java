package xyz.jamesnuge.state;

import fj.data.Collectors;
import fj.data.List;

import java.util.stream.Stream;

public class ServerState {

    private ServerState() {}

    public static List<ServerStateItem> parseServerStateFromString(final String rawString) {
        return Stream.of(rawString.split("\n"))
                .map(ServerStateItem::parseFromString)
                .collect(Collectors.toList());
    }

}

