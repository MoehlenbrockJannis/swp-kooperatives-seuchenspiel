package de.uol.swp.common.marker;

import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.util.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AntidoteMarkerTest {
    private AntidoteMarker antidoteMarker;
    private Plague plague;

    @BeforeEach
    void setUp() {
        plague = new Plague("dto", new Color(3, 2, 1));

        antidoteMarker = new AntidoteMarker(plague);
    }

    @Test
    @DisplayName("Should return specified plague")
    void getPlague() {
        assertThat(antidoteMarker.getPlague())
                .usingRecursiveComparison()
                .isEqualTo(plague);
    }
}