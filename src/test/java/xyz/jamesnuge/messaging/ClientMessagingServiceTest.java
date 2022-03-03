package xyz.jamesnuge.messaging;

import fj.data.Either;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import xyz.jamesnuge.state.ServerStateItem;

import static fj.data.Either.right;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static xyz.jamesnuge.fixtures.ServerStateItemFixtures.SERVER_STATE_ITEM;
import static xyz.jamesnuge.fixtures.ServerStateItemFixtures.createServerStateString;
import static xyz.jamesnuge.util.TestUtil.assertRight;

class ClientMessagingServiceTest {

    @Mock
    Function<String, Either<String, String>> writeMock;

    @Mock
    Supplier<Either<String, String>> readMock;

    @InjectMocks
    ClientMessagingService clientMessagingService;

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
        clientMessagingService.loginToServer(userName);
        inOrder.verify(writeMock).apply(eq("HELO"));
        inOrder.verify(readMock).get();
        inOrder.verify(writeMock).apply("AUTH " + userName);
        inOrder.verify(readMock).get();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testGetServerStateForAllComputers() {
        final InOrder inOrder = inOrder(writeMock, readMock);
        final ServerStateItem generatedState = SERVER_STATE_ITEM;
        when(readMock.get()).thenReturn(
                right("DATA"),
                right(createServerStateString(generatedState))
        );
        final Either<String, List<ServerStateItem>> serverState = clientMessagingService.getServerState();
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
        final Either<String, String> writeResult = clientMessagingService.scheduleJob(1, "test", 4);
        verify(writeMock).apply("SCHD 1 test 4");
        assertRight("write", writeResult);
    }


    @Test
    public void testBeginSchedulingSendsRedyMessageAndReceivesOK() {
        final Either<String, String> schedulingResult = clientMessagingService.beginScheduling();
        verify(writeMock).apply(eq("REDY"));
        assertRight(
                "write",
                schedulingResult
        );
    }

    @Test
    public void testPushJobSendsMessage() {
        final Either<String, String> schedulingResult = clientMessagingService.pushJob();
        verify(writeMock).apply(eq("PSHJ"));
        assertRight(
                "write",
                schedulingResult
        );
    }

    @Test
    public void testQuitSendsMessage() {
        final Either<String, String> schedulingResult = clientMessagingService.quit();
        verify(writeMock).apply(eq("QUIT"));
        assertRight(
                "write",
                schedulingResult
        );
    }

}