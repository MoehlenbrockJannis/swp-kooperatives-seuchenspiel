package de.uol.swp.common.action;

import de.uol.swp.common.card.CityCard;

import java.util.List;

/**
 * The {@code DiscardCardsAction} interface represents an action where a player discards cards.
 * It extends the {@link Action} interface and provides a method to retrieve the cards being discarded.
 */
public interface DiscardCardsAction extends Action {

    /**
     * <p>
     * Returns a {@link List} of {@link CityCard}s that are being discarded.
     * </p>
     *
     * @return {@link List} of discarded {@link CityCard}s
     */
    List<CityCard> getDiscardedCards();
}
