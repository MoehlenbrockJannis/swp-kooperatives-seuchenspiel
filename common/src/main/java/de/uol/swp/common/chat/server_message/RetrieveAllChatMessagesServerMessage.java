package de.uol.swp.common.chat.server_message;

import de.uol.swp.common.message.server_message.AbstractServerMessage;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = false)
public class RetrieveAllChatMessagesServerMessage extends AbstractServerMessage {
    private final List<String> chatMessages = new ArrayList<>();

    /**
     * Creates a new RetrieveAllChatMessagesServerMessage
     *
     * @param chatRequest The chatRequest
     */
    public RetrieveAllChatMessagesServerMessage(final List<String> chatRequest) {
        this.chatMessages.addAll(chatRequest);
    }
}
