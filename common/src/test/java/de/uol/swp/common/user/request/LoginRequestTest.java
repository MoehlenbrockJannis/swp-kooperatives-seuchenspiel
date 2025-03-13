package de.uol.swp.common.user.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginRequestTest {

    final String username = "Marco";
    final String password = "Test";

    @Test
    void createLoginRequest() {
        LoginRequest request = new LoginRequest(username, password);

        assertEquals(username, request.getUsername());
        assertEquals(password, request.getPassword());
    }

    @Test
    void setLobbyRequestUsernameAndPassword() {
        LoginRequest request = new LoginRequest(username, password);

        assertEquals(username, request.getUsername());
        assertEquals(password, request.getPassword());

        request.setUsername("Marco1");
        request.setPassword("Test1");

        assertEquals("Marco1", request.getUsername());
        assertEquals("Test1", request.getPassword());
    }

}
