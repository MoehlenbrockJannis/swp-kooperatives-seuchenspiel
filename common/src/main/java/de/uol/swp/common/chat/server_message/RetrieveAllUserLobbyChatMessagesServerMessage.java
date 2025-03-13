package de.uol.swp.common.chat.server_message;

import de.uol.swp.common.lobby.server_message.AbstractUserLobbyServerMessage;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Server message containing all chat messages for a specific user within a lobby.
 * <p>
 * This message is sent by the server in response to a request to retrieve all chat messages
 * for a user in a specific lobby. It includes the list of chat messages as strings.
 * </p>
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public class RetrieveAllUserLobbyChatMessagesServerMessage extends AbstractUserLobbyServerMessage {
    private final List<String> chatMessage = new ArrayList<>();

    public RetrieveAllUserLobbyChatMessagesServerMessage(List<String> chatMessage) {
        this.chatMessage.addAll(chatMessage);
    }
}
