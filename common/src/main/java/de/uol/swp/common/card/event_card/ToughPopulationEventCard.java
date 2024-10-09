package de.uol.swp.common.card.event_card;


/**
 * Represents a Tough Population Event Card in the game.
 * This card allows the player to choose any card from the infection discard pile and remove it from the game.
 */
public class ToughPopulationEventCard extends  EventCard{
    private static final String DESCRIPTION_STRING = "Wählen Sie eine beliebige Karte aus dem Infektions-Ablagestapel und entfernen Sie sie aus dem Spiel.";
    private static final String TITLE_STRING = "Zähe Bevölkerung";

    /**
     * Constructs a new ToughPopulationEventCard with predefined description and title.
     */
    public ToughPopulationEventCard() {
        super(DESCRIPTION_STRING, TITLE_STRING);
    }
}
