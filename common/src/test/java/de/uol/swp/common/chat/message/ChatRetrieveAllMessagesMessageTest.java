package de.uol.swp.common.chat.message;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for the ChatRetrieveAllMessagesMessage
 *
 * @see ChatRetrieveAllMessagesMessage
 */
public class ChatRetrieveAllMessagesMessageTest {

    /**
     * Test for creation of the ChatRetrieveAllMessagesMessage
     *
     * This test checks if the chatMessages of the ChatRetrieveAllMessagesMessage gets
     * set correctly during the creation of the message
     *
     * @since 2023-05-14
     */
    @Test
    void createChatMessage() {

        ArrayList<String> chatMessages = new ArrayList<>();
        chatMessages.add("Test");
        chatMessages.add("Test2");

        ChatRetrieveAllMessagesMessage message = new ChatRetrieveAllMessagesMessage(chatMessages);

        assertEquals(chatMessages, message.getChatMessages());
    }
}
