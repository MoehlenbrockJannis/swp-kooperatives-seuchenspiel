package de.uol.swp.common.action.simple;

import de.uol.swp.common.action.RoleAction;
import de.uol.swp.common.approvable.Approvable;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.player.Player;

/**
 * The {@code MoveAllyAction} interface represents an action where a player moves an ally.
 * It extends both {@link RoleAction} and {@link Approvable}, meaning it is a role-specific
 * action that requires approval.
 * <p>
 * It provides methods to get and set the ally being moved.
 * </p>
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
public interface MoveAllyAction extends RoleAction, Approvable {

    /**
     * <p>
     * Gets the ally that is being moved.
     * </p>
     *
     * @return the {@link Player} representing the moved ally
     */
    Player getMovedAlly();

    /**
     * <p>
     * Sets the ally to be moved.
     * </p>
     *
     * @param player the {@link Player} to be moved as an ally
     */
    void setMovedAlly(Player player);

    /**
     * <p>
     * Gets the {@link Field} the ally will be moved to.
     * </p>
     *
     * @return {@link Field} the ally will be moved to
     */
    Field getTargetField();

    /**
     * <p>
     * Sets the {@link Field} the ally will be moved to.
     * </p>
     *
     * @param field {@link Field} to move the ally to
     */
    void setTargetField(Field field);

    @Override
    default String getApprovalRequestMessage() {
        return getExecutingPlayer() + " möchte " + getMovedAlly() + " auf das Feld " + getTargetField() + " versetzen.";
    }

    @Override
    default String getApprovedMessage() {
        return getMovedAlly() + " hat angenommen. " + getExecutingPlayer() + " hat " + getMovedAlly() + " auf das Feld " + getTargetField() + " versetzt.";
    }

    @Override
    default String getRejectedMessage() {
        return getMovedAlly() + " hat abgelehnt. " + getExecutingPlayer() + " hat " + getMovedAlly() + " nicht auf das Feld " + getTargetField() + " versetzt.";
    }
}
