package de.uol.swp.common.triggerable;

import de.uol.swp.common.approvable.Approvable;
import de.uol.swp.common.player.Player;

public interface ManualTriggerable extends Triggerable, Approvable {
    /**
     * Returns the {@link Player} that needs to trigger this {@link ManualTriggerable}.
     *
     * @return the {@link Player} that needs to trigger this {@link ManualTriggerable}.
     */
    Player getPlayer();

    /**
     * Sets the {@link Player} that needs to trigger this {@link ManualTriggerable} to given {@link Player}.
     *
     * @param player {@link Player} that needs to trigger this {@link ManualTriggerable}
     */
    void setPlayer(final Player player);

    @Override
    default Player getApprovingPlayer() {
        return getPlayer();
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     *     For any {@link ManualTriggerable}, it is {@code false} by default.
     * </p>
     */
    @Override
    default boolean isResponseRequired() {
        return false;
    }
}
