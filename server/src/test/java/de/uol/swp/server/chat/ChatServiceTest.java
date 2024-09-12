package de.uol.swp.server.chat;

import de.uol.swp.common.chat.request.RetrieveChatRequest;
import de.uol.swp.common.chat.request.SendChatMessageRequest;
import de.uol.swp.common.chat.request.SendLobbyChatMessageRequest;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.EventBusBasedTest;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.lobby.message.LobbyDroppedServerInternalMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.greenrobot.eventbus.EventBus;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the ChatService
 */
public class ChatServiceTest extends EventBusBasedTest {

    final User defaultUser = new UserDTO("Marco", "test", "marco@test.de");
    final Lobby defaultLobby = new LobbyDTO("TestLobby", defaultUser);
    ChatService chatService;
    ChatManagement chatManagement;
    LobbyManagement lobbyManagement;

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
     * Tests the onChatRequest method
     * @throws InterruptedException
     */
    @Test
    void onLobbyChatRequestTest() throws InterruptedException {
        SendLobbyChatMessageRequest sendLobbyChatMessageRequest = new SendLobbyChatMessageRequest( "Test", LocalTime.now());
        sendLobbyChatMessageRequest.setUser(defaultUser);
        sendLobbyChatMessageRequest.setLobbyName(defaultLobby.getName());
        post(sendLobbyChatMessageRequest);

        assertNotNull(chatManagement.getLobbyChatMessages(this.defaultLobby.getName()));
        assertEquals(1, chatManagement.getLobbyChatMessages(this.defaultLobby.getName()).size());

        SendLobbyChatMessageRequest sendLobbyChatMessageRequest2 = new SendLobbyChatMessageRequest( "Test", LocalTime.now());
        sendLobbyChatMessageRequest2.setUser(defaultUser);
        sendLobbyChatMessageRequest2.setLobbyName(defaultLobby.getName());
        post(sendLobbyChatMessageRequest2);

        assertEquals(2, chatManagement.getLobbyChatMessages(this.defaultLobby.getName()).size());
    }

    /**
     * Tests the LobbyDroppedServerInternalMessage method
     * @throws InterruptedException
     */
    @Test
    void onLobbyDroppedServerInternalMessage() throws InterruptedException {
        chatManagement.addLobbyChatMessage(defaultLobby.getName(), "Test");
        LobbyDroppedServerInternalMessage lobbyDroppedServerInternalMessage = new LobbyDroppedServerInternalMessage(defaultLobby.getName());
        post(lobbyDroppedServerInternalMessage);

        assertThat(chatManagement.getLobbyChatMessages(this.defaultLobby.getName())).isNullOrEmpty();


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

    /**
     * Tests the onRetrieveChatRequest method
     * @throws InterruptedException
     */
    @Test
    void onRetrieveLobbyRequestTest() throws InterruptedException {
        chatManagement.addLobbyChatMessage(defaultLobby.getName(), "Test");
        RetrieveChatRequest retrieveChatRequest = new RetrieveChatRequest(this.defaultLobby.getName());
        post(retrieveChatRequest);

        assertNotNull(chatManagement.getLobbyChatMessages(retrieveChatRequest.getLobbyName()));
    }
}