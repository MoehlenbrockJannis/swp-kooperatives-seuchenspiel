package de.uol.swp.common.user.request;

import de.uol.swp.common.message.request.AbstractRequestMessage;
import de.uol.swp.common.user.User;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Request to register a new user
 *
 * @see de.uol.swp.common.user.User
 * @author Marco Grawunder
 * @since 2019-09-02
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
public class RegisterUserRequest extends AbstractRequestMessage {
    private final User user;

    @Override
    public boolean authorizationNeeded() {
        return false;
    }
}
