package de.uol.swp.common.action.simple.car;

import de.uol.swp.common.action.simple.MoveAction;

/**
 * This class represent and realized the car action.
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */

public class CarAction extends MoveAction {
    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public void execute() {

    }
}
