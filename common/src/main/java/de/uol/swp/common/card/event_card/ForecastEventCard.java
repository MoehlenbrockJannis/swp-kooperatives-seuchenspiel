package de.uol.swp.common.card.event_card;

/**
 * Represents a Forecast Event Card in the game.
 * This card allows the player to look at the top 6 cards of the infection deck
 * and rearrange them in any order before placing them back on top of the deck.
 */
public class ForecastEventCard extends EventCard {
    private static final String DESCRIPTION_STRING = "Sehen Sie sich die obersten 6 Karten des Infektions-Zug-Stapels an und bringen Sie sie ind eine Reihenfolge Ihrer Wahl. Legen Sie die Karten danach wieder oben auf den Stapel.";
    private static final String TITLE_STRING = "Prognose";

    /**
     * Constructs a new ForecastEventCard with predefined description and title.
     */
    public ForecastEventCard() {
        super(DESCRIPTION_STRING, TITLE_STRING);
    }

    @Override
    public void trigger() {

    }

    @Override
    public String getApprovalRequestMessage() {
        return "";
    }

    @Override
    public String getApprovedMessage() {
        return "";
    }

    @Override
    public String getRejectedMessage() {
        return "";
    }
}
