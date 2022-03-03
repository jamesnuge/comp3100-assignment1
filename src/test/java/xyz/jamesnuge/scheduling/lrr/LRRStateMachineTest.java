package xyz.jamesnuge.scheduling.lrr;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import xyz.jamesnuge.messaging.ClientMessagingService;
import xyz.jamesnuge.scheduling.StateMachine;
import xyz.jamesnuge.state.ServerStateItem;

import static fj.data.Either.right;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static xyz.jamesnuge.fixtures.ServerStateItemFixtures.SERVER_STATE_ITEM;
import static xyz.jamesnuge.fixtures.ServerStateItemFixtures.generateServerStateItem;
import static xyz.jamesnuge.util.TestUtil.assertRight;

class LRRStateMachineTest {

    @Mock
    ClientMessagingService cms;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testStateMachineConstructsWithCorrectInitialState() {
        final List<ServerStateItem> config = List.of(SERVER_STATE_ITEM);
        when(cms.getServerState()).thenReturn(right(config));
        final StateMachine<LRRInternalState, String> stateMachine = new LRRStateMachine(cms);
        assertRight(
                LRRInternalState.createInternalStateFactory("type1").apply(-1, 1),
                stateMachine.getCurrentState()
        );
        verify(cms).getServerState();
    }

    @Test
    public void shouldPerformNoopIfTriggerUnknown() {
        final List<ServerStateItem> config = Arrays.asList(
                generateServerStateItem("type", 1),
                generateServerStateItem("type", 2)
        );
        when(cms.getServerState()).thenReturn(right(config));
        when(cms.scheduleJob(any(), any(), any())).thenReturn(right("write"));
        final StateMachine<LRRInternalState, String> stateMachine = new LRRStateMachine(cms);
        assertEquals(
                stateMachine.accept("alkdfjsldjfsadjf"),
                LRRInternalState.createInternalStateFactory("type").apply(-1, 2)
        );
    }

    @Test
    public void testStateMachineAssignsJobToTheNextAvailableMachine() {
            final List<ServerStateItem> config = Arrays.asList(
                    generateServerStateItem("type", 1),
                    generateServerStateItem("type", 2)
            );
            when(cms.getServerState()).thenReturn(right(config));
            when(cms.scheduleJob(any(), any(), any())).thenReturn(right("write"));
            final StateMachine<LRRInternalState, String> stateMachine = new LRRStateMachine(cms);
            assertEquals(
                    LRRInternalState.createFinalInternalStateFactory("type").apply(0, 2),
                    stateMachine.accept("JOBN 2142 12 750 4 250 800")
            );
            verify(cms).scheduleJob(eq(12), eq("type"), eq(0));
    }

}