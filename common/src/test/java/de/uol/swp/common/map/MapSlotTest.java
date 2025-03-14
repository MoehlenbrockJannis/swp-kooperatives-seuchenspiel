package de.uol.swp.common.map;

import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.util.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MapSlotTest {
    private MapSlot mapSlot;
    private City city;
    private List<City> connectedCities;
    private Plague plague;
    private int xCoordinate;
    private int yCoordinate;

    @BeforeEach
    void setUp() {
        city = new City("ytiC", "");
        connectedCities = List.of(
                new City("c", ""),
                new City("o", ""),
                new City("n", "")
        );
        plague = new Plague("Arbeit", new Color());
        xCoordinate = -1;
        yCoordinate = 1;

        mapSlot = new MapSlot(city, connectedCities, plague, xCoordinate, yCoordinate);
    }

    @Test
    void testToString() {
        assertThat(mapSlot)
                .hasToString(city.getName());
    }

    @Test
    @DisplayName("Should return true if given plague is equal to specified plague")
    void hasPlague_true() {
        assertThat(mapSlot.hasPlague(plague))
                .isTrue();
    }

    @Test
    @DisplayName("Should return false if given plague is not equal to specified plague")
    void hasPlague_false() {
        final Plague newPlague = new Plague("modernity", new Color());

        assertThat(mapSlot.hasPlague(newPlague))
                .isFalse();
    }

    @Test
    @DisplayName("Should return specified city")
    void getCity() {
        assertThat(mapSlot.getCity())
                .usingRecursiveComparison()
                .isEqualTo(city);
    }

    @Test
    @DisplayName("Should return specified connected cities")
    void getConnectedCities() {
        assertThat(mapSlot.getConnectedCities())
                .usingRecursiveComparison()
                .isEqualTo(connectedCities);
    }

    @Test
    @DisplayName("Should return specified plague")
    void getPlague() {
        assertThat(mapSlot.getPlague())
                .usingRecursiveComparison()
                .isEqualTo(plague);
    }

    @Test
    @DisplayName("Should return specified x coordinate")
    void getXCoordinate() {
        assertThat(mapSlot.getXCoordinate())
                .isEqualTo(xCoordinate);
    }

    @Test
    @DisplayName("Should return specified y coordinate")
    void getYCoordinate() {
        assertThat(mapSlot.getYCoordinate())
                .isEqualTo(yCoordinate);
    }
}