package de.uol.swp.client.chat;

import com.google.inject.Inject;
import de.uol.swp.common.chat.request.RetrieveAllChatMessagesRequest;
import de.uol.swp.common.chat.request.SendChatMessageRequest;
import de.uol.swp.common.chat.request.SendUserLobbyChatMessageRequest;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;
import org.greenrobot.eventbus.EventBus;

import java.time.LocalTime;

/**
 * Classes that manages chat
 *
 * @author Silas van Thiel
 * @since 2024-08-25
 *
 */

public class ChatService {

    private final EventBus eventBus;

    /**
     * Constructor
     *
     * @param eventBus The EventBus set in ClientModule
     * @see de.uol.swp.client.di.ClientModule
     * @since 2024-08-25
     */
    @Inject
    public ChatService(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * Sends a chat message
     *
     * @param user The user who sends the message
     * @param chatMessage The message to be sent
     * @param timestamp The time the message was sent
     * @since 2024-08-26
     */
    public void sendChatRequest(User user, String chatMessage, LocalTime timestamp) {
        SendChatMessageRequest sendChatMessageRequest = new SendChatMessageRequest(user, chatMessage, timestamp);
        eventBus.post(sendChatMessageRequest);
    }

    /**
     * Sends a chat message
     *
     * @param chatMessage The message to be sent
     * @param timestamp The time the message was sent
     * @since 2024-08-26
     */
    public void sendLobbyChatRequest(User user, String chatMessage, LocalTime timestamp, Lobby lobby) {
        SendUserLobbyChatMessageRequest sendLobbyChatMessageRequest = new SendUserLobbyChatMessageRequest(lobby, user, chatMessage, timestamp);
        eventBus.post(sendLobbyChatMessageRequest);
    }

    /**
     * Retrieves the chat
     *
     */
    public void retrieveChat() {
        RetrieveAllChatMessagesRequest retrieveChatRequest = new RetrieveAllChatMessagesRequest();
        eventBus.post(retrieveChatRequest);
    }

    /**
     * Retrieves the chat messages for a specific lobby.
     *
     * This method creates a RetrieveAllChatMessagesRequest with the lobby name and posts it to the EventBus.
     *
     * @param lobby The lobby for which to retrieve chat messages
     * @since 2024-09-09
     */
    public void retrieveChat(Lobby lobby) {
        RetrieveAllChatMessagesRequest retrieveChatRequest = new RetrieveAllChatMessagesRequest(lobby);
        eventBus.post(retrieveChatRequest);
    }
}
