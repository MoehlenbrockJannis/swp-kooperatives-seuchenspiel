package de.uol.swp.server.chat.store;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MainMemoryBasedChatStoreTest {

    private ChatStore chatStore;

    @BeforeEach
    void setUp() {
        this.chatStore = new MainMemoryBasedChatStore();
    }

    @Test
    @DisplayName("Test if a chat message can be added")
    void addChatMessage() {
        chatStore.addChatMessage("Test");
        assertThat(chatStore.getChatMessages()).contains("Test");
    }

    @Test
    @DisplayName("Test if a lobby chat message can be added")
    void addLobbyChatMessage() {
        Lobby mockLobby = mock(LobbyDTO.class);
        when(mockLobby.getName()).thenReturn("Test");

        chatStore.addLobbyChatMessage(mockLobby, "Test");

        assertThat(chatStore.getLobbyChatMessages(mockLobby)).contains("Test");
    }

    @Test
    @DisplayName("Test if all lobby messages are retrieved")
    void getLobbyChatMessages() {
        Lobby mockLobby = mock(LobbyDTO.class);
        when(mockLobby.getName()).thenReturn("Test");
        chatStore.addLobbyChatMessage(mockLobby, "Test");

        assertThat(chatStore.getLobbyChatMessages(mockLobby)).contains("Test");
    }

    @Test
    @DisplayName("Test if all lobby messages are removed")
    void removeLobbyChatMessages() {

        Lobby mockLobby = mock(LobbyDTO.class);
        when(mockLobby.getName()).thenReturn("Test");

        chatStore.addLobbyChatMessage(mockLobby, "Test");

        assertThat(chatStore.getLobbyChatMessages(mockLobby)).contains("Test");

        chatStore.removeLobbyChatMessages(mockLobby);

        assertThat(chatStore.getLobbyChatMessages(mockLobby)).isEmpty();

    }

    @Test
    @DisplayName("Test if all global chat messages are retrieved")
    void getChatMessages() {
        chatStore.addChatMessage("Test");

        assertThat(chatStore.getChatMessages()).contains("Test");


    }
}