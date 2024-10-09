package de.uol.swp.common.card;

import de.uol.swp.common.map.City;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.util.Color;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InfectionCard extends Card{

    private final Color color;
    private final Field associatedField;

    @Override
    public String getTitle() {
        return associatedField.getCity().getName();
    }

    @Override
    public Color getColor() {
        return this.color;
    }
}
