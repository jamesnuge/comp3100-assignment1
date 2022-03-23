package xyz.jamesnuge.scheduling;

import static fj.data.Either.right;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static xyz.jamesnuge.util.TestUtil.assertLeft;
import static xyz.jamesnuge.util.TestUtil.assertRight;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import fj.data.Either;
import xyz.jamesnuge.Pair;
import xyz.jamesnuge.messaging.ClientMessagingService;

class SchedulingServiceTest {

    private static final Either<String, String> WRITE_RESULT = right("write");

    @Mock
    private ClientMessagingService cms;

    private SchedulingService service;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testShouldReturnLeftIfAlgorithmNotFound() {
        service = new SchedulingService(cms, emptyMap());
        Either<String, String> actual = service.scheduleJobsUsingAlgorithm("MissingAlgorithm");
        assertLeft(
                "Algorithm MissingAlgorithm not found",
                actual
        );
    }

    @Test
    @Timeout(value = 100L, unit = TimeUnit.MILLISECONDS)
    @SuppressWarnings("unchecked")
    public void shouldRunAlgorithmToFinalState() {
        service = new SchedulingService(
                cms,
                singletonMap("test", Pair.of(
                                STATE_FACTORY,
                                (c) -> Either.right(new TestState(false))
                        )
                )
        );
        when(cms.loginToServer(any())).thenReturn(WRITE_RESULT);
        when(cms.beginScheduling()).thenReturn(WRITE_RESULT);
        when(cms.quit()).thenReturn(WRITE_RESULT);
        when(cms.getMessage()).thenReturn(
                right("1"),
                right("2"),
                right("3"),
                right("finished"),
                right("unreachable")
        );
        final Either<String, String> result = service.scheduleJobsUsingAlgorithm("test");
        assertRight(
                "Successfully ran algorithm",
                result
        );
        verify(cms).loginToServer(any());
        verify(cms).beginScheduling();
        verify(cms, times(4)).getMessage();
    }

    static class TestState implements State {
        private final Boolean isFinalState;

        public TestState(final Boolean isFinalState) {
            this.isFinalState = isFinalState;
        }

        @Override
        public Boolean isFinalState() {
            return isFinalState;
        }
    }

    private final StateMachineFactory<TestState> STATE_FACTORY = (cms) -> (trigger, state) -> {
        return trigger.equals("finished") ? right(new TestState(true)) : right(state);
    };

}