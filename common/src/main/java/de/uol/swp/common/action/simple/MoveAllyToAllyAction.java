package de.uol.swp.common.action.simple;

import de.uol.swp.common.action.GeneralAction;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.player.Player;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveAllyToAllyAction extends GeneralAction implements MoveAllyAction {

    private Player movedAlly;

    private Player targetAlly;

    @Override
    public boolean isAvailable() {
        return false;
    }

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
    public void execute() {

    }
}
