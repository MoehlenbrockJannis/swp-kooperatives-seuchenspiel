package de.uol.swp.server.chat;

import de.uol.swp.common.chat.request.RetrieveChatRequest;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.EventBusBasedTest;
import org.junit.jupiter.api.Test;
import de.uol.swp.common.chat.request.ChatRequest;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests the ChatService
 */
public class ChatServiceTest extends EventBusBasedTest {

    final User defaultUser = new UserDTO("Marco", "test", "marco@test.de");
    final ChatService chatService = new ChatService(getBus());

    /**
     * Tests the onChatRequest method
     * @see ChatService
     */
    @Test
    void onChatRequestTest() throws InterruptedException {
        ChatRequest chatRequest = new ChatRequest(defaultUser.getUsername(), "Test", LocalTime.now());
        post(chatRequest);

        assertNotNull(chatService.getChatMessages());
        assertEquals(1, chatService.getChatMessages().size());

        ChatRequest chatRequest2 = new ChatRequest(defaultUser.getUsername(), "Test2", LocalTime.now());
        post(chatRequest);

        assertEquals(2, chatService.getChatMessages().size());
    }

    /**
     * Tests the onRetrieveChatRequest method
     * @see ChatService
     */
    @Test
    void onRetrieveChatRequestTest() throws InterruptedException {
        RetrieveChatRequest retrieveChatRequest = new RetrieveChatRequest();
        post(retrieveChatRequest);

        assertNotNull(chatService.getChatMessages());
    }
}
