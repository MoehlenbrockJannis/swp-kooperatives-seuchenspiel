package de.uol.swp.common.user.request;

import de.uol.swp.common.message.request.AbstractRequestMessage;
import de.uol.swp.common.user.User;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Request to update an user
 *
 * @see de.uol.swp.common.user.User
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
public class UpdateUserRequest extends AbstractRequestMessage {
    private final User user;
}
