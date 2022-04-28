package xyz.jamesnuge.scheduling.firstCapable;

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

class FcFactoryTest {

    private static final Either<String, String> WRITE_RESULT = right("write");

    @Mock
    ClientMessagingService cms;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldPerformNoopIfTriggerUnknown() throws Exception {
        Either<String, FcInternalState> currentState = performActionWithInitialState("fjsadjfhaskljdhf");
        assertRight(
                new FcInternalState(false),
                currentState
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStateMachineAssignsJobToTheFirstCapableMachine() throws Exception {
        final List<ServerStateItem> config = list(
                generateServerStateItem("type", 2),
                generateServerStateItem("type", 1)
        );
        when(cms.getServerState()).thenReturn(right(config));
        when(cms.scheduleJob(any(), any(), any())).thenReturn(WRITE_RESULT);
        when(cms.getMessage()).thenReturn(right(OK.name()), right(""));
        when(cms.signalRedy()).thenReturn(WRITE_RESULT);
        when(cms.getServerState(any())).thenReturn(right(config));
        Either<String, FcInternalState> currentState = performAction(
                "JOBN 2142 12 750 4 250 800",
                new FcInternalState(false)
        );
        assertRight(
                new FcInternalState(false),
                currentState
        );
        verify(cms).scheduleJob(eq(12), eq("type"), eq(2));
    }

    @Test
    public void testStateShouldShouldReturnFinalStateOnNone() throws Exception {
        Either<String, FcInternalState> currentState = performActionWithInitialState("NONE");
        assertRight(
                new FcInternalState(true),
                currentState
        );
    }

    @Test
    public void testStateShouldNoopJobCompletionMessage() throws Exception {
        when(cms.signalRedy()).thenReturn(WRITE_RESULT);
        Either<String, FcInternalState> currentState = performActionWithInitialState(MessageParser.InboudMessage.JCPL.name());
        assertRight(
                new FcInternalState(false),
                currentState
        );
        verify(cms).signalRedy();
    }

    private Either<String, FcInternalState> performActionWithInitialState(String action) {
        StateMachine<FcInternalState, String> stateMachine = FcFactory.STATE_MACHINE.createStateMachine(cms);
        Either<String, FcInternalState> state = right(new FcInternalState(false));
        return flatMap(state, (s) -> stateMachine.accept(action, s));
    }

    private Either<String, FcInternalState> performAction(String action, FcInternalState state) {
        StateMachine<FcInternalState, String> stateMachine = FcFactory.STATE_MACHINE.createStateMachine(cms);
        return stateMachine.accept(action, state);
    }

}