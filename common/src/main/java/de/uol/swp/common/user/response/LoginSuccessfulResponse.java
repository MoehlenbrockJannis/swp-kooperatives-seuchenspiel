package de.uol.swp.common.user.response;

import de.uol.swp.common.message.response.AbstractResponseMessage;
import de.uol.swp.common.user.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A message containing the session (typically for a new logged in user)
 *
 * This response is sent to the Client whose LoginRequest was successful
 *
 * @see de.uol.swp.common.user.request.LoginRequest
 * @see de.uol.swp.common.user.User
 * @author Marco Grawunder
 * @since 2019-08-07
 */
@EqualsAndHashCode(callSuper = false)
@Getter
public class LoginSuccessfulResponse extends AbstractResponseMessage {
    private static final long serialVersionUID = -9107206137706636541L;

    private final User user;

    /**
     * Constructor
     *
     * @param user the user who successfully logged in
     * @since 2019-08-07
     */
    public LoginSuccessfulResponse(final User user) {
        this.user = user.getWithoutPassword();
    }
}
