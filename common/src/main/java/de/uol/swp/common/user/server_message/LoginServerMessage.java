package de.uol.swp.common.user.server_message;

import de.uol.swp.common.message.server_message.AbstractServerMessage;
import de.uol.swp.common.user.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serial;

/**
 * A message to indicate a newly logged in user
 *
 * This message is used to automatically update the user lists of every connected
 * client as soon as a user successfully logs in
 *
 * @author Marco Grawunder
 * @since 2017-03-17
 */
@EqualsAndHashCode(callSuper = false)
@Getter
public class LoginServerMessage extends AbstractServerMessage {
	@Serial
	private static final long serialVersionUID = -2071886836547126480L;

	private final User user;

	/**
	 * Constructor
	 *
	 * @param user the newly logged in user
	 * @since 2017-03-17
	 */
	public LoginServerMessage(final User user){
		this.user = user.getWithoutPassword();
	}
}
