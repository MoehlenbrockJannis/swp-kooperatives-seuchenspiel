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

    private Field assosiatedField;

    public City getCity() {
        return assosiatedField.getCity();
    }

    public Plague getPlague() {
        return assosiatedField.getPlague();
    }

    @Override
    public Color getColor() {
        return assosiatedField.getPlague().getColor();
    }


    @Override
    public String getTitel() {
        return this.getCity().getName();
    }


}
