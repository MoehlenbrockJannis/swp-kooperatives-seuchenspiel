package de.uol.swp.common.plague;

import de.uol.swp.common.util.Color;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Getter
@RequiredArgsConstructor
public class Plague implements Serializable {

    private final String name;
    private final Color color;
    private boolean isExterminated;

    public void exterminate() {
        isExterminated = true;
    }
}
