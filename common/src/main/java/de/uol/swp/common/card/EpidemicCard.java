package de.uol.swp.common.card;

import de.uol.swp.common.util.Color;

public class EpidemicCard extends PlayerCard {
    private static final Color DARK_GREEN = new Color(0, 100, 0);

    @Override
    public String getTitle() {
        return "Epidemie";
    }

    @Override
    public Color getColor() {
        return DARK_GREEN;
    }
}