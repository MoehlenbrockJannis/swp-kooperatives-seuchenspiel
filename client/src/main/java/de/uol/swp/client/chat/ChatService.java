package de.uol.swp.client.chat;

import com.google.inject.Inject;
import de.uol.swp.common.chat.message.request.RetrieveChatRequest;
import de.uol.swp.common.chat.message.request.ChatRequest;
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
    public void sendChatRequest(String user, String chatMessage, LocalTime timestamp) {
        ChatRequest chatRequest = new ChatRequest(user, chatMessage, timestamp);
        eventBus.post(chatRequest);
    }

    public void retrieveChat() {
        RetrieveChatRequest retrieveChatRequest = new RetrieveChatRequest();
        eventBus.post(retrieveChatRequest);
    }
}
