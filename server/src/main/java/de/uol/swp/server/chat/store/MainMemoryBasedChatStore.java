package de.uol.swp.server.chat.store;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.server.store.AbstractStore;
import de.uol.swp.server.store.MainMemoryBasedStore;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A chat store that stores chat messages in the main memory.
 */
public class MainMemoryBasedChatStore extends AbstractStore implements ChatStore, MainMemoryBasedStore {
    @Getter
    private final List<String> chatMessages = new ArrayList<>();
    private final Map<Lobby, List<String>> lobbyChatMessages = new HashMap<>();

    @Override
    public void addChatMessage(String chatMessage) {
        chatMessages.add(chatMessage);
    }

    @Override
    public void addLobbyChatMessage(Lobby lobby, String chatMessage) {
        checkLobbyName(lobby);
        lobbyChatMessages.get(lobby).add(chatMessage);
    }

    @Override
    public List<String> getLobbyChatMessages(Lobby lobby) {
        checkLobbyName(lobby);
        return lobbyChatMessages.get(lobby);
    }

    @Override
    public void removeLobbyChatMessages(Lobby lobby) {
        checkLobbyName(lobby);
        this.lobbyChatMessages.remove(lobby);
    }

    /**
     * Checks if the lobby name is already in the map and adds it if it is not.
     *
     * @param lobby the lobby to check
     */
    private void checkLobbyName(Lobby lobby) {
        lobbyChatMessages.computeIfAbsent(lobby, name -> new ArrayList<>());
    }
}
