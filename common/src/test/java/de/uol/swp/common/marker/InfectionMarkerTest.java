package de.uol.swp.common.marker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

class InfectionMarkerTest {

    private InfectionMarker defaultInfectionMarker;

    @BeforeEach
    void setUp() {
        List<Integer> infectionLevels = Arrays.asList(2, 2, 2, 3, 3, 4, 4);
        this.defaultInfectionMarker = new InfectionMarker(infectionLevels);
    }

    @Test
    void increaseLevels() {
        this.defaultInfectionMarker.increaseLevel();
        this.defaultInfectionMarker.increaseLevel();
        this.defaultInfectionMarker.increaseLevel();
        assertEquals(3, this.defaultInfectionMarker.getLevelValue());
    }
    @Test
    void getValue() {
        assertEquals(2,  this.defaultInfectionMarker.getLevelValue());
    }
}
