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
import static xyz.jamesnuge.fixtures.ServerStateItemFixtures.SERVER_STATE_ITEM;
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

    @Test
    public void testStateMachineConstructsWithCorrectInitialState() throws Exception {
        final List<ServerStateItem> config = list(SERVER_STATE_ITEM);
        when(cms.getServerState()).thenReturn(right(config));
        final StateMachine<LRRInternalState, String> stateMachine = new LRRStateMachine(cms);
        assertRight(
                LRRInternalState.createInternalStateFactory("type1").f(-1, 1),
                stateMachine.getCurrentState()
        );
        verify(cms).getServerState();
    }

    @Test
    public void shouldPerformNoopIfTriggerUnknown() throws Exception {
        final List<ServerStateItem> config = list(
                generateServerStateItem("type", 1),
                generateServerStateItem("type", 2)
        );
        when(cms.getServerState()).thenReturn(right(config));
        when(cms.scheduleJob(any(), any(), any())).thenReturn(WRITE_RESULT);
        final StateMachine<LRRInternalState, String> stateMachine = new LRRStateMachine(cms);
        stateMachine.accept("alkdfjsldjfsadjf");
        assertRight(
                LRRInternalState.createInternalStateFactory("type").f(-1, 2),
                stateMachine.getCurrentState()
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
        final StateMachine<LRRInternalState, String> stateMachine = new LRRStateMachine(cms);
        stateMachine.accept("JOBN 2142 12 750 4 250 800");
        assertRight(
                LRRInternalState.createInternalStateFactory("type").f(0, 2),
                stateMachine.getCurrentState()
        );
        verify(cms).scheduleJob(eq(12), eq("type"), eq(0));
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
        final StateMachine<LRRInternalState, String> stateMachine = new LRRStateMachine(cms);
        stateMachine.accept("JOBN 2142 12 750 4 250 800");
        stateMachine.accept("JOBN 2142 13 750 4 250 800");
        stateMachine.accept("JOBN 2142 14 750 4 250 800");
        assertRight(
                LRRInternalState.createInternalStateFactory("type").f(0, 2),
                stateMachine.getCurrentState()
        );
        verify(cms).scheduleJob(eq(12), eq("type"), eq(0));
        verify(cms).scheduleJob(eq(13), eq("type"), eq(1));
        verify(cms).scheduleJob(eq(14), eq("type"), eq(0));
    }

    @Test
    public void testStateShouldShouldReturnFinalStateOnNone() throws Exception {
        final List<ServerStateItem> config = list(
                generateServerStateItem("type", 1),
                generateServerStateItem("type", 2),
                generateServerStateItem("type", 3)
        );
        when(cms.getServerState()).thenReturn(right(config));
        final StateMachine<LRRInternalState, String> stateMachine = new LRRStateMachine(cms);
        stateMachine.accept("NONE");
        assertRight(
                LRRInternalState.createFinalInternalStateFactory("type").f(-1, -1),
                stateMachine.getCurrentState()
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
        final StateMachine<LRRInternalState, String> stateMachine = new LRRStateMachine(cms);
        stateMachine.accept(MessageParser.InboudMessage.JCPL.name());
        verify(cms).signalRedy();
        assertRight(
                LRRInternalState.createInternalStateFactory("type").f(-1, 3),
                stateMachine.getCurrentState()
        );
    }

}