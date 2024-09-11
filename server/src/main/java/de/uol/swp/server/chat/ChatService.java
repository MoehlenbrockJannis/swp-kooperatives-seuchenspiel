package de.uol.swp.server.chat;

import de.uol.swp.common.chat.message.ChatRetrieveAllMessagesMessage;
import de.uol.swp.common.chat.message.LobbyChatRetrieveAllMessagesMessage;
import de.uol.swp.common.chat.request.SendChatMessageRequest;
import de.uol.swp.common.chat.request.RetrieveChatRequest;
import de.uol.swp.common.chat.request.SendLobbyChatMessageRequest;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.lobby.message.LobbyDroppedServerInternalMessage;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.time.LocalTime;

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
     * Handles LobbyChatRequests found on the EventBus
     *
     * If a SendLobbyChatMessage is detected on the EventBus, this method is called.
     * It prints the chat message to the console.
     *
     * @param sendLobbyChatMessageRequest The SendChatMessage found on the EventBus
     */
    @Subscribe
    public void onLobbyChatRequest(SendLobbyChatMessageRequest sendLobbyChatMessageRequest) {
        chatManagement.addLobbyChatMessage(sendLobbyChatMessageRequest.getLobbyName(), getChatMessage(sendLobbyChatMessageRequest.getTimestamp(), sendLobbyChatMessageRequest.getUser().getUsername(), sendLobbyChatMessageRequest.getChatMessage()));

        LobbyChatRetrieveAllMessagesMessage response = new LobbyChatRetrieveAllMessagesMessage(chatManagement.getLobbyChatMessages(sendLobbyChatMessageRequest.getLobbyName()));
        response.setLobbyName(sendLobbyChatMessageRequest.getLobbyName());
        response.initWithMessage(sendLobbyChatMessageRequest);
        post(response);

    }

    @Subscribe
    public  void onLobbyDropServerInternalMessage(LobbyDroppedServerInternalMessage lobbyDroppedServerInternalMessage){
        chatManagement.removeLobbyChatMessages(lobbyDroppedServerInternalMessage.getLobbyName());
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
     * It sends a ChatRetrieveAllMessagesMessage or a LobbyChatRetrieveAllMessagesMassage with all chat messages to the EventBus.
     *
     * @param msg The RetrieveChatRequest found on the EventBus
     */
    @Subscribe
    public void onRetrieveChatRequest(RetrieveChatRequest msg) {
        if(msg.getLobbyName() == null) {
            ChatRetrieveAllMessagesMessage response = new ChatRetrieveAllMessagesMessage(chatManagement.getChatMessages());
            response.initWithMessage(msg);
            post(response);
        }
        else {
            LobbyChatRetrieveAllMessagesMessage response = new LobbyChatRetrieveAllMessagesMessage(chatManagement.getLobbyChatMessages(msg.getLobbyName()));
            response.setLobbyName(msg.getLobbyName());
            response.initWithMessage(msg);
            post(response);
        }
    }
}
