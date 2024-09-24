package de.uol.swp.common.action.advanced.transfer_card;

import lombok.NoArgsConstructor;

/**
 * The {@code ReceiveCardAction} class represents an action where a player receives a card from another player.
 * It extends {@link ShareKnowledgeAction} and provides a method to retrieve the corresponding {@link SendCardAction}
 * of the opponent who is sending the card.
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
@NoArgsConstructor
public class ReceiveCardAction extends ShareKnowledgeAction {

    /**
     * Returns the class representing the action where the opponent sends the card.
     *
     * @return the {@link SendCardAction} class for the sending opponent
     */
    public Class<? extends SendCardAction> getSendCardActionOfOpponent() {
        return SendCardAction.class;
    }
}
