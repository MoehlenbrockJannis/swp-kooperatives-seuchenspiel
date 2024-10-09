package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.player.Player;

/**
 * A request message that indicates a player is attempting to join a lobby.
 *
 * This class extends {@link AbstractPlayerLobbyRequest},
 * serving as a specific message type to indicate that a player, rather than a user,
 * is joining the lobby. The request is typically posted to the event bus and processed
 * by the server to add the player to the lobby.
 *
 * @see AbstractPlayerLobbyRequest
 * @see de.uol.swp.common.lobby.Lobby
 * @since 2024-10-06
 */
public class JoinPlayerLobbyRequest extends AbstractPlayerLobbyRequest {

    /**
     * Constructs a {@code JoinPlayerLobbyRequest} with the specified lobby and player.
     *
     * @param lobby  The lobby that the player is attempting to join.
     * @param player The player who is joining the lobby.
     * @since 2024-10-06
     */
    public JoinPlayerLobbyRequest(Lobby lobby, Player player) {
        super(lobby, player);
    }
}
