package de.uol.swp.server.chat;

import de.uol.swp.common.chat.request.ChatRequest;
import de.uol.swp.common.chat.message.ChatMessage;
import de.uol.swp.common.chat.request.RetrieveChatRequest;
import de.uol.swp.server.AbstractService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Getter;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

@Singleton @Getter
public class ChatService extends AbstractService {

    private final ArrayList<String> chatMessages = new ArrayList<>();

    /**
     * Creates a new ChatService
     *
     * @param eventBus The EventBus to use
     */
    @Inject
    public ChatService(EventBus eventBus) {
        super(eventBus);
    }

    /**
     * Handles ChatRequests found on the EventBus
     *
     * If a SendChatMessage is detected on the EventBus, this method is called.
     * It prints the chat message to the console.
     *
     * @param chatRequest The SendChatMessage found on the EventBus
     */
    @Subscribe
    public void onChatRequest(ChatRequest chatRequest) {
        chatMessages.add(chatRequest.getChatMessage());
        ChatMessage response = new ChatMessage(chatMessages);
        response.initWithMessage(chatRequest);
        post(response);
    }

    /**
     * Handles RetrieveChatRequests found on the EventBus
     *
     * If a RetrieveChatRequest is detected on the EventBus, this method is called.
     * It sends a ChatMessage with all chat messages to the EventBus.
     *
     * @param msg The RetrieveChatRequest found on the EventBus
     */
    @Subscribe
    public void onRetrieveChatRequest(RetrieveChatRequest msg) {
        ChatMessage response = new ChatMessage(chatMessages);
        response.initWithMessage(msg);
        post(response);
    }
}
