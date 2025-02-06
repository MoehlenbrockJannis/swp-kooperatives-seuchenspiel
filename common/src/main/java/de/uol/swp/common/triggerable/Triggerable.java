package de.uol.swp.common.triggerable;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.GameInitializable;
import de.uol.swp.common.util.Command;

public interface Triggerable extends Command, GameInitializable {
    /**
     * Returns the {@link Game} this {@link Triggerable} is part of
     *
     * @return {@link Game} of this {@link Triggerable}
     */
    Game getGame();

    /**
     * Triggers the effect of this {@link Triggerable}
     */
    void trigger();

    @Override
    default void execute() {
        trigger();
    }
}
