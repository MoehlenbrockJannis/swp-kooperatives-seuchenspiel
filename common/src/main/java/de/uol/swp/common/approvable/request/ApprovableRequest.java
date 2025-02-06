package de.uol.swp.common.approvable.request;

import de.uol.swp.common.approvable.Approvable;
import de.uol.swp.common.approvable.ApprovableMessageStatus;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.message.request.AbstractRequestMessage;
import de.uol.swp.common.player.Player;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Request sent to communicate an {@link Approvable} between client and server.
 * It can be either outbound, approved or rejected (or temporarily rejected).
 * It specifies messages to send by a specified {@link Player} after it is either approved or rejected.
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
public class ApprovableRequest extends AbstractRequestMessage {
    private final ApprovableMessageStatus status;
    private final Approvable approvable;
    private final Message onApproved;
    private final Player onApprovedPlayer;
    private final Message onRejected;
    private final Player onRejectedPlayer;
}
