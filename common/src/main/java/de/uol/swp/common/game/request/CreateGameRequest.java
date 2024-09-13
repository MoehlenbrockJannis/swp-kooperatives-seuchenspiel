package de.uol.swp.common.game.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.message.request.AbstractRequestMessage;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.player.Player;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
public class CreateGameRequest extends AbstractRequestMessage {

    private final Lobby lobby;
    private final MapType mapType;
    private final List<Player> players;
    private final List<Plague> plagues;
    private int maxHandCards;
    private int numberOfPlagueCubesPerColor;
    private int numberOfResearchLaboratories;
    private int numberOfEpidemicCards;
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
     * @param players The players of the game
     * @param plagues The plagues of the game
     */
    public CreateGameRequest(Lobby lobby, MapType mapType, List<Player> players, List<Plague> plagues) {
        this.lobby = lobby;
        this.mapType = mapType;
        this.players = players;
        this.plagues = plagues;
    }

}
