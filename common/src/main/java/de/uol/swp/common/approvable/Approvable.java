package de.uol.swp.common.approvable;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.player.Player;

import java.io.Serializable;

/**
 * The {@code Approvable} interface defines an action that can be approved by a player.
 * It provides methods to get the approving player, check approval status, and approve the action.
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
public interface Approvable extends Serializable {

    /**
     * Returns the {@link Game} this {@link Approvable} is part of
     *
     * @return {@link Game} of this {@link Approvable}
     */
    Game getGame();

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

    /**
     * Returns a text description of what this {@link Approvable} would do
     *
     * @return a {@link String} describing the effects of this {@link Approvable}
     */
    String getApprovalRequestMessage();

    /**
     * Returns a text description of what this {@link Approvable} will do
     *
     * @return a {@link String} describing the approval of this {@link Approvable}
     */
    String getApprovedMessage();

    /**
     * Returns a text description of what this {@link Approvable} would have done
     *
     * @return a {@link String} describing the rejection of this {@link Approvable}
     */
    String getRejectedMessage();

    /**
     * Determines whether a response to this {@link Approvable} is required or
     * if the {@link Game} can continue without it.
     *
     * @return {@code true} if {@link Game} needs to be paused to respond to this {@link Approvable}, {@code false} otherwise
     */
    default boolean isResponseRequired() {
        return true;
    }
}

