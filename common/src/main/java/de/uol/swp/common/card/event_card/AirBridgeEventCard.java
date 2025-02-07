package de.uol.swp.common.card.event_card;


/**
 * Represents an Air Bridge event card in the game.
 * This card allows a player to move any game piece to any city.
 * To move another player's piece, permission from that player is required.
 */
public class AirBridgeEventCard extends EventCard{
    private static final String DESCRIPTION_STRING = "Ziehen Sie eine beliebige Spielfigur in eine beliebige Stadt. Um eine fremde Spielfigur zu bewegen, benötigen Sie die Erlaubnis des Spielers.";
    private static final String TITLE_STRING = "Luftbrücke";

    /**
     * Constructs an AirBridgeEventCard with a predefined description and title.
     */
    public AirBridgeEventCard() {
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
