package xyz.jamesnuge.scheduling.lrr;

import fj.data.Either;
import fj.data.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import xyz.jamesnuge.MessageParser;
import xyz.jamesnuge.messaging.ClientMessagingService;
import xyz.jamesnuge.scheduling.StateMachine;
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
import static xyz.jamesnuge.util.TestUtil.assertRight;

class LRRStateMachineTest {

    private static final Either<String, String> WRITE_RESULT = right("write");

    @Mock
    ClientMessagingService cms;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

//    @Test
//    public void testStateMachineConstructsWithCorrectInitialState() throws Exception {
//        final List<ServerStateItem> config = list(SERVER_STATE_ITEM);
//        when(cms.getServerState()).thenReturn(right(config));
//        Pair<StateMachine<LRRInternalState, String>, Either<String, LRRInternalState>> stateMachineAndInitialState = LrrFactory.c.createAlgorithm(cms);
//        assertRight(
//                LRRInternalState.createInternalStateFactory("type1").f(-1, 1),
//                stateMachineAndInitialState.getRight()
//        );
//        verify(cms).getServerState();
//    }

    @Test
    public void shouldPerformNoopIfTriggerUnknown() throws Exception {
        final List<ServerStateItem> config = list(
                generateServerStateItem("type", 1),
                generateServerStateItem("type", 2)
        );
        when(cms.getServerState()).thenReturn(right(config));
        when(cms.scheduleJob(any(), any(), any())).thenReturn(WRITE_RESULT);
        Either<String, LRRInternalState> currentState = performActionWithInitialState("fjsadjfhaskljdhf");
        assertRight(
                new LRRInternalState(-1,"type", 2, false),
                currentState
        );
    }

    @Test
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
                LRRInternalState.createInternalStateFactory("type").f(1, 2),
                currentState
        );
        verify(cms).scheduleJob(eq(12), eq("type"), eq(1));
    }

    @Test
    public void testStateMachineLoopsBackOnceAllServersHaveBeenAssignedAJob() throws Exception {
        final List<ServerStateItem> config = list(
                generateServerStateItem("type", 1),
                generateServerStateItem("type", 2)
        );
        when(cms.getServerState()).thenReturn(right(config));
        when(cms.scheduleJob(any(), any(), any())).thenReturn(WRITE_RESULT);
        when(cms.getMessage()).thenReturn(right(OK.name()), right(""));
        when(cms.signalRedy()).thenReturn(WRITE_RESULT);

        Either<String, LRRInternalState> currentState = performAction("JOBN 2142 12 750 4 250 800", new LRRInternalState(1, "type", 2, false));
        assertRight(
                LRRInternalState.createInternalStateFactory("type").f(0, 2),
                currentState
        );
        verify(cms).scheduleJob(eq(12), eq("type"), eq(0));
    }

    @Test
    public void testStateShouldShouldReturnFinalStateOnNone() throws Exception {
        final List<ServerStateItem> config = list(
                generateServerStateItem("type", 1),
                generateServerStateItem("type", 2),
                generateServerStateItem("type", 3)
        );
        when(cms.getServerState()).thenReturn(right(config));
        Either<String, LRRInternalState> currentState = performActionWithInitialState("NONE");
        assertRight(
                LRRInternalState.createFinalInternalStateFactory("").f(-1, -1),
                currentState
        );
    }

    @Test
    public void testStateShouldNoopJobCompletionMessage() throws Exception {
        final List<ServerStateItem> config = list(
                generateServerStateItem("type", 1),
                generateServerStateItem("type", 2),
                generateServerStateItem("type", 3)
        );
        when(cms.getServerState()).thenReturn(right(config));
        when(cms.signalRedy()).thenReturn(WRITE_RESULT);
        Either<String, LRRInternalState> currentState = performActionWithInitialState(MessageParser.InboudMessage.JCPL.name());
        assertRight(
                LRRInternalState.createInternalStateFactory("type").f(-1, 3),
                currentState
        );
        verify(cms).signalRedy();
    }

    private Either<String, LRRInternalState> performActionWithInitialState(String action) {
        StateMachine<LRRInternalState, String> stateMachine = LrrFactory.STATE_MACHINE.createStateMachine(cms);
        Either<String, LRRInternalState> state = LrrFactory.CONFIGURATION.createInitialState(cms);
        return flatMap(state, (s) -> stateMachine.accept(action, s));
    }

    private Either<String, LRRInternalState> performAction(String action, LRRInternalState state) {
        StateMachine<LRRInternalState, String> stateMachine = LrrFactory.STATE_MACHINE.createStateMachine(cms);
        return stateMachine.accept(action, state);
    }

}