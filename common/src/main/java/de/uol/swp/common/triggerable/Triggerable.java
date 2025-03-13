package de.uol.swp.common.triggerable;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.GameInitializable;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.util.Command;


/**
 * Represents a triggerable effect or event in the game.
 * <p>
 * A {@code Triggerable} defines an effect or event that can be activated during gameplay.
 * These could be automatic or manual and are associated with a specific {@link Game} and possibly a {@link Player}.
 * </p>
 */
public interface Triggerable extends Command, GameInitializable {
    /**
     * Returns the {@link Game} this {@link Triggerable} is part of
     *
     * @return {@link Game} of this {@link Triggerable}
     */
    Game getGame();

    void setGame(Game game);

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
    void setPlayer(Player player);

    /**
     * Triggers the effect of this {@link Triggerable}
     */
    void trigger();

    @Override
    default void execute() {
        trigger();
    }
}
