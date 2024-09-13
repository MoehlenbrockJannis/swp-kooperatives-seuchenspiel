package de.uol.swp.common.user.request;

import de.uol.swp.common.message.request.AbstractRequestMessage;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * A request send from client to server, trying to log in with
 * username and password
 * 
 * @author Marco Grawunder
 * @since  2017-03-17
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
public class LoginRequest extends AbstractRequestMessage {
	private static final long serialVersionUID = 7793454958390539421L;

	private String username;
	private String password;

	@Override
	public boolean authorizationNeeded() {
		return false;
	}
}
