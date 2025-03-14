package de.uol.swp.common.chat.server_message;

import de.uol.swp.common.message.server_message.AbstractServerMessage;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Server message that contains all chat messages for a specific request.
 * <p>
 * This message is sent by the server in response to a request to retrieve all chat messages.
 * It contains the complete list of chat messages as strings.
 * </p>
 */
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
