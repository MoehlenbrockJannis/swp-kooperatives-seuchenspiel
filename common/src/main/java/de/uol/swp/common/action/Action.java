package de.uol.swp.common.action;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.util.Command;

/**
 * Represents an action that can be executed by a player within a game.
 * Actions are executed within the context of a game and involve a player performing
 * an operation that modifies the game state. This interface provides methods to set
 * and retrieve the player and game involved in the action.
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
public interface Action extends Command {

    /**
     * <p>
     * Retrieves the player who is executing this action.
     * </p>
     *
     * @return the {@link Player} executing the action, or null if not set.
     */
    Player getExecutingPlayer();

    /**
     * <p>
     * Sets the player who is executing this action.
     * </p>
     *
     * @param player the {@link Player} to set as the executing player.
     */
    void setExecutingPlayer(Player player);

    /**
     * <p>
     * Retrieves the game in which this action is being executed.
     * </p>
     *
     * @return the {@link Game} associated with this action, or null if not set.
     */
    Game getGame();

    /**
     * <p>
     * Sets the game in which this action is being executed.
     * </p>
     *
     * @param game the {@link Game} to associate with this action.
     */
    void setGame(Game game);

    /**
     * <p>
     * Determines whether this action is currently available to be filled.
     * This may depend on the current game state, player state, or other factors.
     * </p>
     *
     * @return true if the action is available to be filled, false otherwise.
     */
    boolean isAvailable();

    /**
     * <p>
     * Determines whether this action is currently available for execution.
     * This may depend on the current game state, player state, or other factors.
     * </p>
     *
     * @return true if the action is available for execution, false otherwise.
     */
    boolean isExecutable();
}
