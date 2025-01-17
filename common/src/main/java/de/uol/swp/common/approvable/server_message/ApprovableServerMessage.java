package de.uol.swp.common.approvable.server_message;

import de.uol.swp.common.approvable.Approvable;
import de.uol.swp.common.approvable.ApprovableMessageStatus;
import de.uol.swp.common.message.server.AbstractServerMessage;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
public class ApprovableServerMessage extends AbstractServerMessage {
    private final ApprovableMessageStatus status;
    private final Approvable approvable;
}
