package de.uol.swp.common.answerable;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.player.Player;

import java.io.Serializable;

public interface Answerable extends Serializable {

    /**
     * Returns the {@link Game} this {@link Answerable} is part of
     *
     * @return {@link Game} of this {@link Answerable}
     */
    Game getGame();

    /**
     * Returns the player who returns an answer.
     *
     * @return the answering {@link Player}
     */
    Player getAnsweringPlayer();
}
