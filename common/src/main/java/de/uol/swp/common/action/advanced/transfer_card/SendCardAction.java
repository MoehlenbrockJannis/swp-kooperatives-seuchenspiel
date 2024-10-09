package de.uol.swp.common.action.advanced.transfer_card;

import de.uol.swp.common.card.CityCard;
import de.uol.swp.common.player.Player;

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
    protected Player getSender() {
        return getExecutingPlayer();
    }

    @Override
    protected Player getReceiver() {
        return getTargetPlayer();
    }

    /**
     * <p>
     *     Returns the {@link Class} object of the class that the receiving {@link Player} would use to receive a {@link CityCard}.
     * </p>
     *
     * @return the {@link ReceiveCardAction} class for the receiving opponent
     */
    @SuppressWarnings("unchecked")
    public Class<? extends ReceiveCardAction> getReceiveCardActionOfOpponent() {
        return (Class<? extends ReceiveCardAction>) getReceiver().getRoleSpecificActionClassOrDefault(ReceiveCardAction.class);
    }
}
