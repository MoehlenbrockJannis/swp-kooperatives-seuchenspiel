package de.uol.swp.common.lobby.server_message;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.server_message.AbstractServerMessage;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
public class RetrieveAllLobbiesServerMessage extends AbstractServerMessage {
    private final List<Lobby> lobbies;
}
