package de.uol.swp.common.card.event_card;


import de.uol.swp.common.card.InfectionCard;
import de.uol.swp.common.card.stack.CardStack;
import lombok.Getter;
import lombok.Setter;
/**
 * Represents a Tough Population Event Card in the game.
 * This card allows the player to choose any card from the infection discard pile and remove it from the game.
 */
@Setter
@Getter
public class ToughPopulationEventCard extends  EventCard{
    private static final String DESCRIPTION_STRING = "Wählen Sie eine beliebige Karte aus dem Infektions-Ablagestapel und entfernen Sie diese aus dem Spiel.";
    private static final String TITLE_STRING = "Zähe Bevölkerung";

    private InfectionCard infectionCard;

    /**
     * Constructs a new ToughPopulationEventCard with predefined description and title.
     */
    public ToughPopulationEventCard() {
        super(DESCRIPTION_STRING, TITLE_STRING);
    }

    @Override
    public void trigger() {
        final CardStack<InfectionCard> infectionCardStack = game.getInfectionDiscardStack();
        infectionCardStack.remove(infectionCard);
    }

    @Override
    public String getEffectMessage() {
        return "Es wurde die Infektionskarte " + infectionCard.getTitle() + " aus den Spiel entfernt.";
    }

}
