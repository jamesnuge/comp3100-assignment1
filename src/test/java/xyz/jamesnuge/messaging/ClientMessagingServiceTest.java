package xyz.jamesnuge.messaging;

import static fj.data.Either.left;
import static fj.data.Either.right;
import static fj.data.List.list;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static xyz.jamesnuge.fixtures.ServerStateItemFixtures.SERVER_STATE_ITEM;
import static xyz.jamesnuge.fixtures.ServerStateItemFixtures.createServerStateString;
import static xyz.jamesnuge.util.TestUtil.assertRight;

import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import fj.data.Either;
import fj.data.List;
import xyz.jamesnuge.state.ServerStateItem;

class ClientMessagingServiceTest {

    @Mock
    Function<String, Either<String, String>> writeMock;

    @Mock
    Supplier<Either<String, String>> readMock;

    @Mock
    Runnable finishMock;

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
    @SuppressWarnings("unchecked")
    public void testGetServerStateForAllComputers() {
        final ServerStateItem generatedState = SERVER_STATE_ITEM;
        when(readMock.get()).thenReturn(
                right("DATA"),
                right(createServerStateString(generatedState)),
                left("")
        );
        final Either<String, List<ServerStateItem>> serverState = clientMessagingService.getServerState();
        assertRight(
                list(generatedState),
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
        InOrder inOrder = Mockito.inOrder(readMock, writeMock, finishMock);
        when(readMock.get()).thenReturn(right("QUIT"));
        final Either<String, String> schedulingResult = clientMessagingService.quit();
        inOrder.verify(writeMock).apply(eq("QUIT"));
        inOrder.verify(readMock).get();
        inOrder.verify(finishMock).run();
        assertRight(
                "Successfully closed ClientMessagingService",
                schedulingResult
        );
    }


}