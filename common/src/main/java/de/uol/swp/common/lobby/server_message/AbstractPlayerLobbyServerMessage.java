package de.uol.swp.common.lobby.server_message;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.player.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Base class for all server messages related to a player in a lobby.
 *
 * This class extends {@link AbstractLobbyServerMessage} and adds support for messages
 * that involve a specific {@link Player} within a lobby. It is used in scenarios
 * where server messages need to convey information related to both the lobby and a player,
 * such as when a player joins, leaves, or interacts within the lobby.
 *
 * Subclasses should be created for specific player-related lobby messages.
 *
 * @see AbstractLobbyServerMessage
 * @see de.uol.swp.common.player
 */
@NoArgsConstructor
public abstract class AbstractPlayerLobbyServerMessage extends AbstractLobbyServerMessage {
    @Getter
    protected Player player;

    /**
     * Constructs an {@code AbstractPlayerLobbyServerMessage} with the specified lobby and player.
     *
     * @param lobby  The lobby that this server message is related to.
     * @param player The player involved in the message.
     */
    protected AbstractPlayerLobbyServerMessage(Lobby lobby, Player player) {
        super(lobby);
        this.player = player;
    }
}
