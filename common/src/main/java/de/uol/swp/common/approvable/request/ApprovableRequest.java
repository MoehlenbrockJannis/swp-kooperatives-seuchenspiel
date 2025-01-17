package de.uol.swp.common.approvable.request;

import de.uol.swp.common.approvable.Approvable;
import de.uol.swp.common.approvable.ApprovableMessageStatus;
import de.uol.swp.common.message.request.AbstractRequestMessage;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
public class ApprovableRequest extends AbstractRequestMessage {
    private final ApprovableMessageStatus status;
    private final Approvable approvable;
}
