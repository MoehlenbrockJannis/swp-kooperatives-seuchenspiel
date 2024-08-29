package de.uol.swp.common.chat.message;

import de.uol.swp.common.message.AbstractServerMessage;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = false)
public class ChatRetrieveAllMessagesMessage extends AbstractServerMessage {

    private final List<String> chatMessages = new ArrayList<>();

    /**
     * Creates a new ChatRetrieveAllMessagesMessage
     * @param chatRequest The chatRequest
     */
    public ChatRetrieveAllMessagesMessage(List<String> chatRequest) {
        this.chatMessages.addAll(chatRequest);
    }

}
