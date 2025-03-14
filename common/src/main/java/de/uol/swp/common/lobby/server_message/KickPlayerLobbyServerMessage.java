package de.uol.swp.common.lobby.server_message;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.player.Player;

/**
 * Represents a server message indicating that a user has been kicked from a lobby.
 * This class extends AbstractLobbyServerMessage and encapsulates the details
 * of the kick action, including the lobby from which the user was removed
 * and the user who was kicked. It is used to communicate the kick action
 * to other components in the system.
 */
public class KickPlayerLobbyServerMessage extends AbstractPlayerLobbyServerMessage {

    /**
     * Constructs a LobbyKickUserServerMessage with the specified lobby and user.
     * This constructor initializes the message to indicate that a user has been kicked
     * from the given lobby. It inherits the lobby and user details from the
     * AbstractLobbyServerMessage superclass.
     *
     * @param lobby The lobby from which the user was kicked.
     * @param player The player who was kicked from the lobby.
     */
    public KickPlayerLobbyServerMessage(Lobby lobby, Player player) {
        super(lobby, player);
    }
}
