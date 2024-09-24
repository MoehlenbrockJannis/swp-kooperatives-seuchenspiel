package de.uol.swp.common.action.advanced;

import de.uol.swp.common.action.Action;
import de.uol.swp.common.card.CityCard;

/**
 * The {@code DiscardCardAction} interface represents an action where a player discards a card.
 * It extends the {@link Action} interface and provides a method to retrieve the card being discarded.
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
public interface DiscardCardAction extends Action {

    /**
     * Returns the {@link CityCard} that is being discarded.
     *
     * @return the discarded {@link CityCard}
     */
    CityCard getDiscardedCard();
}
