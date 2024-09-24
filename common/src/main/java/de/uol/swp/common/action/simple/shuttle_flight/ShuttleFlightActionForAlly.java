package de.uol.swp.common.action.simple.shuttle_flight;

import de.uol.swp.common.action.simple.MoveAllyAction;
import de.uol.swp.common.player.Player;
import lombok.NoArgsConstructor;

/**
 * This class represent and realized the shuttle flight action for ally.
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
@NoArgsConstructor
public class ShuttleFlightActionForAlly extends ShuttleFlightAction implements MoveAllyAction {
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
