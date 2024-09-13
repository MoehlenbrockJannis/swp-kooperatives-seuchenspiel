package de.uol.swp.common.chat.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.request.AbstractLobbyRequest;
import de.uol.swp.common.user.User;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class SendLobbyChatMessageRequest extends AbstractLobbyRequest {
    private final String chatMessage;
    private final LocalTime timestamp;

    public SendLobbyChatMessageRequest(final Lobby lobby, final User user, final String chatMessage, final LocalTime timestamp) {
        super(lobby, user);
        this.chatMessage = chatMessage;
        this.timestamp = timestamp;
    }
}

