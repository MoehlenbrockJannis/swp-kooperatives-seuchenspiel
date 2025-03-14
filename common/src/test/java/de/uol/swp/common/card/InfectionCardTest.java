package de.uol.swp.common.card;

import de.uol.swp.common.map.*;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.util.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InfectionCardTest {
    private InfectionCard card;
    private Field field;
    private City city;
    private Plague plague;
    private Set<Plague> plagueSet;
    private Color color;

    @BeforeEach
    void setUp() {
        city = new City("Bielefeld", "Existiert nicht");
        plague = new Plague("Ruhrpott", new Color(1, 2, 3));
        color = plague.getColor();

        plagueSet = new HashSet<>();
        plagueSet.add(plague);

        final MapType mapType = mock(MapType.class);
        when(mapType.getUniquePlagues())
                .thenReturn(plagueSet);

        GameMap map = mock(GameMap.class);
        when(map.getType())
                .thenReturn(mapType);
        MapSlot mapSlot = mock(MapSlot.class);
        when(mapSlot.getCity())
                .thenReturn(city);
        when(mapSlot.getPlague())
                .thenReturn(plague);

        field = new Field(map, mapSlot);

        card = new InfectionCard(color, field);
    }

    @Test
    @DisplayName("Should return associated city of specified field")
    void getCity() {
        assertThat(card.getCity())
                .usingRecursiveComparison()
                .isEqualTo(city);
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
    @DisplayName("Should return true if given object has same associated color and field")
    void testEquals_true() {
        final InfectionCard equal = new InfectionCard(color, field);

        assertThat(card.equals(equal))
                .isTrue();
    }

    @Test
    @DisplayName("Should return false if given object does not have same associated color and field")
    void testEquals_false() {
        final Color otherColor = new Color(4, 5, 6);
        final InfectionCard otherColorCard = new InfectionCard(otherColor, field);

        assertThat(card.equals(otherColorCard))
                .isFalse();

        final City newCity = new City("Hamburg", "");
        final MapType newMapType = mock(MapType.class);
        when(newMapType.getUniquePlagues())
                .thenReturn(plagueSet);
        final GameMap newMap = mock(GameMap.class);
        when(newMap.getType())
                .thenReturn(newMapType);
        final MapSlot newMapSlot = mock(MapSlot.class);
        when(newMapSlot.getCity())
                .thenReturn(newCity);
        final Field newField = new Field(newMap, newMapSlot);
        final InfectionCard otherFieldCard = new InfectionCard(color, newField);

        assertThat(card.equals(otherFieldCard))
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
        final InfectionCard other = new InfectionCard(color, field);

        assertThat(card.hashCode())
                .hasSameHashCodeAs(other);
    }
}