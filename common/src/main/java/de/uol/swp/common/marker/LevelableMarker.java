package de.uol.swp.common.marker;

import lombok.Getter;

import java.util.List;

/**
 * The LevelableMarker class represents a marker that can progress through multiple levels.
 * This class is serving as a base for markers that track the progression of certain game
 * states, such as infection or outbreak levels. Each level corresponds to a predefined value
 * from the list of levelValues, which can be accessed and manipulated by the marker.
 * <p>
 * This class provides functionality for increasing the level and retrieving the current level value.
 *
 * @see Marker
 */
public abstract class LevelableMarker extends Marker{
    @Getter
    private int level;
    private final List<Integer> levelValues;
    private int previousLevel;

    /**
     * Constructor for the LevelableMarker.
     * <p>
     * Initializes the LevelableMarker with a list of level values, representing the
     * different stages or levels the marker can progress through. These values are
     * used to track changes in the marker's state, allowing it to increase its level
     * and retrieve the current level's corresponding value.
     *
     * @param levelValues A list of integers representing the possible levels.
     */
    protected LevelableMarker(List<Integer> levelValues) {
        this.levelValues = levelValues;
        this.previousLevel = getLevelValue();
    }

    /**
     * Increases the marker's level by one.
     * <p>
     * This method increments the current level of the marker, progressing it to the next
     * stage based on the predefined levelValues. It also updates the previous level value
     * to the current level value before incrementing.
     */
    public void increaseLevel() {
        if (!isAtMaximumLevel()) {
            this.previousLevel = getLevelValue();
            level++;
        }
    }

    /**
     * Retrieves the value associated with the current level.
     * <p>
     * This method returns the value corresponding to the marker's current level
     * from the levelValues list.
     *
     * @return The value of the current level.
     */
    public int getLevelValue() {
        return this.levelValues.get(this.level);
    }

    /**
     * Returns the level value associated with the given index.
     *
     * @return The level value of the given index.
     */
    public int getLevelValue(int index) {
        return this.levelValues.get(index);
    }

    /**
     * Returns the number of levels of the marker.
     *
     * @return number of levels
     */
    public int getNumberOfLevels() {
        return this.levelValues.size();
    }

    /**
     * Checks if the marker has reached its maximum level.
     *
     * @return true if the current level is at the last position in levelValues, false otherwise
     */
    public boolean isAtMaximumLevel() {
        return this.level >= this.levelValues.size() - 1;
    }

    /**
     * Checks if the infection rate has changed.
     *<p>
     * This method compares the previous level value with the current level value
     * to determine if there has been a change in the infection rate.
     *
     * @return true if the infection rate has changed, false otherwise
     */
    public boolean hasInfectionrateChanged() {
       return this.previousLevel != getLevelValue();
    }
}
