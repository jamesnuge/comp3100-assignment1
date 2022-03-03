package xyz.jamesnuge.scheduling.lrr;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import xyz.jamesnuge.messaging.ClientMessagingService;
import xyz.jamesnuge.scheduling.StateMachine;
import xyz.jamesnuge.state.ServerStateItem;

import static fj.data.Either.right;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static xyz.jamesnuge.fixtures.ServerStateItemFixtures.SERVER_STATE_ITEM;
import static xyz.jamesnuge.util.TestUtil.assertRight;

class LRRStateMachineTest {

    @Mock
    ClientMessagingService cms;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testStateMachineConstructsWithCorrestInitialState() {
        final List<ServerStateItem> config = List.of(SERVER_STATE_ITEM);
        when(cms.getServerState()).thenReturn(right(config));
        final StateMachine<LRRInternalState, String> stateMachine = new LRRStateMachine(cms, config);
        assertRight(
                LRRInternalState.createInternalStateFactory(config.get(0).getType()).apply(0L, 1),
                stateMachine.getCurrentState()
        );
        verify(cms).getServerState();
    }

}