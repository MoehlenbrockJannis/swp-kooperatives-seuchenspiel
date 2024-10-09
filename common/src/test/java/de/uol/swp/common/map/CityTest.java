package de.uol.swp.common.map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CityTest {
    private City city;
    private String name;
    private String information;

    @BeforeEach
    void setUp() {
        name = "Teststadt";
        information = "kleines Kaff westlich des Mississippi";

        city = new City(name, information);
    }

    @Test
    @DisplayName("Should return true if objects have same name and information")
    void testEquals_true() {
        final City equal = new City(name, information);

        assertThat(city.equals(equal))
                .isTrue();
    }

    @Test
    @DisplayName("Should return false if objects do not have same name or information")
    void testEquals_false() {
        final City notEqual = new City("oh no", "");

        assertThat(city.equals(notEqual))
                .isFalse();
    }

    @Test
    @DisplayName("Should return false if objects are not of same type")
    void testEquals_falseOtherObject() {
        final Object object = new Object();

        assertThat(city.equals(object))
                .isFalse();
    }

    @Test
    @DisplayName("Should return same hash code for equal objects")
    void testHashCode() {
        final City equal = new City(name, information);

        assertThat(city)
                .hasSameHashCodeAs(equal);
    }

    @Test
    @DisplayName("Should return specified name")
    void getName() {
        assertThat(city.getName())
                .isEqualTo(name);
    }

    @Test
    @DisplayName("Should return specified information")
    void getInformation() {
        assertThat(city.getInformation())
                .isEqualTo(information);
    }
}