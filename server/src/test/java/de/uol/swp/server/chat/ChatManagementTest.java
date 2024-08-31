package de.uol.swp.server.chat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test für die ChatManagement Klasse
 */
public class ChatManagementTest {

    private ChatManagement chatManagement;

    /**
     * Setzt die Instanz von ChatManagement
     */
    @BeforeEach
    void setUp() {
        chatManagement = new ChatManagement();
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
}