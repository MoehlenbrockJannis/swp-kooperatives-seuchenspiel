package de.uol.swp.common.action;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.player.Player;
import lombok.Getter;
import lombok.Setter;

/**
 * The {@code GeneralAction} class serves as a base class for actions that can be executed by a player
 * within the context of a game. It implements the {@link Action} interface and provides common fields
 * for the executing player and the game.
 * <p>
 * Subclasses should extend this class to define specific actions.
 * </p>
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
@Getter
@Setter
public abstract class GeneralAction implements Action {

    /**
     * The player executing the action.
     */
    private Player executingPlayer;

    /**
     * The game in which the action is executed.
     */
    private Game game;

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public void initWithGame(final Game game) {
        this.game = game;
        this.executingPlayer = game.findPlayer(this.executingPlayer).orElseThrow();
    }
}


