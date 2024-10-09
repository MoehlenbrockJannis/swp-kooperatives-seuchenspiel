package de.uol.swp.server.chat;

import de.uol.swp.common.chat.request.RetrieveAllChatMessagesRequest;
import de.uol.swp.common.chat.request.SendChatMessageRequest;
import de.uol.swp.common.chat.request.SendUserLobbyChatMessageRequest;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
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
    final Lobby defaultLobby = new LobbyDTO("TestLobby", defaultUser, 2, 4);
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
        SendChatMessageRequest sendChatMessageRequest = new SendChatMessageRequest(defaultUser, "Test", LocalTime.now());
        post(sendChatMessageRequest);

        assertNotNull(chatManagement.getChatMessages());
        assertEquals(1, chatManagement.getChatMessages().size());

        SendChatMessageRequest sendChatMessageRequest2 = new SendChatMessageRequest(defaultUser, "Test2", LocalTime.now());
        post(sendChatMessageRequest2);

        assertEquals(2, chatManagement.getChatMessages().size());
    }

    /**
     * Tests the onChatRequest method
     * @throws InterruptedException
     */
    @Test
    void onLobbyChatRequestTest() throws InterruptedException {
        SendUserLobbyChatMessageRequest sendLobbyChatMessageRequest = new SendUserLobbyChatMessageRequest(defaultLobby, defaultUser, "Test", LocalTime.now());
        post(sendLobbyChatMessageRequest);

        assertNotNull(chatManagement.getLobbyChatMessages(this.defaultLobby));
        assertEquals(1, chatManagement.getLobbyChatMessages(this.defaultLobby).size());

        SendUserLobbyChatMessageRequest sendLobbyChatMessageRequest2 = new SendUserLobbyChatMessageRequest(defaultLobby, defaultUser, "Test", LocalTime.now());
        post(sendLobbyChatMessageRequest2);

        assertEquals(2, chatManagement.getLobbyChatMessages(this.defaultLobby).size());
    }

    /**
     * Tests the LobbyDroppedServerInternalMessage method
     * @throws InterruptedException
     */
    @Test
    void onLobbyDroppedServerInternalMessage() throws InterruptedException {
        chatManagement.addLobbyChatMessage(defaultLobby, "Test");
        LobbyDroppedServerInternalMessage lobbyDroppedServerInternalMessage = new LobbyDroppedServerInternalMessage(defaultLobby);
        post(lobbyDroppedServerInternalMessage);

        assertThat(chatManagement.getLobbyChatMessages(this.defaultLobby)).isNullOrEmpty();


    }

    /**
     * Tests the onRetrieveChatRequest method
     * @throws InterruptedException
     */
    @Test
    void onRetrieveChatRequestTest() throws InterruptedException {
        RetrieveAllChatMessagesRequest retrieveChatRequest = new RetrieveAllChatMessagesRequest();
        post(retrieveChatRequest);

        assertNotNull(chatManagement.getChatMessages());
    }

    /**
     * Tests the onRetrieveChatRequest method
     * @throws InterruptedException
     */
    @Test
    void onRetrieveLobbyRequestTest() throws InterruptedException {
        chatManagement.addLobbyChatMessage(defaultLobby, "Test");
        RetrieveAllChatMessagesRequest retrieveChatRequest = new RetrieveAllChatMessagesRequest(this.defaultLobby);
        post(retrieveChatRequest);

        assertNotNull(chatManagement.getLobbyChatMessages(retrieveChatRequest.getLobby()));
    }
}