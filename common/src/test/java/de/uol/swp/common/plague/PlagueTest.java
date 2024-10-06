package de.uol.swp.common.plague;

import de.uol.swp.common.util.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class PlagueTest {
    private Plague plague;
    private String name;
    private Color color;

    @BeforeEach
    void setUp() {
        name = "pt";
        color = new Color(50, 50, 50);

        plague = new Plague(name, color);
    }

    @Test
    @DisplayName("Should return true if objects have the same name")
    void testEquals_true() {
        final Plague equal = new Plague(name, null);

        assertThat(plague.equals(equal))
                .isTrue();
    }

    @Test
    @DisplayName("Should return false if objects have different names")
    void testEquals_falseDifferent() {
        final Plague notEqual = new Plague("", null);

        assertThat(plague.equals(notEqual))
                .isFalse();
    }

    @Test
    @DisplayName("Should return false if objects are of different types")
    void testEquals_falseOtherObject() {
        final Object notEqual = new Object();

        assertThat(plague.equals(notEqual))
                .isFalse();
    }

    @Test
    @DisplayName("Should return hash of given name")
    void testHashCode() {
        assertThat(plague.hashCode())
                .isEqualTo(Objects.hash(name));
    }

    @Test
    @DisplayName("Should set the extermination status to true")
    void exterminate() {
        assertThat(plague.isExterminated())
                .isFalse();

        plague.exterminate();


        assertThat(plague.isExterminated())
                .isTrue();
    }

    @Test
    @DisplayName("Should return the specified name")
    void getName() {
        assertThat(plague.getName())
                .isEqualTo(name);
    }

    @Test
    @DisplayName("Should return the specified color")
    void getColor() {
        assertThat(plague.getColor())
                .usingRecursiveComparison()
                .isEqualTo(color);
    }

    @Test
    @DisplayName("Should return the extermination status")
    void isExterminated() {
        assertThat(plague.isExterminated())
                .isFalse();
    }
}