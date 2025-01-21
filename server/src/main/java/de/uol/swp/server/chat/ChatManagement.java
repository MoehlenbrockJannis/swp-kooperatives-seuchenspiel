package de.uol.swp.server.chat;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.server.chat.store.ChatStore;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ChatManagement {

    private final ChatStore chatStore;


    public void addChatMessage(String chatMessage) {
        this.chatStore.addChatMessage(chatMessage);
    }

    public void addLobbyChatMessage(Lobby lobby, String chatMessage) {
        this.chatStore.addLobbyChatMessage(lobby, chatMessage);
    }

    public List<String> getLobbyChatMessages(Lobby lobby) {
        return this.chatStore.getLobbyChatMessages(lobby);
    }

    public void removeLobbyChatMessages(Lobby lobby){
        this.chatStore.removeLobbyChatMessages(lobby);
    }

    public List<String> getChatMessages() {
        return this.chatStore.getChatMessages();
    }
}
