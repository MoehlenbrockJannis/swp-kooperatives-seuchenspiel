package de.uol.swp.common.plague;

import de.uol.swp.common.util.Color;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

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
