package de.uol.swp.common.lobby.response;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.response.AbstractResponseMessage;
import de.uol.swp.common.user.User;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Response message indicating that a lobby is full.
 * <p>
 * This response is sent by the server to notify that a {@link User} attempted to join a {@link Lobby}
 * which has already reached its maximum capacity.
 * </p>
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@NoArgsConstructor
public class LobbyIsFullResponse extends AbstractResponseMessage {
    private Lobby lobby;
    private User user;
}