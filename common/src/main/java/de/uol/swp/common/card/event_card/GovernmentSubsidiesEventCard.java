package de.uol.swp.common.card.event_card;

/**
 * Represents a Government Subsidies Event Card in the game.
 * This card allows the player to build a research station in any city without discarding a card.
 */
public class GovernmentSubsidiesEventCard extends EventCard {
    private static final String DESCRIPTION_STRING = "Errichten Sie in einer beliebigen Stadt ein Forschungslabor. Sie müssen dafür keine Karte ablegen.";
    private static final String TITLE_STRING = "Regierungssubventionen";

    /**
     * Constructs a new GovernmentSubsidiesEventCard with predefined description and title.
     */
    public GovernmentSubsidiesEventCard() {
        super(DESCRIPTION_STRING, TITLE_STRING);
    }

    @Override
    public void trigger() {

    }

    @Override
    public String getEffectMessage() {
        return "";
    }

    @Override
    public String getEffectMessage() {
        return "";
    }
}
