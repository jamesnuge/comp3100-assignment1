package xyz.jamesnuge.scheduling;

import fj.data.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import xyz.jamesnuge.messaging.ClientMessagingService;

import static fj.data.Either.*;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
    public void shouldRunAlgorithmToFinalState() {
        service = new SchedulingService(cms, singletonMap("test", TestStateMachine::new));
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

        TestState(final Boolean isFinalState) {
            this.isFinalState = isFinalState;
        }

        @Override
        public Boolean isFinalState() {
            return isFinalState;
        }
    }

    static class TestStateMachine implements StateMachine<TestState, String> {

        private static final TestState FINISHED_STATE = new TestState(true);
        private static final TestState INCOMPLETE_STATE = new TestState(false);

        private Either<String, TestState> currentState = right(INCOMPLETE_STATE);

        public TestStateMachine(ClientMessagingService _cms) {}

        @Override
        public void accept(final String trigger) {
            if (trigger.equals("finished")) {
                this.currentState = right(FINISHED_STATE);
            }
        }

        @Override
        public Either<String, TestState> getCurrentState() {
            return currentState;
        }
    }

}