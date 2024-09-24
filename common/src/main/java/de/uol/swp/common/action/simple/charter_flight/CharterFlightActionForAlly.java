package de.uol.swp.common.action.simple.charter_flight;

import de.uol.swp.common.action.simple.MoveAllyAction;
import de.uol.swp.common.player.Player;

/**
 * This class represent and realized the charter flight action for ally.
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
public class CharterFlightActionForAlly extends CharterFlightAction implements MoveAllyAction {
    @Override
    public Player getApprovingPlayer() {
        return null;
    }

    @Override
    public boolean isApproved() {
        return false;
    }

    @Override
    public void approve() {
    }

    @Override
    public Player getMovedAlly() {
        return null;
    }

    @Override
    public void setMovedAlly(Player player) {

    }
}
