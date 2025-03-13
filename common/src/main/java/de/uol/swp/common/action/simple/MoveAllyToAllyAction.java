package de.uol.swp.common.action.simple;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.player.Player;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Action representing the movement of one ally to another ally in the game.
 * <p>
 * This action primarily deals with manipulating players' positions based on
 * defined rules and conditions provided in the game.
 * </p>
 */
@Getter
@Setter
public class MoveAllyToAllyAction extends MoveAction implements MoveAllyAction {
    private Player movedAlly;
    private Player targetAlly;
    @Setter(AccessLevel.NONE)
    private boolean isApproved;

    @Override
    public String toString() {
        return "Spieler zu Spieler bewegen";
    }

    /**
     * <p>
     *     Sets the given {@link Player} as {@link #targetAlly}.
     *     Also sets the targetField as the given {@link Player}'s current {@link Field}.
     * </p>
     *
     * @param targetAlly The target {@link Player} to set
     */
    public void setTargetAlly(final Player targetAlly) {
        this.targetAlly = targetAlly;
        setTargetField(targetAlly.getCurrentField());
    }

    @Override
    public void initWithGame(final Game game) {
        super.initWithGame(game);
        this.movedAlly = game.findPlayer(this.movedAlly).orElseThrow();
        if (this.targetAlly != null) {
            this.targetAlly = game.findPlayer(this.targetAlly).orElseThrow();
        }
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

    /**
     * <p>
     *     Returns a {@link List} of fields other players are standing on.
     *     Does not include the field the {@link #movedAlly} is standing on.
     * </p>
     *
     * {@inheritDoc}
     *
     * @return {@link List} of current {@link Field} of all other players
     * @see Game#getPlayersInTurnOrder()
     * @see Player#getCurrentField()
     * @see #getCurrentField()
     */
    @Override
    public List<Field> getAvailableFields() {
        return getGame().getPlayersInTurnOrder().stream()
                .map(Player::getCurrentField)
                .filter(field -> !field.equals(getCurrentField()))
                .toList();
    }
}
