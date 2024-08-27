package de.uol.swp.server.chat;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ChatManagement {

    private final List<String> chatMessages = new ArrayList<>();

    public void addChatMessage(String chatMessage) {
        chatMessages.add(chatMessage);
    }

}
