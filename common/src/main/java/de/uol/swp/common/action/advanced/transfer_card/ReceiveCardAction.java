package de.uol.swp.common.action.advanced.transfer_card;

import de.uol.swp.common.action.Action;
import de.uol.swp.common.card.CityCard;
import de.uol.swp.common.player.Player;

/**
 * The {@code ReceiveCardAction} class represents an action where a player receives a card from another player.
 * It extends {@link ShareKnowledgeAction} and provides a method to retrieve the corresponding {@link SendCardAction}
 * of the opponent who is sending the card.
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
public class ReceiveCardAction extends ShareKnowledgeAction {

    /**
     * <p>
     *     A {@link ReceiveCardAction} is available if
     *      either
     *       the sender has {@link NoLimitsSendCardAction} as an {@link Action} and
     *       he has hand cards
     *      or
     *       the criteria set out by {@link ShareKnowledgeAction#isAvailable()} are met.
     * </p>
     *
     * @return {@code true} if sender has {@link NoLimitsSendCardAction} and hand cards or result of {@link ShareKnowledgeAction#isAvailable()}
     * @see #getSendCardActionOfOpponent()
     * @see Player#hasHandCards()
     * @see ShareKnowledgeAction#isAvailable()
     */
    @Override
    public boolean isAvailable() {
        return getSender() != null && getSendCardActionOfOpponent().equals(NoLimitsSendCardAction.class) ?
                getSender().hasHandCards() :
                super.isAvailable();
    }

    /**
     * <p>
     *     A {@link ReceiveCardAction} is executable if
     *      either
     *       the sender has {@link NoLimitsSendCardAction} as an {@link Action} and
     *       the criteria set out by {@link #isAvailable()} and {@link #isApproved()} are met
     *      or
     *       the criteria set by {@link ShareKnowledgeAction#isExecutable()} are met.
     * </p>
     *
     * @return {@code true} if sender has {@link NoLimitsSendCardAction} and {@link #isAvailable()} and {@link #isApproved()} are met or result of {@link ShareKnowledgeAction#isExecutable()}
     * @see #getSendCardActionOfOpponent()
     * @see #isAvailable()
     * @see #isApproved()
     * @see ShareKnowledgeAction#isExecutable()
     */
    @Override
    public boolean isExecutable() {
        return getSendCardActionOfOpponent().equals(NoLimitsSendCardAction.class) ?
                isAvailable() && isApproved() :
                super.isExecutable();
    }

    @Override
    protected Player getSender() {
        return getTargetPlayer();
    }

    @Override
    protected Player getReceiver() {
        return getExecutingPlayer();
    }

    /**
     * <p>
     *     Returns the {@link Class} object of the class that the sending {@link Player} would use to send a {@link CityCard}.
     * </p>
     *
     * @return the {@link SendCardAction} class for the sending opponent
     */
    @SuppressWarnings("unchecked")
    public Class<? extends SendCardAction> getSendCardActionOfOpponent() {
        return (Class<? extends SendCardAction>) getSender().getRoleSpecificActionClassOrDefault(SendCardAction.class);
    }
}
