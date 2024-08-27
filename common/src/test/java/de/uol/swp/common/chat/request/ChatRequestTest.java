package de.uol.swp.common.chat.request;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for the chat request
 *
 * @see de.uol.swp.common.chat.request.ChatRequest
 */
public class ChatRequestTest {

    /**
     * Test for creation of the ChatRequests
     *
     * This test checks if the username and the chatMessage of the ChatRequest gets
     * set correctly during the creation of the request
     *
     * @since 2023-05-14
     */

    final User defaultUser = new UserDTO("Marco", "test", "marco@test.de");
    final LocalTime time = LocalTime.now();

    @Test
    void createChatRequest() {

        final String expectedMessage = "[" + time + "] " + defaultUser.getUsername() + ": Hallo Welt!";

        ChatRequest request = new ChatRequest(defaultUser.getUsername(), "Hallo Welt!", time);

        assertEquals(expectedMessage, request.getChatMessage());
    }
}
