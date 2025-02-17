package de.uol.swp.common.lobby.response;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.response.AbstractResponseMessage;
import de.uol.swp.common.user.User;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@NoArgsConstructor
public class LobbyIsFullResponse extends AbstractResponseMessage {
    private Lobby lobby;
    private User user;
}