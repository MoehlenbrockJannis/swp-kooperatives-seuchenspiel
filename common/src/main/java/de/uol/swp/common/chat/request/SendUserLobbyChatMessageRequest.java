package de.uol.swp.common.chat.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.request.AbstractUserLobbyRequest;
import de.uol.swp.common.user.User;
import lombok.Getter;

import java.time.LocalTime;

/**
 * Request message to send a user-specific chat message within a lobby.
 * <p>
 * This request includes the lobby, user information, the chat message content, and the timestamp
 * representing when the message was sent.
 * </p>
 */
@Getter
public class SendUserLobbyChatMessageRequest extends AbstractUserLobbyRequest {
    private final String chatMessage;
    private final LocalTime timestamp;

    public SendUserLobbyChatMessageRequest(final Lobby lobby, final User user, final String chatMessage, final LocalTime timestamp) {
        super(lobby, user);
        this.chatMessage = chatMessage;
        this.timestamp = timestamp;
    }
}

