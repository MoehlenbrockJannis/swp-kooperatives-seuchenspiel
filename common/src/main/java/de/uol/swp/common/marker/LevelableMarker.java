package de.uol.swp.common.marker;

import java.util.List;

/**
 * The LevelableMarker class represents a marker that can progress through multiple levels.
 * This class is serving as a base for markers that track the progression of certain game
 * states, such as infection or outbreak levels. Each level corresponds to a predefined value
 * from the list of levelValues, which can be accessed and manipulated by the marker.
 *
 * This class provides functionality for increasing the level and retrieving the current level value.
 *
 * @see Marker
 * @since 2024-10-01
 */
public abstract class LevelableMarker extends Marker{
    private int level;
    private List<Integer> levelValues;

    /**
     * Constructor for the LevelableMarker.
     *
     * Initializes the LevelableMarker with a list of level values, representing the
     * different stages or levels the marker can progress through. These values are
     * used to track changes in the marker's state, allowing it to increase its level
     * and retrieve the current level's corresponding value.
     *
     * @param levelValues A list of integers representing the possible levels.
     * @since 2024-10-01
     */
    protected LevelableMarker(List<Integer> levelValues) {
        this.levelValues = levelValues;
    }

    /**
     * Increases the marker's level by one.
     *
     * This method increments the current level of the marker, progressing it to the next
     * stage based on the predefined levelValues.
     *
     * @since 2024-10-01
     */
    public void increaseLevel() {
        level++;
    }

    /**
     * Retrieves the value associated with the current level.
     *
     * This method returns the value corresponding to the marker's current level
     * from the levelValues list.
     *
     * @return The value of the current level.
     * @since 2024-10-01
     */
    public int getLevelValue() {
        return this.levelValues.get(this.level);
    }
}
