package de.uol.swp.common.lobby.message;

import de.uol.swp.common.lobby.Lobby;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class UpdatedLobbyListMessage extends AbstractLobbyMessage {
    private final List<Lobby> allLobbies;
}
