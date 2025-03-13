package de.uol.swp.server.chat;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.server.chat.store.ChatStore;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Manages chat functionalities, such as adding messages, retrieving messages, and managing lobby-specific chats.
 * <p>
 * The {@code ChatManagement} class provides a high-level interface for handling chat-related operations.
 * It interacts with the {@link ChatStore} to store and retrieve chat messages, including both global and lobby-specific messages.
 * </p>
 */
@Getter
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ChatManagement {

    private final ChatStore chatStore;

    /**
     * Adds a global chat message to the chat store.
     *
     * @param chatMessage The chat message to be added.
     */
    public void addChatMessage(String chatMessage) {
        this.chatStore.addChatMessage(chatMessage);
    }

    /**
     * Adds a chat message to a specific lobby's chat store.
     *
     * @param lobby       The {@link Lobby} where the message should be added.
     * @param chatMessage The chat message to be added.
     */
    public void addLobbyChatMessage(Lobby lobby, String chatMessage) {
        this.chatStore.addLobbyChatMessage(lobby, chatMessage);
    }

    /**
     * Retrieves all chat messages for a specific lobby.
     *
     * @param lobby The {@link Lobby} for which to retrieve chat messages.
     * @return A list of chat messages for the specified lobby.
     */
    public List<String> getLobbyChatMessages(Lobby lobby) {
        return this.chatStore.getLobbyChatMessages(lobby);
    }

    /**
     * Removes all chat messages associated with a specific lobby.
     *
     * @param lobby The {@link Lobby} whose messages should be removed.
     */
    public void removeLobbyChatMessages(Lobby lobby) {
        this.chatStore.removeLobbyChatMessages(lobby);
    }

    /**
     * Retrieves all global chat messages from the chat store.
     *
     * @return A list of all global chat messages.
     */
    public List<String> getChatMessages() {
        return this.chatStore.getChatMessages();
    }
}