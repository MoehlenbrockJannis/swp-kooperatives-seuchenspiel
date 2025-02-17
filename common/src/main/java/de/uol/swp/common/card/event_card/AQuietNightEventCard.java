package de.uol.swp.common.card.event_card;

import de.uol.swp.common.player.turn.PlayerTurn;

/**
 * Represents the "A quiet night" event card.
 * This card allows the next player to skip the phase where they must play the Transmitter.
 */
public class AQuietNightEventCard extends EventCard {
    private static final String DESCRIPTION_STRING = "Der nächste Spieler, der in seinem Spielzug den Überträger spielen muss, darf diese Phase komplett überspringen.";
    private static final String TITLE_STRING = "Eine ruhige Nacht";

    /**
     * Constructs an AquietNightEventCard with a predefined description and title.
     */
    public AQuietNightEventCard() {
        super(DESCRIPTION_STRING, TITLE_STRING);
    }

    /**
     * Sets the number of infection cards to draw in the current turn of {@link #game} to {@code 0}.
     */
    @Override
    public void trigger() {
        final PlayerTurn currentPlayerTurn = game.getCurrentTurn();
        currentPlayerTurn.setNumberOfInfectionCardsToDraw(0);
    }

    @Override
    public String getEffectMessage() {
        return "Die nächste Infektionskartenzugphase wird übersprungen.";
    }
}
