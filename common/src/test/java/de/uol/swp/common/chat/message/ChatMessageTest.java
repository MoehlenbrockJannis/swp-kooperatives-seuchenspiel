package de.uol.swp.common.chat.message;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for the ChatMessage
 *
 * @see de.uol.swp.common.chat.message.ChatMessage
 */
public class ChatMessageTest {

    /**
     * Test for creation of the ChatMessage
     *
     * This test checks if the chatMessages of the ChatMessage gets
     * set correctly during the creation of the message
     *
     * @since 2023-05-14
     */
    @Test
    void createChatMessage() {

        ArrayList<String> chatMessages = new ArrayList<>();
        chatMessages.add("Test");
        chatMessages.add("Test2");

        ChatMessage message = new ChatMessage(chatMessages);

        assertEquals(chatMessages, message.getChatMessages());
    }
}
