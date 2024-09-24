package de.uol.swp.common.action.advanced.discover_antidote;

import de.uol.swp.common.action.advanced.AdvancedAction;
import de.uol.swp.common.card.CityCard;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/** The {@code DiscoverAntidoteAction} class represents an advanced action where a player attempts to discover an antidote
 * by discarding a specified number of {@link CityCard}s. It extends {@link AdvancedAction}.
 * <p>
 * The action requires a certain number of cards to be discarded, which is tracked and managed through the methods in this class.
 * </p>
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DiscoverAntidoteAction extends AdvancedAction {

    /**
     * The number of cards that must be discarded to complete the action.
     */
    private int requiredAmountOfDiscardedCards;

    /**
     * The list of {@link CityCard}s that have been discarded so far.
     */
    private List<CityCard> discardedCards;

    /**
     * Constructs a new {@code DiscoverAntidoteAction} with the required number of discarded cards.
     *
     * @param requiredAmountOfDiscardedCards the number of cards that must be discarded to discover the antidote
     */
    protected DiscoverAntidoteAction(int requiredAmountOfDiscardedCards) {
        this.requiredAmountOfDiscardedCards = requiredAmountOfDiscardedCards;
        this.discardedCards = new ArrayList<>();
    }

    /**
     * Adds a {@link CityCard} to the list of discarded cards if the required amount has not been reached.
     *
     * @param card the {@link CityCard} to be discarded
     * @throws IllegalStateException if the required amount of discarded cards is already reached
     */
    public void addDiscardedCard(CityCard card) {
        if (discardedCards.size() < requiredAmountOfDiscardedCards) {
            discardedCards.add(card);
        } else {
            throw new IllegalStateException("Maximum number of discarded cards reached.");
        }
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public void execute() {

    }
}
