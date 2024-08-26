package de.uol.swp.common.chat.message.message;

import de.uol.swp.common.message.AbstractServerMessage;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public class ChatMessage extends AbstractServerMessage {

    private final ArrayList<String> chatMessages = new ArrayList<>();

    /**
     * Creates a new ChatMessage
     * @param chatRequest The chatRequest
     */
    public ChatMessage(ArrayList<String> chatRequest) {
        this.chatMessages.addAll(chatRequest);
    }

}
