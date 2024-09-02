package de.uol.swp.common.lobby.message;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;
import lombok.*;

/**
 * Base class of all lobby request messages. Basic handling of lobby data.
 *
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.message.AbstractRequestMessage
 * @author Marco Grawunder
 * @since 2019-10-08
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
@NoArgsConstructor
@Setter
public class AbstractLobbyRequest extends AbstractRequestMessage {
    String lobbyName;
    User user;
}
