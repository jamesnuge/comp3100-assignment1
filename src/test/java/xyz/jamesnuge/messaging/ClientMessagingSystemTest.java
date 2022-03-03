package xyz.jamesnuge.messaging;

import fj.data.Either;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import xyz.jamesnuge.state.ServerStateItem;

import static fj.data.Either.right;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static xyz.jamesnuge.state.ServerStateItem.ServerStatus.INACTIVE;

class ClientMessagingSystemTest {

    @Mock
    Function<String, Either<String, String>> writeMock;

    @Mock
    Supplier<Either<String, String>> readMock;

    @InjectMocks
    ClientMessagingSystem clientMessagingSystem;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(writeMock.apply(any())).thenReturn(right("write"));
    }

    @Test
    public void testLoginToServer() {
        final InOrder inOrder = inOrder(writeMock, readMock);
        final String userName = "userName";
        when(readMock.get()).thenReturn(right("OK"));
        clientMessagingSystem.loginToServer(userName);
        inOrder.verify(writeMock).apply(eq("HELO"));
        inOrder.verify(readMock).get();
        inOrder.verify(writeMock).apply("AUTH " + userName);
        inOrder.verify(readMock).get();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testGetServerStateForAllComputers() {
        final InOrder inOrder = inOrder(writeMock, readMock);
        final ServerStateItem generatedState = generateServerStateItem(1);
        when(readMock.get()).thenReturn(
                right("DATA"),
                right(createServerStateString(generatedState))
        );
        final Either<String, List<ServerStateItem>> serverState = clientMessagingSystem.getServerState();
        inOrder.verify(writeMock).apply(eq("GETS All"));
        inOrder.verify(readMock).get();
        inOrder.verify(writeMock).apply("OK");
        inOrder.verify(readMock).get();
        assertRight(
                List.of(generatedState),
                serverState
        );
    }

    @Test
    public void testScheduleJobSendsMessage() {
        when(writeMock.apply(any())).thenReturn(right("mockResult"));
        final Either<String, String> writeResult = clientMessagingSystem.scheduleJob(1, "test", 4);
        verify(writeMock).apply("SCHD 1 test 4");
        assertRight("mockResult", writeResult);
    }

    public static <A, B> void  assertRight(B expected, Either<A, B> actual) {
        if (actual.isLeft()) {
            fail("Either had a left value: " + actual.left().value());
        } else {
            assertEquals(
                    expected,
                    actual.right().value()
            );
        }
    }

    public static ServerStateItem generateServerStateItem(Integer id) {
        return new ServerStateItem(
                "type",
                id,
                INACTIVE,
                -1L,
                id,
                id.longValue(),
                id.longValue(),
                id,
                id
        );
    }

    public static String createServerStateString(ServerStateItem item) {
        return item.getType() + " " +
                item.getId() + " " +
                item.getStats() + " " +
                item.getCurrentStartTime() + " " +
                item.getCore() + " " +
                item.getMemory() + " " +
                item.getDisk() + " " +
                item.getWaitingJobs() + " " +
                item.getRunningJobs();
    }

}