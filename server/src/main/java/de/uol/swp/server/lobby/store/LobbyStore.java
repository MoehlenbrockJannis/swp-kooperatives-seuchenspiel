package de.uol.swp.server.lobby.store;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.server.store.ContentStore;

import java.util.List;
import java.util.Optional;

/**
 * Interface for managing and storing lobby data.
 * <p>
 * The {@code LobbyStore} interface provides methods to add, remove, retrieve, update,
 * and list lobbies, enabling persistent management of lobby-related data.
 * </p>
 */
public interface LobbyStore extends ContentStore {

    /**
     * Adds a lobby to the store.
     *
     * @param lobby the lobby to add
     */
    Lobby addLobby(Lobby lobby);

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
    Optional<Lobby> getLobby(int lobbyID);

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
