package de.uol.swp.common.map;


import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.util.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


class MapTypeTest {

    private MapType mapType;

    private String name;

    private MapSlot mapSlot1;
    private MapSlot mapSlot2;
    private MapSlot mapSlot3;

    private City city1;
    private City city2;
    private City city3;

    private Plague plague1;
    private Plague plague2;
    private Plague plague3;

    @BeforeEach
    void setUp() {
        name = "name";

        plague1 = new Plague("Plague1", new Color(10, 10, 10));
        plague2 = new Plague("Plague2", new Color(20, 20, 20));
        plague3 = new Plague("Plague3", new Color(30, 30, 30));

        city1 = new City("City1", "City No.1");
        city2 = new City("City2", "City No.2");
        city3 = new City("City3", "City No.3");

        mapSlot1 = new MapSlot(city1, List.of(city2, city3), plague1, 1, 1);
        mapSlot2 = new MapSlot(city2, List.of(city1, city3), plague2, 2, 2);
        mapSlot3 = new MapSlot(city3, List.of(city1, city2), plague3, 3, 3);

        mapType = new MapType(name, List.of(mapSlot1, mapSlot2, mapSlot3), city1);
    }

    @Test
    @DisplayName("Should return true if given object has the same plagues")
    void getUniquePlagues() {
        Set<Plague> plagueSet = Set.of(plague1, plague2, plague3);

        assertThat(mapType.getUniquePlagues())
                .isEqualTo(plagueSet);
    }

    @Test
    @DisplayName("Should return true if given object has same name")
    void testEquals_true() {
        final MapType equal = new MapType(name, List.of(mapSlot1, mapSlot2, mapSlot3), city1);

        assertThat(mapType.equals(equal))
                .isTrue();
    }

    @Test
    @DisplayName("Should return false if given object does not have same name")
    void testEquals_false() {
        final MapType otherMap = new MapType("different", List.of(mapSlot1, mapSlot2, mapSlot3), city1);

        assertThat(mapType.equals(otherMap))
                .isFalse();
    }

    @Test
    @DisplayName("Should return false if given object is of different type")
    void testEquals_falseDifferentObject() {
        final Object notEqual = new Object();

        assertThat(mapType.equals(notEqual))
                .isFalse();
    }

    @Test
    @DisplayName("Should return equal hash code for equal objects")
    void testHashCode() {
        final MapType other = new MapType(name, List.of(mapSlot1, mapSlot2, mapSlot3), city1);

        assertThat(mapType.hashCode())
                .hasSameHashCodeAs(other);
    }
}
