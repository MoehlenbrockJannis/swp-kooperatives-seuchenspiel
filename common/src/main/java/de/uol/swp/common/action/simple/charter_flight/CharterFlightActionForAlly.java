package de.uol.swp.common.action.simple.charter_flight;

import de.uol.swp.common.action.simple.MoveAllyAction;
import de.uol.swp.common.player.Player;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represent and realized the charter flight action for ally.
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
@Getter
public class CharterFlightActionForAlly extends CharterFlightAction implements MoveAllyAction {
    @Setter
    private Player movedAlly;
    private boolean isApproved;

    @Override
    public Player getApprovingPlayer() {
        return getMovedPlayer();
    }

    @Override
    public void approve() {
        this.isApproved = true;
    }

    @Override
    public Player getMovedPlayer() {
        return movedAlly;
    }
}
