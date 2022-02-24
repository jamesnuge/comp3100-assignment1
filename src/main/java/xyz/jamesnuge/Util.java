package xyz.jamesnuge;

import fj.data.Either;
import fj.data.Option;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.time4j.SystemClock;
import net.time4j.TemporalType;

public class Util {

    // TODO: Add timeout + timeout exception
    public static void waitForMessage(final BufferedReader reader, Consumer<String> consumer) throws IOException, InterruptedException {
        while(!reader.ready()) {
            Thread.sleep(100);
        }
        final String line = reader.readLine();
        consumer.accept(line);
    }

    public static <T> T waitForMessage(final BufferedReader reader, final Function<String, T> function) throws IOException {
        while(!reader.ready()) {
            return function.apply(reader.readLine());
        }
        return null;
    }

    public static <B> Either<String, B> tryUntil(final Duration duration, final Supplier<Option<B>> supplier) {
        final Clock clock = TemporalType.CLOCK.from(SystemClock.MONOTONIC);
        final Instant endTime = clock.instant().plus(duration.toMillis(), ChronoUnit.MILLIS);
        while ((clock.instant().isBefore(endTime))) {
            final Option<B> option = supplier.get();
            if (option.isSome()) {
                return option.toEither("Did not get correct message from server");
            }
        }
        return Either.left("Could not fetch value in time");
    }

    public static <A, B> Either<String, B> flatMap(final Either<String, A> a, Function<A, Either<String, B>> fn) {
        if (a.isRight()) {
            return fn.apply(a.right().value());
        } else {
            return Either.left(a.left().value());
        }
    }

    public static Either<String, String> chain(final Either<String, String> start, final List<Function<String, Either<String, String>>> chains) {
        if (start.isLeft()) {
            return start;
        }
        Either<String, String> eitherToReturn = start;
        for (final Function<String, Either<String, String>> chain: chains){
            eitherToReturn = flatMap(eitherToReturn, chain);
            if (eitherToReturn.isLeft()) {
                break;
            }
        }
        return eitherToReturn;
    }

}
