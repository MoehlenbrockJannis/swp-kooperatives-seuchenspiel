package de.uol.swp.common.triggerable;

import de.uol.swp.common.answerable.Answerable;
import de.uol.swp.common.player.Player;

public interface ManualTriggerable extends Triggerable, Answerable {
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
}
