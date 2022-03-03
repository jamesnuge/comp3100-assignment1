package xyz.jamesnuge.state;

import fj.data.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static xyz.jamesnuge.state.ServerState.parseServerStateFromString;
import static xyz.jamesnuge.state.ServerStateItem.ServerStatus.BOOTING;
import static xyz.jamesnuge.state.ServerStateItem.ServerStatus.INACTIVE;

public class ServerStateTest {

    @Test
    public void testParseFromString() {
        List<ServerStateItem> actual = parseServerStateFromString(
        "juju 0 booting 120 0 2500 13100 1 0\njuju 1 inactive 156 0 2500 13900 1 0"
        );
        assertEquals(
                List.list(
                        new ServerStateItem("juju", 0, BOOTING, 120L, 0, 2500L, 13100L, 1, 0),
                        new ServerStateItem("juju", 1, INACTIVE, 156L, 0, 2500L, 13900L, 1, 0)
                ),
                actual
        );
    }
}
