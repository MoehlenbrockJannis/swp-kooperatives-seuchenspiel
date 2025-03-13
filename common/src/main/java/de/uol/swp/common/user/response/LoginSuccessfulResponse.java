package de.uol.swp.common.user.response;

import de.uol.swp.common.message.response.AbstractResponseMessage;
import de.uol.swp.common.user.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serial;

/**
 * A message containing the session (typically for a new logged in user)
 *
 * This response is sent to the Client whose LoginRequest was successful
 *
 * @see de.uol.swp.common.user.request.LoginRequest
 * @see de.uol.swp.common.user.User
 */
@EqualsAndHashCode(callSuper = false)
@Getter
public class LoginSuccessfulResponse extends AbstractResponseMessage {
    @Serial
    private static final long serialVersionUID = -9107206137706636541L;

    private final User user;

    /**
     * Constructor
     *
     * @param user the user who successfully logged in
     */
    public LoginSuccessfulResponse(final User user) {
        this.user = user.getWithoutPassword();
    }
}
