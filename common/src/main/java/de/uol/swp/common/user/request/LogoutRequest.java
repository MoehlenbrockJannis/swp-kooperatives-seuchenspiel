package de.uol.swp.common.user.request;

import de.uol.swp.common.message.request.AbstractRequestMessage;
import de.uol.swp.common.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;

/**
 * A request send from client to server to log out
 */
@AllArgsConstructor
@Getter
public class LogoutRequest extends AbstractRequestMessage {
	@Serial
	private static final long serialVersionUID = -5912075449879112061L;

	private final User user;
}
