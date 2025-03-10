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
 * This service class manages the chat functionality within the client application.
 * <p>
 * It is responsible for sending and retrieving chat messages, both in general and specific to lobbies.
 * The class interacts with the EventBus to dispatch requests and handle responses regarding chat actions.
 */
public class ChatService {

    private final EventBus eventBus;

    /**
     * Constructor
     *
     * @param eventBus The EventBus set in ClientModule
     * @see de.uol.swp.client.di.ClientModule
     */
    @Inject
    public ChatService(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * Sends a general chat message to all users.
     *
     * @param user The user who sends the message.
     * @param chatMessage The message to be sent.
     * @param timestamp The time the message was sent.
     */
    public void sendChatRequest(User user, String chatMessage, LocalTime timestamp) {
        SendChatMessageRequest sendChatMessageRequest = new SendChatMessageRequest(user, chatMessage, timestamp);
        eventBus.post(sendChatMessageRequest);
    }

    /**
     * Sends a chat message to a specific lobby.
     *
     * @param user The user who sends the message.
     * @param chatMessage The message to be sent.
     * @param timestamp The time the message was sent.
     * @param lobby The lobby to which the message is sent.
     */
    public void sendLobbyChatRequest(User user, String chatMessage, LocalTime timestamp, Lobby lobby) {
        SendUserLobbyChatMessageRequest sendLobbyChatMessageRequest = new SendUserLobbyChatMessageRequest(lobby, user, chatMessage, timestamp);
        eventBus.post(sendLobbyChatMessageRequest);
    }

    /**
     * Retrieves all general chat messages from the server.
     */
    public void retrieveChat() {
        RetrieveAllChatMessagesRequest retrieveChatRequest = new RetrieveAllChatMessagesRequest();
        eventBus.post(retrieveChatRequest);
    }

    /**
     * Retrieves all chat messages specific to a given lobby from the server.
     *
     * @param lobby The lobby for which to retrieve chat messages.
     */
    public void retrieveChat(Lobby lobby) {
        RetrieveAllChatMessagesRequest retrieveChatRequest = new RetrieveAllChatMessagesRequest(lobby);
        eventBus.post(retrieveChatRequest);
    }
}
