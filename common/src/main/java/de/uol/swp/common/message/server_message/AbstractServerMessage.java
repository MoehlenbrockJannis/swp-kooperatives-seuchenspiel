package de.uol.swp.common.message.server_message;

import de.uol.swp.common.message.AbstractMessage;
import de.uol.swp.common.user.Session;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class of all server messages. Basic handling of notifications from the server
 * to a group of clients
 *
 * @see de.uol.swp.common.message.AbstractMessage
 * @see ServerMessage
 * @author Marco Grawunder
 * @since 2019-08-07
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class AbstractServerMessage extends AbstractMessage implements ServerMessage {
    private transient List<Session> receiver = new ArrayList<>();
}
