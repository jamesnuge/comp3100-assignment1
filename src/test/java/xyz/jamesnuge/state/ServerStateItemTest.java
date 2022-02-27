package xyz.jamesnuge.state;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static xyz.jamesnuge.state.ServerStateItem.ServerStatus.INACTIVE;

class ServerStateItemTest {

    @Test
    public void testParseFromString() throws Exception {
        final ServerStateItem actual = ServerStateItem.parseFromString("super-silk 0 inactive -1 16 64000 512000 0 0");
        assertEquals(
                actual,
                new ServerStateItem(
                        "super-silk",
                        0,
                        INACTIVE,
                        -1L,
                        16,
                        64000L,
                        512000L,
                        0,
                        0
                )
        );
    }
}