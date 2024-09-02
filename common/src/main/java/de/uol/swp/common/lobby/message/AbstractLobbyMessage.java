package de.uol.swp.common.lobby.message;

import de.uol.swp.common.message.AbstractServerMessage;
import de.uol.swp.common.user.User;
import lombok.*;

/**
 * Base class of all lobby messages. Basic handling of lobby data.
 *
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.message.AbstractServerMessage
 * @author Marco Grawunder
 * @since 2019-10-08
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@NoArgsConstructor
@Setter
public class AbstractLobbyMessage extends AbstractServerMessage {
    String lobbyName;
    User user;
}
