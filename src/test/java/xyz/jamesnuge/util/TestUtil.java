package xyz.jamesnuge.util;

import fj.data.Either;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TestUtil {
    private TestUtil() {}


    public static <A, B> void  assertRight(B expected, Either<A, B> actual) {
        if (actual.isLeft()) {
            fail("Either had a left value: " + actual.left().value());
        } else {
            assertEquals(
                    expected,
                    actual.right().value()
            );
        }
    }
}
