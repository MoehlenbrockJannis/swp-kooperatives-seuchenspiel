package de.uol.swp.server.chat.store;


import de.uol.swp.common.lobby.Lobby;

import java.util.List;

/**
 * Interface for the chat store.
 */
public interface ChatStore {

    /**
     * Adds a chat message to the store.
     *
     * @param chatMessage the chat message to add
     */
    void addChatMessage(String chatMessage);

    /**
     * Retrieves all global chat messages from the store.
     *
     * @return a list of chat messages
     */
    List<String> getChatMessages();

    /**
     * Adds a chat message to a specific lobby.
     *
     * @param lobby the lobby to which the chat message belongs
     * @param chatMessage the chat message to add
     */
    void addLobbyChatMessage(Lobby lobby, String chatMessage);

    /**
     * Retrieves the chat messages for a specific lobby.
     *
     * @param lobby the lobby for which to retrieve chat messages
     * @return an Optional containing a list of chat messages, or an empty Optional if no messages are found
     */
    List<String> getLobbyChatMessages(Lobby lobby);

    /**
     * Deletes all chat messages for a specific lobby.
     *
     * @param lobby the lobby for which to delete chat messages
     */
    void removeLobbyChatMessages(Lobby lobby);
}
