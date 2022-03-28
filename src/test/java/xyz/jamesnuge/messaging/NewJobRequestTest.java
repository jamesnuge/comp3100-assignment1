package xyz.jamesnuge.messaging;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NewJobRequestTest {

    @Test
    public void testParsesFromStringCorrectly() {
        String message = "JOBN 123 1 2 3 4 5";
        NewJobRequest expected = new NewJobRequest(1, 123, 2L, 3, 4L, 5L);
        assertEquals(
                expected,
                NewJobRequest.parseFromJOBNMessage(message)
        );
    }
}