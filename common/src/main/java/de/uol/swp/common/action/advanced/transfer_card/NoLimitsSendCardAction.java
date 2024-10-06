package de.uol.swp.common.action.advanced.transfer_card;

import de.uol.swp.common.action.RoleAction;
import de.uol.swp.common.player.Player;

/**
 * The {@code NoLimitsSendCardAction} class allows sending a card without restrictions.
 * It extends {@link SendCardAction} and implements {@link RoleAction}.
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
public class NoLimitsSendCardAction extends SendCardAction implements RoleAction {

    /**
     * <p>
     *     {@link NoLimitsSendCardAction} is available as long as the sender has hand cards.
     * </p>
     *
     * @return {@code true} if sender has hand cards, {@code false} otherwise
     * @see Player#hasHandCards()
     */
    @Override
    public boolean isAvailable() {
        return getSender().hasHandCards();
    }

    @Override
    public boolean isExecutable() {
        return isAvailable() && isApproved();
    }
}

