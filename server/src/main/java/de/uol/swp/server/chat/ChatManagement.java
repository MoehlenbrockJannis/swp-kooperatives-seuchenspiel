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

    public void addLobbyChatMessage(String lobbyName, String chatMessage) {
        if (!lobbyChatMessages.containsKey(lobbyName)) {
            lobbyChatMessages.put(lobbyName, new ArrayList<>());
        }
        lobbyChatMessages.get(lobbyName).add(chatMessage);
    }

    public List<String> getLobbyChatMessages(String lobbyName) {
        return lobbyChatMessages.get(lobbyName);
    }

    public void removeLobbyChatMessages(String lobbyName){
        this.lobbyChatMessages.remove(lobbyName);
    }

}
