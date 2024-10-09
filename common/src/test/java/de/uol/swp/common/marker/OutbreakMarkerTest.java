package de.uol.swp.common.marker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

class OutbreakMarkerTest {

    private OutbreakMarker defaultOutbreakMarker;

    @BeforeEach
    void setUp() {
        List<Integer> outbreakLevels = Arrays.asList(0, 1, 2, 3, 4, 5, 8);
        this.defaultOutbreakMarker = new OutbreakMarker(outbreakLevels);
    }

    @Test
    void increaseLevels() {
        this.defaultOutbreakMarker.increaseLevel();
        assertEquals(1, this.defaultOutbreakMarker.getLevelValue());
    }
    @Test
    void getValue() {
        assertEquals(0,  this.defaultOutbreakMarker.getLevelValue());
    }
}
