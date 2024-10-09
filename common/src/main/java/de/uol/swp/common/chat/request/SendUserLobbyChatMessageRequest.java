package de.uol.swp.common.chat.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.request.AbstractUserLobbyRequest;
import de.uol.swp.common.user.User;
import lombok.Getter;

import java.time.LocalTime;

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

