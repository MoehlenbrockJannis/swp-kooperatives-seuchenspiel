package de.uol.swp.server.chat;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.chat.store.ChatStore;
import de.uol.swp.server.chat.store.MainMemoryBasedChatStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test für die ChatManagement Klasse
 */
public class ChatManagementTest {

    private ChatManagement chatManagement;
    private User user = new UserDTO("TestUser", "TestUser", "TestUser@Mail.com");
    private Lobby lobby = new LobbyDTO("TestLobby", user, 2, 4);
    private  ChatManagement chatManagement2;
    private ChatStore chatStore = mock(MainMemoryBasedChatStore.class);

    /**
     * Setzt die Instanz von ChatManagement
     */
    @BeforeEach
    void setUp() {

        chatManagement = new ChatManagement(chatStore);
        chatManagement2 = new ChatManagement(chatStore);
        chatManagement2.addLobbyChatMessage(lobby, "Hello World!");
    }

    /**
     * Testet das Hinzufügen einer Chatnachricht
     */
    @Test
    void testAddChatMessage() {
        String message = "Hello World!";
        chatManagement.addChatMessage(message);
        when(chatStore.getChatMessages()).thenReturn(List.of(message));



        List<String> messages = chatManagement.getChatMessages();
        assertEquals(1, messages.size());
        assertEquals(message, messages.get(0));
    }

    /**
     * Testet das Hinzufügen einer Chatnachricht
     */
    @Test
    void testAddLobbyChatMessageTrue() {
        String message = "Hello World!";
        chatManagement.addLobbyChatMessage(lobby, message);

        when(chatStore.getLobbyChatMessages(lobby)).thenReturn(List.of(message));

        List<String> messages = chatManagement.getLobbyChatMessages(lobby);
        assertEquals(1, messages.size());
        assertEquals(message, messages.get(0));
    }


    /**
     * Testet das Abrufen der Chatnachrichten
     */
    @Test
    void testGetChatMessages() {
        String message1 = "Hello World!";
        String message2 = "Another message";
        chatManagement.addChatMessage(message1);
        chatManagement.addChatMessage(message2);

        when(chatStore.getChatMessages()).thenReturn(List.of(message1, message2));

        List<String> messages = chatManagement.getChatMessages();
        assertEquals(2, messages.size());
        assertTrue(messages.contains(message1));
        assertTrue(messages.contains(message2));
    }

    /**
     * Testet das Abrufen der Chatnachrichten der Lobby
     */
    @Test
    void testGetLobbyChatMessages() {
        String message1 = "Hello World!";
        String message2 = "Another message";
        chatManagement.addLobbyChatMessage(lobby, message1);
        chatManagement.addLobbyChatMessage(lobby, message2);

        when(chatStore.getLobbyChatMessages(lobby)).thenReturn(List.of(message1, message2));

        List<String> messages = chatManagement.getLobbyChatMessages(lobby);
        assertEquals(2, messages.size());
        assertTrue(messages.contains(message1));
        assertTrue(messages.contains(message2));
    }

    /**
     * Testet das Abrufen der Chatnachrichten der Lobby
     */
    @Test
    void testRemoveLobbyChatMessages() {
        String message1 = "Hello World!";
        String message2 = "Another message";
        chatManagement.addLobbyChatMessage(lobby, message1);
        chatManagement.addLobbyChatMessage(lobby, message2);
        chatManagement.removeLobbyChatMessages(lobby);

        when(chatStore.getLobbyChatMessages(lobby)).thenReturn(null);

        List<String> messages = chatManagement.getLobbyChatMessages(lobby);
        assertThat(messages).isNullOrEmpty();
    }
}