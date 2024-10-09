package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.player.Player;

/**
 * Represents a request to kick a user from a lobby.
 * This class extends AbstractLobbyRequest and is used to encapsulate the details
 * of the kick action, including the lobby from which the user is being removed
 * and the user to be kicked. It serves as a data transfer object for the
 * event bus to process the kick action appropriately.
 *
 * @since 2024-09-23
 */
public class KickPlayerLobbyRequest extends AbstractPlayerLobbyRequest {

    /**
     * Constructs a LobbyKickUserRequest with the specified lobby and user.
     * This constructor initializes the request with the lobby from which
     * the user will be kicked and the user to be removed.
     *
     * @param lobby The lobby from which the user will be kicked.
     * @param player The player to be kicked from the lobby.
     * @since 2024-09-23
     */
    public KickPlayerLobbyRequest(Lobby lobby, Player player) {
        super(lobby, player);
    }
}
