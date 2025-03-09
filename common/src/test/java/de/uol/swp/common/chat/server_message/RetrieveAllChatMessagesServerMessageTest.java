package de.uol.swp.common.chat.server_message;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for the RetrieveAllChatMessagesServerMessage
 *
 * @see RetrieveAllChatMessagesServerMessage
 */
public class RetrieveAllChatMessagesServerMessageTest {

    /**
     * Test for creation of the RetrieveAllChatMessagesServerMessage
     *
     * This test checks if the chatMessages of the RetrieveAllChatMessagesServerMessage gets
     * set correctly during the creation of the message
     *
     * @since 2023-05-14
     */
    @Test
    void createChatMessage() {

        ArrayList<String> chatMessages = new ArrayList<>();
        chatMessages.add("Test");
        chatMessages.add("Test2");

        RetrieveAllChatMessagesServerMessage message = new RetrieveAllChatMessagesServerMessage(chatMessages);

        assertEquals(chatMessages, message.getChatMessages());
    }
}
