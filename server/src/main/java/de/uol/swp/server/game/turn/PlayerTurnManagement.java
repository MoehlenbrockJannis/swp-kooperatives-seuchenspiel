package de.uol.swp.server.game.turn;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.game.turn.PlayerTurn;

/**
 * Management class for {@link PlayerTurn}.
 * All service logic for it is encapsulated here.
 *
 * @see PlayerTurn
 * @author Tom Weelborg
 * @since 2024-10-18
 */
public class PlayerTurnManagement {
    /**
     * Returns a new {@link PlayerTurn} with references to
     *  the given {@link Game},
     *  its current {@link Player},
     *  the number of actions to do during that turn and
     *  the number of player cards to draw during it.
     *
     * @return new {@link PlayerTurn} object
     * @see Game#getCurrentPlayer()
     * @see Game#getNumberOfActionsPerTurn()
     * @see Game#getNumberOfPlayerCardsToDrawPerTurn()
     */
    public PlayerTurn createPlayerTurn(final Game game) {
        return new PlayerTurn(
                game,
                game.getCurrentPlayer(),
                game.getNumberOfActionsPerTurn(),
                game.getNumberOfPlayerCardsToDrawPerTurn(),
                game.getNumberOfInfectionCardsToDrawPerTurn()
        );
    }

    /**
     * Creates a new {@link PlayerTurn} for given {@link Game}
     * by first increases the index of the current {@link Player}
     * and then creating a {@link PlayerTurn}.
     *
     * @param game {@link Game} to start a new {@link PlayerTurn} for
     * @see Game#nextPlayer()
     * @see #createPlayerTurn(Game)
     */
    public void startNewPlayerTurn(final Game game) {
        game.nextPlayer();
        game.addPlayerTurn(createPlayerTurn(game));
    }
}
