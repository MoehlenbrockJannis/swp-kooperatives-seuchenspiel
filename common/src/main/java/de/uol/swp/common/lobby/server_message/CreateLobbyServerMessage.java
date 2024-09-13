package de.uol.swp.common.lobby.server_message;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;

public class CreateLobbyServerMessage extends AbstractLobbyServerMessage {

    public CreateLobbyServerMessage(Lobby lobby, User user) {
        super(lobby, user);
    }

}
