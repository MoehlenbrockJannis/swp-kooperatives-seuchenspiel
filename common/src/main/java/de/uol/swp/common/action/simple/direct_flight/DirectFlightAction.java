package de.uol.swp.common.action.simple.direct_flight;

import de.uol.swp.common.action.DiscardCardsAction;
import de.uol.swp.common.action.simple.MoveAction;
import de.uol.swp.common.card.CityCard;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.player.Player;

import java.util.List;

/**
 * This class represent and realized the direct flight action.
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
public class DirectFlightAction extends MoveAction implements DiscardCardsAction {

    @Override
    public String toString() {
        return "Direktflug";
    }

    /**
     * <p>
     *     Returns a {@link List} of city cards with one element,
     *     the {@link CityCard} of the target {@link Field}.
     * </p>
     *
     * @return {@link List} with {@link CityCard} of target {@link Field}
     * @throws IllegalStateException if the executing {@link Player} does not have a fitting {@link CityCard} on hand
     */
    @Override
    public List<CityCard> getDiscardedCards() {
        final List<PlayerCard> handCards = getExecutingPlayer().getHandCards();
        for (final PlayerCard playerCard : handCards) {
            if (playerCard instanceof CityCard cityCard && cityCard.hasField(getTargetField())) {
                return List.of(cityCard);
            }
        }
        throw new IllegalStateException("Player does not have a CityCard associated with the targetField.");
    }

    @Override
    public Player getMovedPlayer() {
        return getExecutingPlayer();
    }

    /**
     * <p>
     *     Returns a {@link List} of fields the executing {@link Player} has a fitting {@link CityCard} for.
     *     Does not include the field the moved {@link Player} is standing on.
     * </p>
     *
     * {@inheritDoc}
     *
     * @return {@link List} of all fields the executing {@link Player} has a fitting {@link CityCard} for
     * @see Player#getHandCards()
     * @see CityCard#getAssociatedField()
     * @see #getCurrentField()
     */
    @Override
    public List<Field> getAvailableFields() {
        return getExecutingPlayer().getHandCards().stream()
                .filter(CityCard.class::isInstance)
                .map(CityCard.class::cast)
                .map(CityCard::getAssociatedField)
                .filter(field -> !field.equals(getCurrentField()))
                .toList();
    }
}
