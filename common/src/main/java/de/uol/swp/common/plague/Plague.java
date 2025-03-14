package de.uol.swp.common.plague;

import de.uol.swp.common.util.Color;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a plague in the game.
 * <p>
 * A plague is defined by its name, color, and whether it has been exterminated.
 * Each plague is treated as unique and is compared based on its name.
 * </p>
 */
@Getter
@RequiredArgsConstructor
public class Plague implements Serializable {

    private final String name;
    private final Color color;
    private boolean isExterminated;

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Plague plague) {
            return name.equals(plague.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public void exterminate() {
        isExterminated = true;
    }
}
