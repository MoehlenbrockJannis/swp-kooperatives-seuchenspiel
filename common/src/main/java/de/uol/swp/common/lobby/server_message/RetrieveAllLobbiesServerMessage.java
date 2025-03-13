package de.uol.swp.common.lobby.server_message;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.server_message.AbstractServerMessage;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

/**
 * Server message that contains a list of all existing lobbies.
 * <p>
 * This message is sent by the server to provide the client with a list of all available {@link Lobby} instances.
 * </p>
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
public class RetrieveAllLobbiesServerMessage extends AbstractServerMessage {
    private final List<Lobby> lobbies;
}
