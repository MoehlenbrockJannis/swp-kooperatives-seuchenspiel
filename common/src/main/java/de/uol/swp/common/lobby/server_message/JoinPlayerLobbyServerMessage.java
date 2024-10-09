package de.uol.swp.common.lobby.server_message;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.player.Player;

/**
 * Represents a server message indicating that a player has joined a lobby.
 *
 * This message is sent to notify clients when a player successfully joins
 * a lobby. It extends {@link AbstractPlayerLobbyServerMessage} and
 * contains the relevant information regarding the lobby and the player.
 *
 * @see AbstractPlayerLobbyServerMessage
 * @see Player
 * @see Lobby
 * @since 2024-10-06
 */
public class JoinPlayerLobbyServerMessage extends AbstractPlayerLobbyServerMessage {

    /**
     * Constructs a {@code JoinPlayerLobbyServerMessage} with the specified lobby and player.
     *
     * @param lobby The lobby that the player has joined.
     * @param player The player who has joined the lobby.
     * @since 2024-10-06
     */
    public JoinPlayerLobbyServerMessage(Lobby lobby, Player player) {
        super(lobby, player);
    }
}
