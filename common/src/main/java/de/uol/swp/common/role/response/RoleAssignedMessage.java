package de.uol.swp.common.role.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
public class RoleAssignedMessage extends AbstractResponseMessage {

    private String roleCardName;
    private boolean roleAssigned;
}
