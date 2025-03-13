package de.uol.swp.common.action.simple.car;

import de.uol.swp.common.action.simple.MoveAction;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.player.Player;

import java.util.List;

/**
 * This class represent and realized the car action.
 */

public class CarAction extends MoveAction {

    @Override
    public String toString() {
        return "Auto (oder Fähre)";
    }

    @Override
    public Player getMovedPlayer() {
        return getExecutingPlayer();
    }

    /**
     * <p>
     *     Returns a {@link List} of all fields neighboring the current {@link Field}.
     *     Does not include the {@link Field} the moved {@link Player} is standing on.
     * </p>
     *
     * {@inheritDoc}
     *
     * @return {@link List} of all fields neighboring the one the moved {@link Player} is standing on
     * @see #getCurrentField()
     * @see Field#getNeighborFields()
     */
    @Override
    public List<Field> getAvailableFields() {
        return getCurrentField().getNeighborFields();
    }
}
