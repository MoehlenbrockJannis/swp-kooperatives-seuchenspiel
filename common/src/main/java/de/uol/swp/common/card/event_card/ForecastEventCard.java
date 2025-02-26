package de.uol.swp.common.card.event_card;

import de.uol.swp.common.card.InfectionCard;
import de.uol.swp.common.card.stack.CardStack;
import lombok.Setter;

import java.util.List;

/**
 * Represents a Forecast Event Card in the game.
 * This card allows the player to look at the top 6 cards of the infection deck
 * and rearrange them in any order before placing them back on top of the deck.
 */
@Setter
public class ForecastEventCard extends EventCard {
    private static final String DESCRIPTION_STRING = "Sehen Sie sich die obersten 6 Karten des Infektions-Zug-Stapels an und bringen Sie sie ind eine Reihenfolge Ihrer Wahl. Legen Sie die Karten danach wieder oben auf den Stapel.";
    private static final String TITLE_STRING = "Prognose";
    private List<InfectionCard> reorderedInfectionCards;

    /**
     * Constructs a new ForecastEventCard with predefined description and title.
     */
    public ForecastEventCard() {
        super(DESCRIPTION_STRING, TITLE_STRING);
    }

    /**
     * Triggers the infection card stack update by reordering the cards as specified in {@code reorderedInfectionCards}.
     *
     * @author Marvin Tischer
     * @since 2025-02-17
     */
    @Override
    public void trigger() {
        CardStack<InfectionCard> infectionCardStack = game.getInfectionDrawStack();
        if (reorderedInfectionCards != null && !reorderedInfectionCards.isEmpty()) {
            int reorderedInfectionCardsSize = reorderedInfectionCards.size();
            for (int removedCards = 0; removedCards < reorderedInfectionCardsSize; removedCards++) {
                infectionCardStack.pop();
            }

            for (int insertIndex = reorderedInfectionCardsSize - 1; insertIndex >= 0; insertIndex--) {
                InfectionCard reorderedInfectionCard = reorderedInfectionCards.get(insertIndex);
                infectionCardStack.push(reorderedInfectionCard);
            }
        }
    }

    @Override
    public String getEffectMessage() {
        int reorderedInfectionCardsSize = reorderedInfectionCards.size();
        return "Die obersten " + reorderedInfectionCardsSize + " Infektionskarten des Infektionszugstapel wurden neu gemischt";
    }

}
