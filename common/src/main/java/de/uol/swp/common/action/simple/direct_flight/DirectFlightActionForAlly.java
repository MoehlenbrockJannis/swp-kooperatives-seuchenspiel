package de.uol.swp.common.action.simple.direct_flight;

import de.uol.swp.common.action.simple.MoveAllyAction;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.player.Player;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represent and realized the direct flight action for ally.
 */
@Getter
public class DirectFlightActionForAlly extends DirectFlightAction implements MoveAllyAction {
    @Setter
    private Player movedAlly;
    private boolean isApproved;

    @Override
    public void initWithGame(final Game game) {
        super.initWithGame(game);
        this.movedAlly = game.findPlayer(this.movedAlly).orElseThrow();
    }

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
