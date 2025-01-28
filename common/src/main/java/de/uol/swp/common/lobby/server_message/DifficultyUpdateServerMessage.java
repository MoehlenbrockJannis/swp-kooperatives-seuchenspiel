package de.uol.swp.common.lobby.server_message;

import de.uol.swp.common.lobby.Lobby;
import lombok.Getter;

/**
 * Message sent by the server to update the difficulty in a lobby
 *
 * This message is sent to all clients in a lobby when the difficulty level
 * (number of epidemic cards) has been changed by the lobby owner.
 *
 * @see AbstractLobbyServerMessage
 * @since 2025-01-25
 */
@Getter
public class DifficultyUpdateServerMessage extends AbstractLobbyServerMessage {
    private final int numberOfEpidemicCards;

    /**
     * Constructor
     *
     * @param lobby The lobby in which the difficulty was updated
     * @param numberOfEpidemicCards The new number of epidemic cards
     */
    public DifficultyUpdateServerMessage(Lobby lobby, int numberOfEpidemicCards) {
        super(lobby);
        this.numberOfEpidemicCards = numberOfEpidemicCards;
    }
}