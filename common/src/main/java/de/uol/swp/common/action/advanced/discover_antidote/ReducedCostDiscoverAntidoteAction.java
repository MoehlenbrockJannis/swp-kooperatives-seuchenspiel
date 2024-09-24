package de.uol.swp.common.action.advanced.discover_antidote;

import de.uol.swp.common.action.RoleAction;

/**
 * The {@code ReducedCostDiscoverAntidoteAction} class represents a specialized action for discovering an antidote
 * at a reduced cost. It extends {@link DiscoverAntidoteAction} and implements {@link RoleAction}, indicating that
 * this action is associated with a specific role in the game.
 * <p>
 * The reduced cost is reflected by requiring fewer discarded cards than a standard {@code DiscoverAntidoteAction}.
 * </p>
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
public class ReducedCostDiscoverAntidoteAction extends DiscoverAntidoteAction implements RoleAction {

    /**
     * Constructs a new {@code ReducedCostDiscoverAntidoteAction} with the specified reduced number of discarded cards.
     *
     * @param requiredAmountOfDiscardedCards the reduced number of cards required to discover the antidote
     */
    protected ReducedCostDiscoverAntidoteAction(int requiredAmountOfDiscardedCards) {
        super(requiredAmountOfDiscardedCards);
    }

}
