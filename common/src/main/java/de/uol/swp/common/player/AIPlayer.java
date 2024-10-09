package de.uol.swp.common.player;

import de.uol.swp.common.user.User;
import lombok.*;

import java.util.Objects;

/**
 * The AIPlayer class represents an artificial intelligence (AI) controlled player in the game.
 * This class extends the base Player class and includes additional attributes or behaviors
 * specific to AI players, such as their name.
 */
public class AIPlayer extends Player{

    @Getter
    private String name;

    /**
     * Constructs an AIPlayer instance with the specified last sickness date and name.
     *
     * @param name the name of the AI player
     */
    public AIPlayer(String name) {
        this.name = name;
    }

    @Override
    public boolean containsUser(final User user) {
        return false;
    }
}
