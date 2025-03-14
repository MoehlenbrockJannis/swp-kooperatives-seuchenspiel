package de.uol.swp.common.user.server_message;

import de.uol.swp.common.message.response.AbstractResponseMessage;
import de.uol.swp.common.user.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serial;

/**
 * A message to indicate a user is  logged out
 *
 * This message is used to automatically update the user lists of every connected
 * client as soon as a user successfully logs out
 */
@EqualsAndHashCode(callSuper = false)
@Getter
public class LogoutServerMessage extends AbstractResponseMessage {
	@Serial
	private static final long serialVersionUID = -2071886836547126480L;

	private final User user;

	/**
	 * Constructor
	 *
	 * @param user the newly logged out user
	 */
	public LogoutServerMessage(final User user){
		this.user = user.getWithoutPassword();
	}
}
