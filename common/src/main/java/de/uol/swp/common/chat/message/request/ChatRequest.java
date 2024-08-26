package de.uol.swp.common.chat.message.request;

import de.uol.swp.common.message.AbstractRequestMessage;

import java.time.LocalTime;

public class ChatRequest extends AbstractRequestMessage {


    private final String user;
    private final String chatMessage;
    private final LocalTime timestamp;

    /**
     * Constructor of the ChatRequest
     *
     * @param user        the user who sends the message
     * @param chatMessage the message
     * @param timestamp   the time when the message was sent
     */
    public ChatRequest(String user, String chatMessage, LocalTime timestamp) {
        this.user = user;
        this.chatMessage = chatMessage;
        this.timestamp = timestamp;
    }

    /**
     * Returns the chat message
     *
     * @return the chat message
     */
    public String getChatMessage() {
        return "[" + timestamp + "] " + user + ": " + chatMessage;
    }
}
