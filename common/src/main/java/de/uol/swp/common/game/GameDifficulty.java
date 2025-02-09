package de.uol.swp.common.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the difficulty levels available in the game.
 * Each difficulty level is associated with a display name and
 * a specific number of epidemic cards used in the game setup.
 */
@RequiredArgsConstructor
@Getter
public enum GameDifficulty {
    EASY("Leicht", 4),
    MEDIUM("Mittel", 5),
    HARD("Schwer", 6);

    private final String displayName;
    private final int numberOfEpidemicCards;

    public static GameDifficulty getDefault() {
        return EASY;
    }

    /**
     * Returns the display name of the difficulty level.
     *
     * @return The display name as a String
     */
    @Override
    public String toString() {
        return displayName;
    }
}
