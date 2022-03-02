package xyz.jamesnuge;

import fj.data.Either;
import fj.data.Option;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.time4j.SystemClock;
import net.time4j.TemporalType;

public class Util {

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

    public static Either<String, String> chain(final List<Function<String, Either<String, String>>> chains) {
        if (chains.isEmpty()) {
            return Either.left("Cannot chain with empty list of functions");
        } else {
            return chain(chains.get(0).apply(""), chains.subList(1, chains.size()));
        }
    }

    @SafeVarargs
    public static Either<String, String> chain(final Function<String, Either<String, String>> ...chains) {
        return chain(Arrays.asList(chains));
    }

    public static Either<String, String> chain(
            final Either<String, String> start,
            final List<Function<String, Either<String, String>>> chains
    ) {
        if (start.isLeft()) {
            return start;
        } else {
            Either<String, String> eitherToReturn = start;
            for (final Function<String, Either<String, String>> chain : chains) {
                eitherToReturn = flatMap(eitherToReturn, chain);
                if (eitherToReturn.isLeft()) {
                    break;
                }
            }
            return eitherToReturn;
        }
    }

    public static Function<String, Either<String, String>> match(Predicate<String> matcher) {
        return (s) -> matcher.test(s) ? Either.right(s) : Either.left("Value '" + s + "' did not pass matcher");
    }

    public static <A, T> Option<T> toOption(final Either<A, T> either) {
        if (either.isRight()) {
            return Option.some(either.right().value());
        } else {
            return Option.none();
        }
    }

    public static Either<String, String> printString(String s) {
        System.out.println(s);
        return Either.right(s);
    }

}
