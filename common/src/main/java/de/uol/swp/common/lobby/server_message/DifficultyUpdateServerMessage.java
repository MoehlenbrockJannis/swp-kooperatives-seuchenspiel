package de.uol.swp.common.lobby.server_message;

import de.uol.swp.common.game.GameDifficulty;
import de.uol.swp.common.lobby.Lobby;
import lombok.Getter;

/**
 * Message sent by the server to update the difficulty in a lobby
 *
 * This message is sent to all clients in a lobby when the difficulty level
 * (number of epidemic cards) has been changed by the lobby owner.
 *
 * @see AbstractLobbyServerMessage
 */
@Getter
public class DifficultyUpdateServerMessage extends AbstractLobbyServerMessage {
    private final GameDifficulty difficulty;

    public DifficultyUpdateServerMessage(Lobby lobby, GameDifficulty difficulty) {
        super(lobby);
        this.difficulty = difficulty;
    }
}