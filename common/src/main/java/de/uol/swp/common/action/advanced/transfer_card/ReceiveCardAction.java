package de.uol.swp.common.action.advanced.transfer_card;

import de.uol.swp.common.card.CityCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.player.Player;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The {@code ReceiveCardAction} class represents an action where a player receives a card from another player.
 * It extends {@link ShareKnowledgeAction} and provides a method to retrieve the corresponding {@link SendCardAction}
 * of the opponent who is sending the card.
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
public class ReceiveCardAction extends ShareKnowledgeAction {

    @Override
    protected Player getSender() {
        return getTargetPlayer();
    }

    @Override
    protected Player getReceiver() {
        return getExecutingPlayer();
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     *     All sendable cards of any {@link Player} that a transfer is possible with can be received.
     * </p>
     * 
     * @see #getPlayersWithPossibilityOfTransfer()
     * @see #getSendableCardsOfPlayer(Player)
     */
    @Override
    public Map<Player, List<CityCard>> getTargetPlayersWithAvailableCardsAssociation() {
        final List<Player> possibleTargets = getPlayersWithPossibilityOfTransfer();
        return possibleTargets.stream()
                .map(player -> new Object() {
                    final Player p = player;
                    final List<CityCard> sendableCards = getSendableCardsOfPlayer(p);
                })
                .filter(o -> !o.sendableCards.isEmpty())
                .collect(Collectors.toMap(o -> o.p, o -> o.sendableCards));
    }

    /**
     * <p>
     *     Returns a {@link List} of hand cards the given {@code player} can send.
     *     The content of the {@link List} is determined by the {@link SendCardAction} of the given {@code player}.
     * </p>
     *
     * @param player the {@link Player} to get sendable cards of
     * @return {@link List} of cards the given {@code player} can send
     * @see #getSendCardActionClass(Player)
     * @see #createShareKnowledgeAction(Class, Player, Game)
     * @see SendCardAction#getSendableCards()
     */
    protected List<CityCard> getSendableCardsOfPlayer(final Player player) {
        final Class<? extends SendCardAction> sendCardActionClass = getSendCardActionClass(player);
        final SendCardAction sendCardAction = createShareKnowledgeAction(sendCardActionClass, player, getGame());
        return sendCardAction.getSendableCards();
    }
}
