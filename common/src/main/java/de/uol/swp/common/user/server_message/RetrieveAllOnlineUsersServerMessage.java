package de.uol.swp.common.user.server_message;

import de.uol.swp.common.message.server_message.AbstractServerMessage;
import de.uol.swp.common.user.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serial;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A message containing all current logged in usernames
 */
@EqualsAndHashCode(callSuper = false)
@Getter
public class RetrieveAllOnlineUsersServerMessage extends AbstractServerMessage {
	@Serial
	private static final long serialVersionUID = -7968574381977330152L;

	private final List<User> users;

	/**
	 * Constructor
	 *
	 * @param users List containing all users currently logged in
	 */
	public RetrieveAllOnlineUsersServerMessage(final List<User> users){
		this.users = users.stream()
				.map(User::getWithoutPassword)
				.collect(Collectors.toList());
	}
}
