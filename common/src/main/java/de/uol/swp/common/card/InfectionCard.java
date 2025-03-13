package de.uol.swp.common.card;

import de.uol.swp.common.map.City;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.util.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * Represents an Infection card in the game.
 * <p>
 * Infection cards are tied to specific cities and fields on the map. Each Infection card has a color and is associated
 * with a field, which in turn points to a city. The card's title is the name of its associated city.
 * </p>
 */
@AllArgsConstructor
public class InfectionCard extends Card{

    private final Color color;

    @Getter
    private final Field associatedField;

    @Override
    public String getTitle() {
        return associatedField.getCity().getName();
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    public City getCity() {
        return associatedField.getCity();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InfectionCard that = (InfectionCard) o;
        return Objects.equals(color, that.color) &&
                Objects.equals(associatedField, that.associatedField);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, associatedField);
    }
}
