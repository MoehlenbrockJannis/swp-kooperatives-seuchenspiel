package de.uol.swp.common.lobby.server_message;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.server.AbstractServerMessage;
import de.uol.swp.common.user.User;
import lombok.*;

/**
 * Base class of all lobby messages. Basic handling of lobby data.
 *
 * @see de.uol.swp.common.user.User
 * @see AbstractServerMessage
 * @author Marco Grawunder
 * @since 2019-10-08
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@NoArgsConstructor
@Setter
public class AbstractLobbyServerMessage extends AbstractServerMessage {
    protected Lobby lobby;
    protected User user;
}
