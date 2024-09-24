package de.uol.swp.common.action.simple.shuttle_flight;

import de.uol.swp.common.action.simple.MoveAction;
import lombok.NoArgsConstructor;

/**
 * This class represent and realized the shuttle flight action.
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
@NoArgsConstructor
public class ShuttleFlightAction extends MoveAction {
    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public void execute() {

    }
}
