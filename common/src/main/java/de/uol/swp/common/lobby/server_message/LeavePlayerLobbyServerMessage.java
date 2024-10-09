package de.uol.swp.common.lobby.server_message;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.player.Player;

/**
 * Message sent by the server when a user successfully leaves a lobby
 *
 * @see AbstractUserLobbyServerMessage
 * @see de.uol.swp.common.user.User
 * @author Marco Grawunder
 * @since 2019-10-08
 */
public class LeavePlayerLobbyServerMessage extends AbstractPlayerLobbyServerMessage {
    /**
     * Constructor
     *
     * @param lobby lobby
     * @param player player who left the lobby
     * @since 2019-10-08
     */
    public LeavePlayerLobbyServerMessage(Lobby lobby, Player player) {
        super(lobby, player);
    }
}
