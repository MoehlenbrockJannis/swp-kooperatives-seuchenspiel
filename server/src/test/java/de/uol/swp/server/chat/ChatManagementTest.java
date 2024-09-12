package de.uol.swp.server.chat;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test für die ChatManagement Klasse
 */
public class ChatManagementTest {

    private ChatManagement chatManagement;
    private User user = new UserDTO("TestUser", "TestUser", "TestUser@Mail.com");
    private Lobby lobby = new LobbyDTO("TestLobby", user);
    private  ChatManagement chatManagement2;

    /**
     * Setzt die Instanz von ChatManagement
     */
    @BeforeEach
    void setUp() {
        chatManagement = new ChatManagement();
        chatManagement2 = new ChatManagement();
        chatManagement2.addLobbyChatMessage(lobby.getName(), "Hello World!");
    }

    /**
     * Testet das Hinzufügen einer Chatnachricht
     */
    @Test
    void testAddChatMessage() {
        String message = "Hello World!";
        chatManagement.addChatMessage(message);

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
        chatManagement.addLobbyChatMessage(lobby.getName(), message);

        List<String> messages = chatManagement.getLobbyChatMessages(lobby.getName());
        assertEquals(1, messages.size());
        assertEquals(message, messages.get(0));
    }

    /**
     * Testet das Hinzufügen einer Chatnachricht
     */
    @Test
    void testAddLobbyChatMessageFalse() {
        String message = "Hello World!";
        chatManagement2.addLobbyChatMessage(lobby.getName(), message);

        List<String> messages = chatManagement2.getLobbyChatMessages(lobby.getName());
        assertEquals(2, messages.size());
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
        chatManagement.addLobbyChatMessage(lobby.getName(), message1);
        chatManagement.addLobbyChatMessage(lobby.getName(), message2);

        List<String> messages = chatManagement.getLobbyChatMessages(lobby.getName());
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
        chatManagement.addLobbyChatMessage(lobby.getName(), message1);
        chatManagement.addLobbyChatMessage(lobby.getName(), message2);
        chatManagement.removeLobbyChatMessages(lobby.getName());

        List<String> messages = chatManagement.getLobbyChatMessages(lobby.getName());
        assertThat(messages).isNullOrEmpty();
    }
}