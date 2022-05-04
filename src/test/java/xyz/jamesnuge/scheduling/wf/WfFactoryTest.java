package xyz.jamesnuge.scheduling.wf;

import fj.data.Either;
import fj.data.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import xyz.jamesnuge.MessageParser;
import xyz.jamesnuge.messaging.ClientMessagingService;
import xyz.jamesnuge.scheduling.StateMachine;
import xyz.jamesnuge.scheduling.worstFit.WfFactory;
import xyz.jamesnuge.scheduling.worstFit.WfInternalState;
import xyz.jamesnuge.state.ServerStateItem;

import static fj.data.Either.right;
import static fj.data.List.list;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static xyz.jamesnuge.MessageParser.Message.OK;
import static xyz.jamesnuge.Util.flatMap;
import static xyz.jamesnuge.fixtures.ServerStateItemFixtures.generateServerStateItem;
import static xyz.jamesnuge.fixtures.ServerStateItemFixtures.generateServerStateItemWithNoJobs;
import static xyz.jamesnuge.util.TestUtil.assertRight;

public class WfFactoryTest {

    private static final Either<String, String> WRITE_RESULT = right("write");

    @Mock
    ClientMessagingService cms;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldPerformNoopIfTriggerUnknown() throws Exception {
        Either<String, WfInternalState> currentState = performActionWithInitialState("fjsadjfhaskljdhf");
        assertRight(
                new WfInternalState(false),
                currentState
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStateMachineAssignsJobToTheBestFitMachineWithoutJobs() throws Exception {
        final List<ServerStateItem> config = list(
                generateServerStateItemWithNoJobs("large", 3),
                generateServerStateItemWithNoJobs("small", 1),
                generateServerStateItemWithNoJobs("medium", 2)
        );
        when(cms.getServerState()).thenReturn(right(config));
        when(cms.scheduleJob(any(), any(), any())).thenReturn(WRITE_RESULT);
        when(cms.getMessage()).thenReturn(right(OK.name()), right(""));
        when(cms.signalRedy()).thenReturn(WRITE_RESULT);
        when(cms.getServerState(any())).thenReturn(right(config));
        Either<String, WfInternalState> currentState = performAction(
                "JOBN 2142 12 750 4 250 800",
                new WfInternalState(false)
        );
        assertRight(
                new WfInternalState(false),
                currentState
        );
        verify(cms).scheduleJob(eq(12), eq("large"), eq(3));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStateMachineAssignsJobToTheBestFitMachineWithJobs() throws Exception {
        final List<ServerStateItem> config = list(
                generateServerStateItem("large", 3, 1, 1),
                generateServerStateItem("small", 1, 0, 1),
                generateServerStateItem("medium", 2, 1, 0)
        );
        when(cms.getServerState()).thenReturn(right(config));
        when(cms.scheduleJob(any(), any(), any())).thenReturn(WRITE_RESULT);
        when(cms.getMessage()).thenReturn(right(OK.name()), right(""));
        when(cms.signalRedy()).thenReturn(WRITE_RESULT);
        when(cms.getServerState(any())).thenReturn(right(config));
        Either<String, WfInternalState> currentState = performAction(
                "JOBN 2142 12 750 4 250 800",
                new WfInternalState(false)
        );
        assertRight(
                new WfInternalState(false),
                currentState
        );
        verify(cms).scheduleJob(eq(12), eq("large"), eq(3));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStateMachineAssignsJobToTheBestFitMachineFiltersBusyMachines() throws Exception {
        final List<ServerStateItem> config = list(
                generateServerStateItem("large", 3, 1, 0),
                generateServerStateItemWithNoJobs("small", 1),
                generateServerStateItem("medium", 2, 1, 0)
        );
        when(cms.getServerState()).thenReturn(right(config));
        when(cms.scheduleJob(any(), any(), any())).thenReturn(WRITE_RESULT);
        when(cms.getMessage()).thenReturn(right(OK.name()), right(""));
        when(cms.signalRedy()).thenReturn(WRITE_RESULT);
        when(cms.getServerState(any())).thenReturn(right(config));
        Either<String, WfInternalState> currentState = performAction(
                "JOBN 2142 12 750 4 250 800",
                new WfInternalState(false)
        );
        assertRight(
                new WfInternalState(false),
                currentState
        );
        verify(cms).scheduleJob(eq(12), eq("small"), eq(1));
    }

    @Test
    public void testStateShouldShouldReturnFinalStateOnNone() throws Exception {
        Either<String, WfInternalState> currentState = performActionWithInitialState("NONE");
        assertRight(
                new WfInternalState(true),
                currentState
        );
    }

    @Test
    public void testStateShouldNoopJobCompletionMessage() throws Exception {
        when(cms.signalRedy()).thenReturn(WRITE_RESULT);
        Either<String, WfInternalState> currentState = performActionWithInitialState(MessageParser.InboudMessage.JCPL.name());
        assertRight(
                new WfInternalState(false),
                currentState
        );
        verify(cms).signalRedy();
    }

    private Either<String, WfInternalState> performActionWithInitialState(String action) {
        StateMachine<WfInternalState, String> stateMachine = WfFactory.STATE_MACHINE.createStateMachine(cms);
        Either<String, WfInternalState> state = right(new WfInternalState(false));
        return flatMap(state, (s) -> stateMachine.accept(action, s));
    }

    private Either<String, WfInternalState> performAction(String action, WfInternalState state) {
        StateMachine<WfInternalState, String> stateMachine = WfFactory.STATE_MACHINE.createStateMachine(cms);
        return stateMachine.accept(action, state);
    }

}