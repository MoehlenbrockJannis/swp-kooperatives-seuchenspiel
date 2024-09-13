package de.uol.swp.server.chat;

import de.uol.swp.common.lobby.Lobby;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ChatManagement {

    private final List<String> chatMessages = new ArrayList<>();
    private final Map<Lobby, List<String>> lobbyChatMessages = new HashMap<>();

    public void addChatMessage(String chatMessage) {
        chatMessages.add(chatMessage);
    }

    private void checkLobbyName(final Lobby lobby) {
        lobbyChatMessages.computeIfAbsent(lobby, name -> new ArrayList<>());
    }

    public void addLobbyChatMessage(Lobby lobby, String chatMessage) {
        checkLobbyName(lobby);
        lobbyChatMessages.get(lobby).add(chatMessage);
    }

    public List<String> getLobbyChatMessages(Lobby lobby) {
        checkLobbyName(lobby);
        return lobbyChatMessages.get(lobby);
    }

    public void removeLobbyChatMessages(Lobby lobby){
        checkLobbyName(lobby);
        this.lobbyChatMessages.remove(lobby);
    }

}
