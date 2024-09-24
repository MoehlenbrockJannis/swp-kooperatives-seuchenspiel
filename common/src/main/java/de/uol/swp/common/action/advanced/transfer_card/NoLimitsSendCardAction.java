package de.uol.swp.common.action.advanced.transfer_card;

import de.uol.swp.common.action.RoleAction;
import lombok.NoArgsConstructor;

/**
 * The {@code NoLimitsSendCardAction} class allows sending a card without restrictions.
 * It extends {@link SendCardAction} and implements {@link RoleAction}.
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
@NoArgsConstructor
public class NoLimitsSendCardAction extends SendCardAction implements RoleAction {
}

