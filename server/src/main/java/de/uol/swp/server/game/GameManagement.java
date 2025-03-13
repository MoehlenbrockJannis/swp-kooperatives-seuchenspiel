package de.uol.swp.server.game;

import com.google.inject.Inject;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.GameDifficulty;
import de.uol.swp.common.game.GameEndReason;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyStatus;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.server.game.store.GameStore;
import de.uol.swp.server.game.turn.PlayerTurnManagement;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.role.RoleManagement;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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

    /**
     * Removes a game from the list of managed games.
     *
     * @param game The game to be removed
     */
    public void removeGame(Game game) {
        this.gameStore.removeGame(game);
    }

    /**
     * Determines why the game ended based on the current game state.
     *
     * @param game The game to analyze
     * @return The reason why the game ended
     */
    public GameEndReason determineGameEndReason(Game game) {
        return Stream.of(
                        this.checkForWin(game),
                        this.checkForEmptyDrawStack(game),
                        this.checkForMaxOutbreaks(game),
                        this.checkForNoPlagueCubes()
                )
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No valid game end reason found"));
    }

    /**
     * Checks if the game has been won by discovering all antidotes.
     *
     * @param game The game to check
     * @return An Optional containing the win reason if the game is won, otherwise empty
     */
    private Optional<GameEndReason> checkForWin(Game game) {
        return game.isGameWon()
                ? Optional.of(GameEndReason.ALL_ANTIDOTES_DISCOVERED)
                : Optional.empty();
    }

    /**
     * Checks if the player draw stack is empty, which is a loss condition.
     *
     * @param game The game to check
     * @return An Optional containing the empty draw stack reason if the stack is empty, otherwise empty
     */
    private Optional<GameEndReason> checkForEmptyDrawStack(Game game) {
        return game.getPlayerDrawStack().isEmpty()
                ? Optional.of(GameEndReason.NO_PLAYER_CARDS_LEFT)
                : Optional.empty();
    }

    /**
     * Checks if the maximum number of outbreaks has been reached, which is a loss condition.
     *
     * @param game The game to check
     * @return An Optional containing the max outbreaks reason if the limit is reached, otherwise empty
     */
    private Optional<GameEndReason> checkForMaxOutbreaks(Game game) {
        return game.getOutbreakMarker().isAtMaximumLevel()
                ? Optional.of(GameEndReason.MAX_OUTBREAKS_REACHED)
                : Optional.empty();
    }

    /**
     * Checks if there are no plague cubes left, which is a loss condition.
     *
     * @return An Optional containing the no plague cubes reason
     */
    private Optional<GameEndReason> checkForNoPlagueCubes() {
        return Optional.of(GameEndReason.NO_PLAGUE_CUBES_LEFT);
    }

    /**
     * Returns all stored games in {@link #gameStore}.
     *
     * @return {@link List} of all games
     */
    public List<Game> findAllGames() {
        return gameStore.getAllGames();
    }
}
