package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.player.Player;

/**
 * Request sent to the server when a user wants to leave a lobby
 *
 * @see AbstractUserLobbyRequest
 * @see de.uol.swp.common.user.User
 * @author Marco Grawunder
 * @since 2019-10-08
 */
public class LeavePlayerLobbyRequest extends AbstractPlayerLobbyRequest {

    /**
     * Constructor
     *
     * @param lobby lobby
     * @param player player who wants to leave the lobby
     * @since 2019-10-08
     */
    public LeavePlayerLobbyRequest(Lobby lobby, Player player) {
        super(lobby, player);
    }

}
