package de.uol.swp.common.lobby.response;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.response.AbstractResponseMessage;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Response message representing the result of a lobby creation request.
 * <p>
 * This response is sent by the server to notify clients about the successful creation of a {@link Lobby}.
 * </p>
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@NoArgsConstructor
public class CreateLobbyResponse extends AbstractResponseMessage {
    private Lobby lobby;
}
