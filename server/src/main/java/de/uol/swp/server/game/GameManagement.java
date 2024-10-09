package de.uol.swp.server.game;

import com.google.inject.Inject;
import de.uol.swp.common.card.InfectionCard;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyStatus;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.role.RoleManagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Manages the game
 */
public class GameManagement {
    @Inject
    private LobbyManagement lobbyManagement;
    @Inject
    private RoleManagement roleManagement;

    private final List<Game> games = new ArrayList<>();
    private final Random random = new Random();

    /**
     * Creates a game
     *
     * @param lobby    The lobby from which the game is to be created
     * @param mapType  The mapType of the game
     * @param plagues  The plagues of the game
     * @return The created game
     */
    public Game createGame(Lobby lobby, MapType mapType, List<Plague> plagues) {
        lobbyManagement.updateLobbyStatus(lobby, LobbyStatus.RUNNING);
        roleManagement.assignRolesToPlayers(lobby);

        Game newGame = new Game(lobby, mapType, new ArrayList<>(lobby.getPlayers()), plagues);
        newGame.setId(generateUniqueGameId());
        return newGame;
    }

    /**
     * Adds a game to the list of managed games.
     *
     * @param game The game to be added
     */
    public void addGame(Game game) {
        games.add(game);
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
     * Discards a player card by pushing it onto the player's discard stack.
     *
     * @param game       The game from which the player card is to be discarded
     * @param playerCard The player card to be discarded
     */
    public void discardPlayerCard(Game game, PlayerCard playerCard) {
        game.getPlayerDiscardStack().push(playerCard);
    }

    /**
     * Draws an infection card from the top of the infection draw stack of a game.
     *
     * @param game The game from which the infection card is to be drawn
     */
    public InfectionCard drawInfectionCardFromTheTop(Game game) {
        return game.getInfectionDrawStack().pop();
    }

    /**
     * Draws an infection card from the bottom of the infection draw stack of a game.
     *
     * @param game The game from which the infection card is to be drawn
     */
    public InfectionCard drawInfectionCardFromTheBottom(Game game) {
       return game.getInfectionDrawStack().removeFirstCard();
    }

    public void discardInfectionCard(Game game, InfectionCard infectionCard) {
        game.getInfectionDiscardStack().push(infectionCard);
    }

    /**
     * Updates a game in the list of managed games.
     *
     * @param game The game to be updated
     */
    public void updateGame(Game game) {
        this.games.set(this.games.indexOf(game), game);
    }

    /**
     * Retrieves a game from the list of managed games.
     *
     * @param game The game to be retrieved
     * @return An Optional containing the game if found, otherwise an empty Optional
     */
    public Optional<Game> getGame(Game game) {
        try {
            Game gameInStore = games.get(games.indexOf(game));
            return Optional.of(gameInStore);
        }catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    /**
     * Generates a unique game ID.
     *
     * @return A unique game ID
     */
    int generateUniqueGameId() {
        var ref = new Object() {
            int uniqueGameId;
        };
        do {

            ref.uniqueGameId = this.random.nextInt(100000);
        } while (games.stream().anyMatch(game -> game.getId() == ref.uniqueGameId));
        return ref.uniqueGameId;
    }
}
