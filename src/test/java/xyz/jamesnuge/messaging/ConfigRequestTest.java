package xyz.jamesnuge.messaging;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigRequestTest {

    @Test
    public void shouldReturnAllConfigMessageString() {
        assertEquals(
                "GETS All",
                ConfigRequest.WHOLE_SYSTEM_REQUEST.constructServerMessage()
        );
    }

    @Test
    public void shouldReturnServerConfigMessageString() {
        assertEquals(
                "GETS Type serverType",
                ConfigRequest.createServerTypeConfigRequest("serverType").constructServerMessage()
        );
    }

    @Test
    public void shouldReturnCapableConfigMessageString() {
        assertEquals(
                "GETS Capable 10 100 1000",
                ConfigRequest.createCapableTypeConfigRequest(10, 100L, 1000L).constructServerMessage()
        );
    }

    @Test
    public void shouldReturnAvailableConfigMessageString() {
        assertEquals(
                "GETS Avail 10 100 1000",
                ConfigRequest.createAvailTypeConfigRequest(10, 100L, 1000L).constructServerMessage()
        );
    }

}