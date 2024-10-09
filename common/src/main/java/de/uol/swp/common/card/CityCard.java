package de.uol.swp.common.card;

import de.uol.swp.common.map.City;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.util.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a card associated with a city.
 */
@AllArgsConstructor
@Getter
public class CityCard extends PlayerCard{

    private Field associatedField;

    public City getCity() {
        return associatedField.getCity();
    }

    public Plague getPlague() {
        return associatedField.getPlague();
    }

    @Override
    public Color getColor() {
        return associatedField.getPlague().getColor();
    }

    @Override
    public String getTitle() {
        return this.getCity().getName();
    }

    /**
     * Checks if this {@link CityCard} has the given {@link Field} as {@link #associatedField}.
     *
     * @param targetField {@link Field} to check equality with {@link #associatedField}
     * @return {@code true} if given {@link Field} is equal to {@link #associatedField}, {@code false} otherwise
     */
    public boolean hasField(final Field targetField) {
        return associatedField.equals(targetField);
    }

    /**
     * <p>
     *     Returns {@code true} if given {@link Plague} is associated with {@link #associatedField}, {@code false} otherwise.
     * </p>
     *
     * <p>
     *     Delegates to {@link Field#hasPlague(Plague)}.
     * </p>
     *
     * @param plague {@link Plague} to check association with {@link #associatedField} for
     * @return {@code true} if given {@link Plague} is associated with {@link #associatedField}, {@code false} otherwise
     * @see Field#hasPlague(Plague)
     */
    public boolean hasPlague(final Plague plague) {
        return associatedField.hasPlague(plague);
    }
}
