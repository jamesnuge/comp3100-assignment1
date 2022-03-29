package xyz.jamesnuge.scheduling.lrr;

import static fj.data.Either.right;
import static fj.data.List.list;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static xyz.jamesnuge.MessageParser.Message.OK;
import static xyz.jamesnuge.Util.flatMap;
import static xyz.jamesnuge.fixtures.ServerStateItemFixtures.generateServerStateItem;
import static xyz.jamesnuge.util.TestUtil.assertRight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import fj.data.Either;
import fj.data.List;
import xyz.jamesnuge.MessageParser;
import xyz.jamesnuge.messaging.ClientMessagingService;
import xyz.jamesnuge.scheduling.StateMachine;
import xyz.jamesnuge.state.ServerStateItem;

class LRRStateMachineTest {

    private static final Either<String, String> WRITE_RESULT = right("write");

    @Mock
    ClientMessagingService cms;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldPerformNoopIfTriggerUnknown() throws Exception {
        final List<ServerStateItem> config = list(
                generateServerStateItem("type", 1),
                generateServerStateItem("type", 2)
        );
        when(cms.getServerState()).thenReturn(right(config));
        when(cms.scheduleJob(any(), any(), any())).thenReturn(WRITE_RESULT);
        Either<String, LRRInternalState> currentState = performActionWithInitialState("fjsadjfhaskljdhf", "type", 2);
        assertRight(
                new LRRInternalState(-1,"type", 2, false),
                currentState
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStateMachineAssignsJobToTheNextAvailableMachine() throws Exception {
        final List<ServerStateItem> config = list(
                generateServerStateItem("type", 1),
                generateServerStateItem("type", 2)
        );
        when(cms.getServerState()).thenReturn(right(config));
        when(cms.scheduleJob(any(), any(), any())).thenReturn(WRITE_RESULT);
        when(cms.getMessage()).thenReturn(right(OK.name()), right(""));
        when(cms.signalRedy()).thenReturn(WRITE_RESULT);
        Either<String, LRRInternalState> currentState = performAction(
                "JOBN 2142 12 750 4 250 800",
                new LRRInternalState(0, "type", 2, false)
        );
        assertRight(
                new LRRInternalState(1, "type", 2, false),
                currentState
        );
        verify(cms).scheduleJob(eq(12), eq("type"), eq(1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStateMachineLoopsBackOnceAllServersHaveBeenAssignedAJob() throws Exception {
        when(cms.scheduleJob(any(), any(), any())).thenReturn(WRITE_RESULT);
        when(cms.getMessage()).thenReturn(right(OK.name()), right(""));
        when(cms.signalRedy()).thenReturn(WRITE_RESULT);

        Either<String, LRRInternalState> currentState = performAction("JOBN 2142 12 750 4 250 800", new LRRInternalState(1, "type", 2, false));
        assertRight(
                new LRRInternalState(0, "type", 2, false),
                currentState
        );
        verify(cms).scheduleJob(eq(12), eq("type"), eq(0));
    }

    @Test
    public void testStateShouldShouldReturnFinalStateOnNone() throws Exception {
        Either<String, LRRInternalState> currentState = performActionWithInitialState("NONE", "type", 3);
        assertRight(
                new LRRInternalState(-1, "", -1, true),
                currentState
        );
    }

    @Test
    public void testStateShouldNoopJobCompletionMessage() throws Exception {
        when(cms.signalRedy()).thenReturn(WRITE_RESULT);
        Either<String, LRRInternalState> currentState = performActionWithInitialState(MessageParser.InboudMessage.JCPL.name(), "type", 3);
        assertRight(
                new LRRInternalState(-1, "type", 3, false),
                currentState
        );
        verify(cms).signalRedy();
    }

    private Either<String, LRRInternalState> performActionWithInitialState(String action, String serverType, Integer numOfServers) {
        StateMachine<LRRInternalState, String> stateMachine = LrrFactory.STATE_MACHINE.createStateMachine(cms);
        Either<String, LRRInternalState> state = right(new LRRInternalState(-1, serverType, numOfServers, false));
        return flatMap(state, (s) -> stateMachine.accept(action, s));
    }

    private Either<String, LRRInternalState> performAction(String action, LRRInternalState state) {
        StateMachine<LRRInternalState, String> stateMachine = LrrFactory.STATE_MACHINE.createStateMachine(cms);
        return stateMachine.accept(action, state);
    }

}