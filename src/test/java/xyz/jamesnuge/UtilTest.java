package xyz.jamesnuge;

import org.junit.jupiter.api.Test;

import static xyz.jamesnuge.Util.tryEither;
import static xyz.jamesnuge.util.TestUtil.assertLeft;
import static xyz.jamesnuge.util.TestUtil.assertRight;

public class UtilTest {

    @Test
    public void tryEitherShouldReturnRightOnSuccess() {
        assertRight(
                1,
            tryEither(() -> 1)
        );
    }

    @Test
    public void tryEitherShouldReturnLeftOnException() throws Exception {
        assertLeft(
                "message",
                tryEither(() -> {
                    throw new Exception("message");
                })
        );
    }
}
