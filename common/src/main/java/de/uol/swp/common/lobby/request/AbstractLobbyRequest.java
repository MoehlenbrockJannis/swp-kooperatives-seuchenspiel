package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.request.AbstractRequestMessage;
import lombok.*;

/**
 * Represents a generic request message related to a specific lobby.
 *
 * This abstract class extends {@link AbstractRequestMessage} and contains a protected
 * {@link Lobby} object, representing the lobby involved in the request.
 * It is the base class for various lobby-related request messages, such as requests for
 * joining, leaving, or kicking users from a lobby.
 *
 * The class uses Lombok annotations for boilerplate code reduction
 *
 * @since 2024-10-06
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class AbstractLobbyRequest extends AbstractRequestMessage {
    protected Lobby lobby;
}
