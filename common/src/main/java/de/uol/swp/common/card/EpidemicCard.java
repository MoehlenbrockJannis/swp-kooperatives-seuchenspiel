package de.uol.swp.common.card;

import de.uol.swp.common.util.Color;

/**
 * Represents an Epidemic card in the game.
 * <p>
 * Epidemic cards introduce additional challenges to the game, such as increasing infection rates or triggering outbreaks.
 * This card type has a dark green color and the title "Epidemie".
 * </p>
 */
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