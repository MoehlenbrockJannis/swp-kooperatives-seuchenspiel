package de.uol.swp.common.util;

import de.uol.swp.common.player.Player;

/**
 * The {@code Approvable} interface defines an action that can be approved by a player.
 * It provides methods to get the approving player, check approval status, and approve the action.
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
public interface Approvable {

    /**
     * Returns the player who approves the action.
     *
     * @return the approving {@link Player}
     */
    Player getApprovingPlayer();

    /**
     * Checks if the action has been approved.
     *
     * @return {@code true} if approved, otherwise {@code false}
     */
    boolean isApproved();

    /**
     * Approves the action.
     */
    void approve();
}

