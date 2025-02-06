package de.uol.swp.common.triggerable.request;

import de.uol.swp.common.message.Message;
import de.uol.swp.common.message.request.AbstractRequestMessage;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.triggerable.Triggerable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Request sent to communicate a {@link Triggerable} between client and server.
 * It specifies a {@link Message} to send by a specified {@link Player} after.
 */
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
public class TriggerableRequest extends AbstractRequestMessage {
    private final Triggerable triggerable;
    private final Message cause;
    private final Player returningPlayer;
}
