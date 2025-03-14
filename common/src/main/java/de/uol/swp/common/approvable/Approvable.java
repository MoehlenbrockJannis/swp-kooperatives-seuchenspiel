package de.uol.swp.common.approvable;

import de.uol.swp.common.answerable.Answerable;
import de.uol.swp.common.player.Player;

/**
 * The {@code Approvable} interface defines an action that can be approved by a player.
 * It provides methods to get the approving player, check approval status, and approve the action.
 */
public interface Approvable extends Answerable {

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

    @Override
    default Player getAnsweringPlayer() {
        return getApprovingPlayer();
    }
}

