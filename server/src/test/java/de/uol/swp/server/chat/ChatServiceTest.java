package de.uol.swp.server.chat;

import de.uol.swp.common.chat.request.RetrieveAllChatMessagesRequest;
import de.uol.swp.common.chat.request.SendChatMessageRequest;
import de.uol.swp.common.chat.request.SendUserLobbyChatMessageRequest;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.EventBusBasedTest;
import de.uol.swp.server.chat.store.ChatStore;
import de.uol.swp.server.chat.store.MainMemoryBasedChatStore;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.lobby.message.LobbyDroppedServerInternalMessage;
import org.greenrobot.eventbus.EventBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the ChatService
 */
public class ChatServiceTest extends EventBusBasedTest {

    final User defaultUser = new UserDTO("Marco", "test", "marco@test.de");
    final Lobby defaultLobby = new LobbyDTO("TestLobby", defaultUser);
    ChatService chatService;
    ChatManagement chatManagement;
    LobbyManagement lobbyManagement;
    private ChatStore chatStore;

    /**
     * Sets up the test environment
     */
    @BeforeEach
    void setUp() {
        EventBus eventBus = getBus();
        this.chatStore = mock(MainMemoryBasedChatStore.class);
        chatManagement = new ChatManagement(chatStore);
        chatService = new ChatService(eventBus, chatManagement);
    }

    /**
     * Tests the onChatRequest method
     */
    @Test
    void onChatRequestTest() {
        SendChatMessageRequest sendChatMessageRequest = new SendChatMessageRequest(defaultUser, "Test", LocalTime.now());
        post(sendChatMessageRequest);

        when(chatStore.getChatMessages()).thenReturn(List.of(sendChatMessageRequest.getChatMessage()));

        assertNotNull(chatManagement.getChatMessages());
        assertEquals(1, chatManagement.getChatMessages().size());

        SendChatMessageRequest sendChatMessageRequest2 = new SendChatMessageRequest(defaultUser, "Test2", LocalTime.now());
        post(sendChatMessageRequest2);
        when(chatStore.getChatMessages()).thenReturn(List.of(sendChatMessageRequest.getChatMessage(), sendChatMessageRequest2.getChatMessage()));

        assertEquals(2, chatManagement.getChatMessages().size());
    }

    /**
     * Tests the onChatRequest method
     */
    @Test
    void onLobbyChatRequestTest() {
        SendUserLobbyChatMessageRequest sendLobbyChatMessageRequest = new SendUserLobbyChatMessageRequest(defaultLobby, defaultUser, "Test", LocalTime.now());
        post(sendLobbyChatMessageRequest);

        when(chatStore.getLobbyChatMessages(defaultLobby)).thenReturn(List.of(sendLobbyChatMessageRequest.getChatMessage()));

        assertNotNull(chatManagement.getLobbyChatMessages(this.defaultLobby));
        assertEquals(1, chatManagement.getLobbyChatMessages(this.defaultLobby).size());

        SendUserLobbyChatMessageRequest sendLobbyChatMessageRequest2 = new SendUserLobbyChatMessageRequest(defaultLobby, defaultUser, "Test", LocalTime.now());
        post(sendLobbyChatMessageRequest2);

        when(chatStore.getLobbyChatMessages(defaultLobby)).thenReturn(List.of(sendLobbyChatMessageRequest.getChatMessage(), sendLobbyChatMessageRequest2.getChatMessage()));

        assertEquals(2, chatManagement.getLobbyChatMessages(this.defaultLobby).size());
    }

    /**
     * Tests the LobbyDroppedServerInternalMessage method
     */
    @Test
    void onLobbyDroppedServerInternalMessage() {
        chatManagement.addLobbyChatMessage(defaultLobby, "Test");
        LobbyDroppedServerInternalMessage lobbyDroppedServerInternalMessage = new LobbyDroppedServerInternalMessage(defaultLobby);
        post(lobbyDroppedServerInternalMessage);

        assertThat(chatManagement.getLobbyChatMessages(this.defaultLobby)).isNullOrEmpty();


    }

    /**
     * Tests the onRetrieveChatRequest method
     */
    @Test
    void onRetrieveChatRequestTest() {
        RetrieveAllChatMessagesRequest retrieveChatRequest = new RetrieveAllChatMessagesRequest();
        post(retrieveChatRequest);

        assertNotNull(chatManagement.getChatMessages());
    }

    /**
     * Tests the onRetrieveChatRequest method
     */
    @Test
    void onRetrieveLobbyRequestTest() {
        chatManagement.addLobbyChatMessage(defaultLobby, "Test");
        RetrieveAllChatMessagesRequest retrieveChatRequest = new RetrieveAllChatMessagesRequest(this.defaultLobby);
        post(retrieveChatRequest);

        assertNotNull(chatManagement.getLobbyChatMessages(retrieveChatRequest.getLobby()));
    }
}