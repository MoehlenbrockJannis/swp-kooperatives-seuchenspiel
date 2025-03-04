package de.uol.swp.common.action.advanced.transfer_card;

import de.uol.swp.common.card.CityCard;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.player.Player;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The {@code SendCardAction} class represents an action where a player sends a card to another player.
 * It extends {@link ShareKnowledgeAction} and provides a method to retrieve the corresponding
 * {@link ReceiveCardAction} for the opponent receiving the card.
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
public class SendCardAction extends ShareKnowledgeAction {

    @Override
    public String toString() {
        return super.toString() + " (Karte verschicken)";
    }

    @Override
    public Player getSender() {
        return getExecutingPlayer();
    }

    @Override
    public Player getReceiver() {
        return getTargetPlayer();
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     *     All sendable cards can be sent to any {@link Player} that a transfer is possible with.
     * </p>
     *
     * @see #getSendableCards()
     * @see #getPlayersWithPossibilityOfTransfer()
     */
    @Override
    public Map<Player, List<CityCard>> getTargetPlayersWithAvailableCardsAssociation() {
        final List<CityCard> sendableCards = getSendableCards();
        if (sendableCards.isEmpty()) {
            return Map.of();
        }

        final List<Player> possibleTargets = getPlayersWithPossibilityOfTransfer();
        return possibleTargets.stream()
                .collect(Collectors.toMap(player -> player, player -> sendableCards));
    }

    /**
     * <p>
     *     Returns a {@link List} of cards the {@link #getSender()} can send.
     *     The requirement to determine whether a hand card can be sent is that it has to be a {@link CityCard}
     *     with the same field the {@link #getExecutingPlayer()} is standing on.
     * </p>
     *
     * @return a {@link List} of cards the {@link #getSender()} can send
     * @see Player#getHandCards()
     * @see CityCard
     * @see CityCard#hasField(Field)
     * @see Player#getCurrentField()
     */
    protected List<CityCard> getSendableCards() {
        return getExecutingPlayer().getHandCards().stream()
                .filter(card -> card instanceof CityCard cityCard && cityCard.hasField(getExecutingPlayer().getCurrentField()))
                .map(CityCard.class::cast)
                .toList();
    }
}
