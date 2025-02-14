package de.uol.swp.server.lobby;

import com.google.inject.Inject;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.lobby.LobbyStatus;
import de.uol.swp.server.lobby.store.LobbyStore;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

/**
 * Manages creation, deletion and storing of lobbies
 *
 * @see de.uol.swp.common.lobby.Lobby
 * @see LobbyDTO
 * @author Marco Grawunder
 * @since 2019-10-08
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class LobbyManagement {

    private final LobbyStore lobbyStore;

    /**
     * Creates a new lobby and adds it to the list
     *
     * @implNote the primary key of the lobbies is the name therefore the name has
     *           to be unique
     * @param lobby the lobby to create
     * @see de.uol.swp.common.user.User
     * @throws IllegalArgumentException name already taken
     * @since 2019-10-08
     */
    public Lobby createLobby(Lobby lobby) {
       return this.lobbyStore.addLobby(lobby);
    }

    /**
     * Deletes lobby with requested name
     *
     * @param lobby lobby to delete
     * @throws IllegalArgumentException there exists no lobby with the  requested
     *                                  name
     * @since 2019-10-08
     */
    public void dropLobby(Lobby lobby) {
        this.lobbyStore.removeLobby(lobby);
    }

    /**
     * Searches for the lobby with the requested name
     *
     * @param lobby the lobby to search for
     * @return either empty Optional or Optional containing the lobby
     * @see Optional
     * @since 2019-10-08
     */
    public Optional<Lobby> getLobby(Lobby lobby) {
        return this.lobbyStore.getLobby(lobby.getId());
    }

    /**
     * Returns a list of all lobbies
     *
     * @return List of all lobbies
     * @since 2024-08-24
     */
    public List<Lobby> getAllLobbies() {
        return this.lobbyStore.getAllLobbies();
    }

    /**
     * Updates a lobby
     *
     * @param lobby The lobby to update
     * @since 2024-09-13
     */
    public void updateLobby(final Lobby lobby) {
        this.lobbyStore.updateLobby(lobby);
    }

    /**
     * Updates the status of a lobby
     *
     * @param lobby The lobby to update
     * @param status The new status of the lobby
     * @since 2024-08-29
     */
    public void updateLobbyStatus(Lobby lobby, LobbyStatus status) {
        this.lobbyStore.getLobby(lobby.getId()).ifPresentOrElse(
                l -> {
                    l.setStatus(status);
                    updateLobby(l);
                },
                () -> {
                    throw new IllegalArgumentException("Lobby " + lobby + " not found!");
                }
        );
    }
}
