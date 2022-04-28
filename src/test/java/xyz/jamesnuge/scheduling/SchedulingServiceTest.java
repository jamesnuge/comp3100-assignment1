package xyz.jamesnuge.scheduling;

import fj.data.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import xyz.jamesnuge.Pair;
import xyz.jamesnuge.messaging.ClientMessagingService;

import java.util.concurrent.TimeUnit;

import static fj.data.Either.right;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static xyz.jamesnuge.util.TestUtil.assertLeft;
import static xyz.jamesnuge.util.TestUtil.assertRight;

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
        verify(cms).quit();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSchedulingServiceUsesNewStateOnEachAlgorithmCall() {
        StateMachine<TestState, String> algorithm = new CounterAlgorithm();
        StateMachine<TestState, String> algorithmSpy = Mockito.spy(algorithm);
        StateMachineFactory<TestState> counterAlgorithm = (cms) -> algorithmSpy;
        service = new SchedulingService(
                cms,
                singletonMap("test", Pair.of(
                                counterAlgorithm,
                                (c) -> Either.right(new TestState(false, 0))
                        )
                )
        );
        when(cms.loginToServer(any())).thenReturn(WRITE_RESULT);
        when(cms.beginScheduling()).thenReturn(WRITE_RESULT);
        when(cms.quit()).thenReturn(WRITE_RESULT);
        when(cms.getMessage()).thenReturn(
                right("1"),
                right("2"),
                right("finished")
        );
        InOrder inOrder = Mockito.inOrder(algorithmSpy);
        final Either<String, String> schedulingResult = service.scheduleJobsUsingAlgorithm("test");
        assertTrue(schedulingResult.isRight());
        inOrder.verify(algorithmSpy).accept("1", new TestState(false, 0));
        inOrder.verify(algorithmSpy).accept("2", new TestState(false, 1));
        inOrder.verify(algorithmSpy).accept("finished", new TestState(false, 2));
    }

    private final StateMachineFactory<TestState> STATE_FACTORY = (cms) -> (trigger, state) -> {
        return trigger.equals("finished") ? right(new TestState(true)) : right(state);
    };

    // Using proper class instead of lambda because Mockito.spy doesn't work with lambdas
    static class CounterAlgorithm implements StateMachine<TestState, String> {

        @Override
        public Either<String, TestState> accept(String trigger, TestState currentState) {
            return Either.right(
                    trigger.equals("finished") ?
                            new TestState(true, currentState.getCounter()) :
                            new TestState(false, currentState.getCounter() + 1)
            );
        }
    }

}