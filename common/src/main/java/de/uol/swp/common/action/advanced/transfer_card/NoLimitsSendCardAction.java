package de.uol.swp.common.action.advanced.transfer_card;

import de.uol.swp.common.action.RoleAction;
import de.uol.swp.common.card.CityCard;
import de.uol.swp.common.player.Player;

import java.util.List;

/**
 * The {@code NoLimitsSendCardAction} class allows sending a card without restrictions.
 * It extends {@link SendCardAction} and implements {@link RoleAction}.
 */
public class NoLimitsSendCardAction extends SendCardAction implements RoleAction {

    /**
     * <p>
     *     Returns a {@link List} of hand cards the {@link #getSender()} can send.
     *     The requirement to determine whether a hand card can be sent is that it has to be a {@link CityCard}.
     * </p>
     *
     * @return a {@link List} of cards the {@link #getSender()} can send
     * @see Player#getHandCards()
     * @see CityCard
     */
    @Override
    protected List<CityCard> getSendableCards() {
        return getExecutingPlayer().getHandCards().stream()
                .filter(CityCard.class::isInstance)
                .map(CityCard.class::cast)
                .toList();
    }
}
