package de.uol.swp.server.chat;

import de.uol.swp.common.chat.request.RetrieveChatRequest;
import de.uol.swp.common.chat.request.SendChatMessageRequest;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.EventBusBasedTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.greenrobot.eventbus.EventBus;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests the ChatService
 */
public class ChatServiceTest extends EventBusBasedTest {

    final User defaultUser = new UserDTO("Marco", "test", "marco@test.de");
    ChatService chatService;
    ChatManagement chatManagement;

    /**
     * Sets up the test environment
     */
    @BeforeEach
    void setUp() {
        EventBus eventBus = getBus();
        chatManagement = new ChatManagement();
        chatService = new ChatService(eventBus, chatManagement);
    }

    /**
     * Tests the onChatRequest method
     * @throws InterruptedException
     */
    @Test
    void onChatRequestTest() throws InterruptedException {
        SendChatMessageRequest sendChatMessageRequest = new SendChatMessageRequest(defaultUser.getUsername(), "Test", LocalTime.now());
        post(sendChatMessageRequest);

        assertNotNull(chatManagement.getChatMessages());
        assertEquals(1, chatManagement.getChatMessages().size());

        SendChatMessageRequest sendChatMessageRequest2 = new SendChatMessageRequest(defaultUser.getUsername(), "Test2", LocalTime.now());
        post(sendChatMessageRequest2);

        assertEquals(2, chatManagement.getChatMessages().size());
    }

    /**
     * Tests the onRetrieveChatRequest method
     * @throws InterruptedException
     */
    @Test
    void onRetrieveChatRequestTest() throws InterruptedException {
        RetrieveChatRequest retrieveChatRequest = new RetrieveChatRequest();
        post(retrieveChatRequest);

        assertNotNull(chatManagement.getChatMessages());
    }
}