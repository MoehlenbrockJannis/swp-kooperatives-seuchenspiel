package de.uol.swp.common.player;

import java.util.Date;
import lombok.*;

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
     * @param lastSick the date when the AI player was last sick
     * @param name the name of the AI player
     */
    public AIPlayer(Date lastSick, String name) {
        super(lastSick);
        this.name = name;
    }
}
