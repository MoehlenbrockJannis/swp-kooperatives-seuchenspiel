package de.uol.swp.server.chat;

import de.uol.swp.common.chat.message.ChatRetrieveAllMessagesMessage;
import de.uol.swp.common.chat.request.SendChatMessageRequest;
import de.uol.swp.common.chat.request.RetrieveChatRequest;
import de.uol.swp.server.AbstractService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ChatService extends AbstractService {

    private final ChatManagement chatManagement;

    /**
     * Creates a new ChatService
     *
     * @param eventBus The EventBus to use
     */
    @Inject
    public ChatService(EventBus eventBus, ChatManagement chatManagement) {
        super(eventBus);
        this.chatManagement = chatManagement;
    }

    /**
     * Handles ChatRequests found on the EventBus
     *
     * If a SendChatMessage is detected on the EventBus, this method is called.
     * It prints the chat message to the console.
     *
     * @param sendChatMessageRequest The SendChatMessage found on the EventBus
     */
    @Subscribe
    public void onChatRequest(SendChatMessageRequest sendChatMessageRequest) {
        chatManagement.addChatMessage(getChatMessage(sendChatMessageRequest.getTimestamp(), sendChatMessageRequest.getUserName(), sendChatMessageRequest.getChatMessage()));
        ChatRetrieveAllMessagesMessage response = new ChatRetrieveAllMessagesMessage(chatManagement.getChatMessages());
        response.initWithMessage(sendChatMessageRequest);
        post(response);
    }

    /**
     * Returns the chat message
     *
     * @return the chat message
     */
    public String getChatMessage(LocalTime timestamp, String userName, String chatMessage) {
        return "[" + timestamp + "] " + userName + ": " + chatMessage;
    }

    /**
     * Handles RetrieveChatRequests found on the EventBus
     *
     * If a RetrieveChatRequest is detected on the EventBus, this method is called.
     * It sends a ChatRetrieveAllMessagesMessage with all chat messages to the EventBus.
     *
     * @param msg The RetrieveChatRequest found on the EventBus
     */
    @Subscribe
    public void onRetrieveChatRequest(RetrieveChatRequest msg) {
        ChatRetrieveAllMessagesMessage response = new ChatRetrieveAllMessagesMessage(chatManagement.getChatMessages());
        response.initWithMessage(msg);
        post(response);
    }
}
