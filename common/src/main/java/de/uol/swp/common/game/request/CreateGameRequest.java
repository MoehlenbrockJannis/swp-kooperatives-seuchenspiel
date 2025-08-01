package de.uol.swp.common.game.request;

import de.uol.swp.common.game.GameDifficulty;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.message.request.AbstractRequestMessage;
import de.uol.swp.common.plague.Plague;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

/**
 * Represents a request to create a new game.
 * <p>
 * This request includes all necessary details for initializing a game, such as the lobby, map type, plagues,
 * game difficulty, and other game-specific configurations.
 * </p>
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
public class CreateGameRequest extends AbstractRequestMessage {

    private final Lobby lobby;
    private final MapType mapType;
    private final List<Plague> plagues;
    private int maxHandCards;
    private int numberOfPlagueCubesPerColor;
    private int numberOfResearchLaboratories;
    private final GameDifficulty difficulty;
    private int numberOfInfectionCardsDrawnPerPhaseOfInitialPlagueCubeDistribution;
    private int numberOfPlagueCubesAddedToEveryFieldInFirstPhaseOfInitialPlagueCubeDistribution;
    private int maxNumberOfPlagueCubesPerField;
    private int numberOfActionsPerTurn;
    private int numberOfPlayerCardsToDrawPerTurn;

    /**
     * Constructor of the CreateGameRequest
     *
     * @param lobby   The lobby from which the game is to be created
     * @param mapType The mapType of the game
     * @param plagues The plagues of the game
     * @param difficulty The difficulty of the game
     */
    public CreateGameRequest(Lobby lobby, MapType mapType, List<Plague> plagues, GameDifficulty difficulty) {
        this.lobby = lobby;
        this.mapType = mapType;
        this.plagues = plagues;
        this.difficulty = difficulty;
    }

}
