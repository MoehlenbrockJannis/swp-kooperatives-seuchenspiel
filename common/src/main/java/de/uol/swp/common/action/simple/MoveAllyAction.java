package de.uol.swp.common.action.simple;

import de.uol.swp.common.action.Approvable;
import de.uol.swp.common.action.RoleAction;
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
     * Gets the ally that is being moved.
     *
     * @return the {@link Player} representing the moved ally
     */
    Player getMovedAlly();

    /**
     * Sets the ally to be moved.
     *
     * @param player the {@link Player} to be moved as an ally
     */
    void setMovedAlly(Player player);
}
