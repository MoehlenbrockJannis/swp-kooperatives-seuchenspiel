package de.uol.swp.server.lobby.store;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.server.store.ContentStore;

import java.util.List;
import java.util.Optional;

public interface LobbyStore extends ContentStore {

    /**
     * Adds a lobby to the store.
     *
     * @param lobby the lobby to add
     */
    void addLobby(Lobby lobby);

    /**
     * Removes a lobby from the store.
     *
     * @param lobby the lobby to remove
     */
    void removeLobby(Lobby lobby);

    /**
     * Returns a lobby by its ID.
     *
     * @param lobbyID the ID of the lobby
     * @return the lobby
     */
    Optional<Lobby> getLobby(String lobbyID);

    /**
     * Returns all lobbies.
     *
     * @return all lobbies
     */
    List<Lobby> getAllLobbies();

    /**
     * Updates a lobby.
     *
     * @param lobby the lobby to update
     */
    void updateLobby(Lobby lobby);

}
