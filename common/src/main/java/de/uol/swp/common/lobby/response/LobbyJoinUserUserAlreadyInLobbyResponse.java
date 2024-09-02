package de.uol.swp.common.lobby.response;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Response sent to the client containing the joined lobby and the joining user if the user is in the lobby already
 *
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @see de.uol.swp.common.lobby.Lobby
 * @see de.uol.swp.common.user.User
 * @author Tom Weelborg
 * @since 2024-08-29
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@NoArgsConstructor
public class LobbyJoinUserUserAlreadyInLobbyResponse extends AbstractResponseMessage {
    private Lobby lobby;
    private User user;
}
