package de.uol.swp.client.chat;

import de.uol.swp.client.EventBusBasedTest;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

/**
 * Test class of the ChatService
 */

public class ChatServiceTest extends EventBusBasedTest {

    final User defaultUser = new UserDTO("Marco", "test", "marco@test.de");

    /**
     * Test for the sendChatRequest method
     * <p>
     * This test checks if the sendChatRequest method sends a ChatRequest to the EventBus
     *
     * @since 2024-08-26
     */
    @Test
    public void testSendChatRequest() throws InterruptedException {
        ChatService chatService = new ChatService(getBus());
        chatService.sendChatRequest(defaultUser.getUsername(), "Hello World!", LocalTime.now());
        waitForLock();
    }

    /**
     * Test for the retrieveChat method
     * <p>
     * This test checks if the retrieveChat method sends a RetrieveChatRequest to the EventBus
     *
     * @since 2024-08-26
     */
    @Test
    public void testRetrieveChat() {
        ChatService chatService = new ChatService(getBus());
        chatService.retrieveChat();
    }
}
