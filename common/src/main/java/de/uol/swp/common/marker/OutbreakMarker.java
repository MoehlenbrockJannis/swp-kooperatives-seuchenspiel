package de.uol.swp.common.marker;

import java.util.List;

/**
 * The OutbreakMarker class represents a marker used to track the outbreak level in the game.
 * It extends the LevelableMarker class, allowing this marker to handle multiple levels of
 * outbreaks as they escalate or subside during gameplay.
 *
 * This class is primarily used to monitor and manage the outbreak status within a game scenario.
 *
 * @see LevelableMarker
 */
public class OutbreakMarker extends LevelableMarker{

    /**
     * Constructor for the OutbreakMarker.
     * <p>
     * Initializes the OutbreakMarker with a list of level values, representing
     * different stages or levels of outbreaks.
     *
     * @param levelValues A list of integers representing the outbreak levels.
     */
    public OutbreakMarker(List<Integer> levelValues) {
        super(levelValues);
    }
}
