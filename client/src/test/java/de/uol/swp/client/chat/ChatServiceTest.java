package de.uol.swp.client.chat;

import de.uol.swp.client.EventBusBasedTest;
import de.uol.swp.common.chat.request.RetrieveChatRequest;
import de.uol.swp.common.chat.request.SendChatMessageRequest;
import de.uol.swp.common.chat.request.SendLobbyChatMessageRequest;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.greenrobot.eventbus.Subscribe;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Test class of the ChatService
 */

public class ChatServiceTest extends EventBusBasedTest {

    final User defaultUser = new UserDTO("Marco", "test", "marco@test.de");
    final Lobby defaultLobby = new LobbyDTO("TestLobby", defaultUser);

    /**
     * Sends a chat request
     * @throws InterruptedException
     */
    public void sendChatRequest() throws InterruptedException {
        ChatService chatService = new ChatService(getBus());
        chatService.sendChatRequest(defaultUser.getUsername(), "Hello World!", LocalTime.now());
        waitForLock();
    }

    public void sendLobbyChatRequest() throws InterruptedException {
        ChatService chatService = new ChatService(getBus());
        chatService.sendLobbyChatRequest(defaultUser, "Hello World!", LocalTime.now(), "TestLobby");
        waitForLock();
    }

    // handlers for events
    @Subscribe
    public void onEvent(SendChatMessageRequest e) {
        handleEvent(e);
    }

    @Subscribe
    public void onEvent(SendLobbyChatMessageRequest e) {
        handleEvent(e);
    }

    @Subscribe
    public void onEvent(RetrieveChatRequest e) {
        handleEvent(e);
    }

    /**
     * Test for the sendChatRequest method
     * <p>
     * This test checks if the sendChatRequest method sends a SendChatMessageRequest to the EventBus
     *
     * @since 2024-08-26
     */
    @Test
    public void testSendChatRequest() throws InterruptedException {
        sendChatRequest();
        assertInstanceOf(SendChatMessageRequest.class, event);
    }

    @Test
    public void testSendLobbyChatRequest() throws InterruptedException {
        sendLobbyChatRequest();
        assertInstanceOf(SendLobbyChatMessageRequest.class, event);
    }

    /**
     * Test for the retrieveChat method
     * <p>
     * This test checks if the retrieveChat method sends a RetrieveChatRequest to the EventBus
     *
     * @since 2024-08-26
     */
    @Test
    public void testRetrieveChat() throws InterruptedException {
        ChatService chatService = new ChatService(getBus());
        chatService.retrieveChat();

        waitForLock();

        assertInstanceOf(RetrieveChatRequest.class, event);
    }

    @Test
    public void testRetrieveLobbyChat() throws InterruptedException {
        ChatService chatService = new ChatService(getBus());
        chatService.retrieveChat(this.defaultLobby);

        waitForLock();

        assertInstanceOf(RetrieveChatRequest.class, event);
    }
}
