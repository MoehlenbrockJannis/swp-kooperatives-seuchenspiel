package de.uol.swp.server.chat;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ChatManagement {

    private final List<String> chatMessages = new ArrayList<>();
    private final Map<String, List<String>> lobbyChatMessages = new HashMap<>();

    public void addChatMessage(String chatMessage) {
        chatMessages.add(chatMessage);
    }

    private void checkLobbyName(final String lobbyName) {
        lobbyChatMessages.computeIfAbsent(lobbyName, name -> new ArrayList<>());
    }

    public void addLobbyChatMessage(String lobbyName, String chatMessage) {
        checkLobbyName(lobbyName);
        lobbyChatMessages.get(lobbyName).add(chatMessage);
    }

    public List<String> getLobbyChatMessages(String lobbyName) {
        checkLobbyName(lobbyName);
        return lobbyChatMessages.get(lobbyName);
    }

    public void removeLobbyChatMessages(String lobbyName){
        this.lobbyChatMessages.remove(lobbyName);
    }

}
