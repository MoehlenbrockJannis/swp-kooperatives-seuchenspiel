package de.uol.swp.server.lobby.store;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.server.store.AbstractStore;
import de.uol.swp.server.store.MainMemoryBasedStore;

import java.util.*;

/**
 * A lobby store that stores lobbies in the main memory.
 */
public class MainMemoryBasedLobbyStore extends AbstractStore implements LobbyStore, MainMemoryBasedStore {

    private final Map<String, Lobby> lobbies = new HashMap<>();


    @Override
    public void addLobby(Lobby lobby) {
        if (doesLobbyExist(lobby)) {
            throw new IllegalArgumentException("Lobby " + lobby + " already exists!");
        }
        lobbies.put(lobby.getName(), lobby);
    }

    @Override
    public void removeLobby(Lobby lobby) {
        throwLobbyNotFoundException(lobby);
        lobbies.remove(lobby.getName());
    }

    @Override
    public Optional<Lobby> getLobby(String lobbyID) {
        return Optional.ofNullable(lobbies.get(lobbyID));
    }

    @Override
    public List<Lobby> getAllLobbies() {
        return new ArrayList<>(lobbies.values());
    }

    @Override
    public void updateLobby(Lobby lobby) {
        throwLobbyNotFoundException(lobby);
        lobbies.put(lobby.getName(), lobby);
    }

    /**
     * Throws an exception if the lobby does not exist.
     *
     * @param lobby the lobby to check
     */
    private boolean doesLobbyExist(Lobby lobby) {
        return lobbies.containsKey(lobby.getName());
    }

    /**
     * Throws an exception if the lobby does not exist.
     *
     * @param lobby the lobby to check
     */
    private void throwLobbyNotFoundException(Lobby lobby) {
        if (!doesLobbyExist(lobby)) {
            throw new IllegalArgumentException("Lobby " + lobby + " not found!");
        }
    }
}
