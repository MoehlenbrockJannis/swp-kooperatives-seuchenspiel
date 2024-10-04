package de.uol.swp.common.card;

import de.uol.swp.common.map.City;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.util.Color;
import lombok.AllArgsConstructor;

/**
 * Represents a card associated with a city.
 */
@AllArgsConstructor
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


}
