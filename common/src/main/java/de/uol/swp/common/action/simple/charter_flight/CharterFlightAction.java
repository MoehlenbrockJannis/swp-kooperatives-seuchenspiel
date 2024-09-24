package de.uol.swp.common.action.simple.charter_flight;

import de.uol.swp.common.action.advanced.DiscardCardAction;
import de.uol.swp.common.action.simple.MoveAction;
import de.uol.swp.common.card.CityCard;

/**
 * This class represent and realized the charter flight action.
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
public class CharterFlightAction extends MoveAction implements DiscardCardAction {
    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public void execute() {

    }

    @Override
    public CityCard getDiscardedCard() {
        return null;
    }
}
