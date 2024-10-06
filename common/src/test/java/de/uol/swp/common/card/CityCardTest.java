package de.uol.swp.common.card;

import de.uol.swp.common.map.City;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.map.GameMap;
import de.uol.swp.common.map.MapSlot;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.util.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CityCardTest {
    private CityCard card;
    private Field field;
    private GameMap map;
    private MapSlot mapSlot;
    private City city;
    private Plague plague;

    @BeforeEach
    void setUp() {
        city = new City("Bielefeld", "Existiert nicht");
        plague = new Plague("Ruhrpott", new Color(1, 2, 3));

        map = mock(GameMap.class);
        mapSlot = mock(MapSlot.class);
        when(mapSlot.getCity())
                .thenReturn(city);
        when(mapSlot.getPlague())
                .thenReturn(plague);

        field = new Field(map, mapSlot);

        card = new CityCard(field);
    }

    @Test
    @DisplayName("Should return associated city of specified field")
    void getCity() {
        assertThat(card.getCity())
                .usingRecursiveComparison()
                .isEqualTo(city);
    }

    @Test
    @DisplayName("Should return associated plague of specified field")
    void getPlague() {
        assertThat(card.getPlague())
                .usingRecursiveComparison()
                .isEqualTo(plague);
    }

    @Test
    @DisplayName("Should return associated color of specified field")
    void getColor() {
        assertThat(card.getColor())
                .usingRecursiveComparison()
                .isEqualTo(plague.getColor());
    }

    @Test
    @DisplayName("Should return associated city name of specified field")
    void getTitle() {
        assertThat(card.getTitle())
                .isEqualTo(city.getName());
    }

    @Test
    @DisplayName("Should return specified field")
    void getAssociatedField() {
        assertThat(card.getAssociatedField())
                .usingRecursiveComparison()
                .isEqualTo(field);
    }

    @Test
    @DisplayName("Should return true if given object has same associated field")
    void testEquals_true() {
        final CityCard equal = new CityCard(field);

        assertThat(card.equals(equal))
                .isTrue();
    }

    @Test
    @DisplayName("Should return false if given object does not have same associated field")
    void testEquals_false() {
        final GameMap newMap = mock(GameMap.class);
        final MapSlot newMapSlot = mock(MapSlot.class);
        final Field newField = new Field(newMap, newMapSlot);

        final CityCard notEqual = new CityCard(newField);

        assertThat(card.equals(notEqual))
                .isFalse();
    }

    @Test
    @DisplayName("Should return false if given object is of different type")
    void testEquals_falseDifferentObject() {
        final Object notEqual = new Object();

        assertThat(card.equals(notEqual))
                .isFalse();
    }

    @Test
    @DisplayName("Should return equal hash code for equal objects")
    void testHashCode() {
        final CityCard other = new CityCard(field);

        assertThat(card.hashCode())
                .hasSameHashCodeAs(other);
    }

    @Test
    @DisplayName("Should return true if given field is equal to associated field")
    void hasField_true() {
        final Field otherField = new Field(map, mapSlot);

        assertThat(card.hasField(otherField))
                .isTrue();
    }

    @Test
    @DisplayName("Should return false if given field is not equal to associated field")
    void hasField_false() {
        final GameMap newMap = mock(GameMap.class);
        final MapSlot newMapSlot = mock(MapSlot.class);
        final Field otherField = new Field(newMap, newMapSlot);

        assertThat(card.hasField(otherField))
                .isFalse();
    }

    @Test
    @DisplayName("Should return the same as equivalent method on map slot of associated field")
    void hasPlague() {
        final boolean result = true;

        when(mapSlot.hasPlague(plague))
                .thenReturn(result);

        assertThat(card.hasPlague(plague))
                .isEqualTo(result);
    }
}