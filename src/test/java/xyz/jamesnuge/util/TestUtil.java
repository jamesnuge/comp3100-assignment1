package xyz.jamesnuge.util;

import fj.data.Either;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TestUtil {
    private TestUtil() {}


    public static <A, B> void  assertRight(final B expected, final Either<A, B> actual) {
        if (actual.isLeft()) {
            fail("Either had a left value: " + actual.left().value());
        } else {
            assertEquals(
                    expected,
                    actual.right().value()
            );
        }
    }

    public static <A, B> void  assertLeft(final A expected, final Either<A, B> actual) {
        if (actual.isRight()) {
            fail("Either had a right value: " + actual.right().value());
        } else {
            assertEquals(
                    expected,
                    actual.left().value()
            );
        }
    }
}
