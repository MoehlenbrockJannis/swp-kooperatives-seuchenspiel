package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.player.Player;
import lombok.Getter;

/**
 * Represents a request related to a specific lobby and a player.
 *
 * This abstract class extends {@link AbstractLobbyRequest} and includes an additional
 * {@link Player} object, representing the player involved in the request. It is used as a
 * base class for more specific player-related lobby requests, such as adding or removing players
 * from the lobby.
 */
@Getter
public abstract class AbstractPlayerLobbyRequest extends AbstractLobbyRequest {
    protected Player player;

    /**
     * Constructor for creating an {@code AbstractPlayerLobbyRequest}.
     *
     * @param lobby  The lobby that the request is related to.
     * @param player The player involved in the request.
     * @since 2024-10-06
     */
    protected AbstractPlayerLobbyRequest(Lobby lobby, Player player) {
        super(lobby);
        this.player = player;
    }
}
