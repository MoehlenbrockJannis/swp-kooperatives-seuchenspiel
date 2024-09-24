package de.uol.swp.common.action.advanced.transfer_card;

import lombok.NoArgsConstructor;

/**
 * The {@code SendCardAction} class represents an action where a player sends a card to another player.
 * It extends {@link ShareKnowledgeAction} and provides a method to retrieve the corresponding
 * {@link ReceiveCardAction} for the opponent receiving the card.
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
@NoArgsConstructor
public class SendCardAction extends ShareKnowledgeAction {

    /**
     * Returns the class representing the action where the opponent receives the card.
     *
     * @return the {@link ReceiveCardAction} class for the receiving opponent
     */
    public Class<? extends ReceiveCardAction> getReceiveCardActionOfOpponent() {
        return ReceiveCardAction.class;
    }
}
