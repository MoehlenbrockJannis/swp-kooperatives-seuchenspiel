package de.uol.swp.server.game;

import com.google.inject.Inject;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.GameDifficulty;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyStatus;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.server.game.store.GameStore;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.player.turn.PlayerTurnManagement;
import de.uol.swp.server.role.RoleManagement;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Manages the game
 */
@Setter(onMethod = @__(@Inject))
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class GameManagement {
    private LobbyManagement lobbyManagement;
    private PlayerTurnManagement playerTurnManagement;
    private RoleManagement roleManagement;
    private final GameStore gameStore;


    /**
     * Creates a game
     *
     * @param lobby    The lobby from which the game is to be created
     * @param mapType  The mapType of the game
     * @param plagues  The plagues of the game
     * @param difficulty The difficulty level of the game, determines the number of epidemic cards
     * @return The created game
     */
    public Game createGame(Lobby lobby, MapType mapType, List<Plague> plagues, GameDifficulty difficulty) {
        lobbyManagement.updateLobbyStatus(lobby, LobbyStatus.RUNNING);
        roleManagement.assignRolesToPlayers(lobby);

        Game newGame = new Game(lobby, mapType, new ArrayList<>(lobby.getPlayers()),
                plagues, difficulty);
        newGame.addPlayerTurn(playerTurnManagement.createPlayerTurn(newGame));
        return newGame;
    }

    /**
     * Adds a game to the list of managed games.
     *
     * @param game The game to be added
     */
    public void addGame(Game game) {
        this.gameStore.addGame(game);
    }

    /**
     * Draws a player card from the player draw stack of a game.
     *
     * @param game The game from which the player card is to be drawn
     * @return The drawn player card
     */
    public PlayerCard drawPlayerCard(Game game) {
        return game.getPlayerDrawStack().pop();

    }

    /**
     * Updates a game in the list of managed games.
     *
     * @param game The game to be updated
     */
    public void updateGame(Game game) {
        this.gameStore.updateGame(game);
    }

    /**
     * Retrieves a game from the list of managed games.
     *
     * @param game The game to be retrieved
     * @return An Optional containing the game if found, otherwise an empty Optional
     */
    public Optional<Game> getGame(Game game) {
        return this.gameStore.getGame(game);
    }
}
