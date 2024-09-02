package de.uol.swp.common.lobby.response;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Response sent to the client containing all lobbies
 *
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @see de.uol.swp.common.lobby.Lobby
 * @author Tom Weelborg
 * @since 2024-08-24
 */
@EqualsAndHashCode(callSuper = false)
@Getter
@NoArgsConstructor
public class LobbyFindLobbiesResponse extends AbstractResponseMessage {
    private final List<Lobby> lobbies = new ArrayList<>();

    /**
     * Constructor
     *
     * <p>
     * This constructor fills {@link #lobbies} with the lobbies in the given collection.
     * The difference between the collection and {@link #lobbies} is that the latter contains users with their passwords hidden.
     * </p>
     *
     * @param lobbies Collection of all existing lobbies
     * @since 2024-08-24
     */
    public LobbyFindLobbiesResponse(final Collection<Lobby> lobbies) {
        for (final Lobby lobby : lobbies) {
            final Lobby lobbyDTO = new LobbyDTO(lobby.getName(), lobby.getOwner().getWithoutPassword());
            for (final User user : lobby.getUsers()) {
                lobbyDTO.joinUser(user.getWithoutPassword());
            }
            this.lobbies.add(lobbyDTO);
        }
    }
}
