package de.uol.swp.common.action.simple.charter_flight;

import de.uol.swp.common.action.DiscardCardsAction;
import de.uol.swp.common.action.simple.MoveAction;
import de.uol.swp.common.card.CityCard;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.player.Player;

import java.util.List;

/**
 * This class represent and realized the charter flight action.
 */
public class CharterFlightAction extends MoveAction implements DiscardCardsAction {

    @Override
    public String toString() {
        return "Charterflug";
    }

    /**
     * <p>
     *     Returns a {@link List} of city cards with one element,
     *     the {@link CityCard} of the current {@link Field}.
     * </p>
     *
     * @return {@link List} with {@link CityCard} of current {@link Field}
     * @throws IllegalStateException if the executing {@link Player} does not have a fitting {@link CityCard} on hand
     */
    @Override
    public List<CityCard> getDiscardedCards() {
        final List<PlayerCard> handCards = getExecutingPlayer().getHandCards();
        for (final PlayerCard playerCard : handCards) {
            if (playerCard instanceof CityCard cityCard && cityCard.hasField(getCurrentField())) {
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
     *     Returns a {@link List} of all fields.
     *     Does not include the {@link Field} the moved {@link Player} is standing on.
     * </p>
     *
     * <p>
     *     If the moved {@link Player} does not have the fitting {@link CityCard} to the {@link Field} he is standing on,
     *     returns an empty {@link List}.
     * </p>
     *
     * {@inheritDoc}
     *
     * @return {@link List} of all fields except the one the moved {@link Player} is standing on
     * @see #getDiscardedCards()
     * @see Game#getFields()
     * @see #getCurrentField()
     */
    @Override
    public List<Field> getAvailableFields() {
        try {
            getDiscardedCards();

            final List<Field> allFields = getGame().getFields();
            return allFields.stream()
                    .filter(field -> !field.equals(getCurrentField()))
                    .toList();
        } catch (final IllegalStateException e) {
            // ignore
        }
        return List.of();
    }
}
