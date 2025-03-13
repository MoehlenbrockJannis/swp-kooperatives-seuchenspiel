package de.uol.swp.common.lobby.response;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.message.response.AbstractResponseMessage;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Response sent to the client containing all lobbies
 *
 * @see AbstractResponseMessage
 * @see de.uol.swp.common.lobby.Lobby
 */
@EqualsAndHashCode(callSuper = false)
@Getter
@NoArgsConstructor
public class RetrieveAllLobbiesResponse extends AbstractResponseMessage {
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
     */
    public RetrieveAllLobbiesResponse(final Collection<Lobby> lobbies) {
        for (final Lobby lobby : lobbies) {
            final Lobby lobbyDTO = new LobbyDTO(lobby);
            this.lobbies.add(lobbyDTO);
        }
    }
}
