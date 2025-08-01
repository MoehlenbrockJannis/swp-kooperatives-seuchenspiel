package de.uol.swp.common.lobby.response;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.response.AbstractResponseMessage;
import de.uol.swp.common.user.User;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Response sent to the client containing the joined lobby and the joining user if the user is in the lobby already
 *
 * @see AbstractResponseMessage
 * @see de.uol.swp.common.lobby.Lobby
 * @see de.uol.swp.common.user.User
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@NoArgsConstructor
public class JoinUserUserAlreadyInLobbyLobbyResponse extends AbstractResponseMessage {
    private Lobby lobby;
    private User user;
}
