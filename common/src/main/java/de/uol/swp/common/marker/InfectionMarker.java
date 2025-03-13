package de.uol.swp.common.marker;

import java.util.List;

/**
 * The InfectionMarker class represents a marker used to track infection levels in the game.
 * It extends the LevelableMarker class, which allows this marker to have multiple levels
 * that can be adjusted as the infection progresses or recedes.
 *
 * This class is useful for monitoring and managing the infection status in a game scenario.
 *
 * @see LevelableMarker
 * @since 2024-10-01
 */
public class InfectionMarker extends LevelableMarker{

    /**
     * Constructor for the InfectionMarker.
     * <p>
     * Initializes the InfectionMarker with a list of level values, representing
     * different stages or levels of infection.
     *
     * @param levelValues A list of integers representing the infection levels.
     * @since 2024-10-01
     */
    public InfectionMarker(List<Integer> levelValues) {
        super(levelValues);
    }

}
