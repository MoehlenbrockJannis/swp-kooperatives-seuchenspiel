package de.uol.swp.server.chat;

import de.uol.swp.common.chat.server_message.RetrieveAllChatMessagesServerMessage;
import de.uol.swp.common.chat.server_message.RetrieveAllUserLobbyChatMessagesServerMessage;
import de.uol.swp.common.chat.request.SendChatMessageRequest;
import de.uol.swp.common.chat.request.RetrieveAllChatMessagesRequest;
import de.uol.swp.common.chat.request.SendUserLobbyChatMessageRequest;
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
        chatManagement.addChatMessage(getChatMessage(sendChatMessageRequest.getTimestamp(), sendChatMessageRequest.getUser().getUsername(), sendChatMessageRequest.getChatMessage()));
        RetrieveAllChatMessagesServerMessage response = new RetrieveAllChatMessagesServerMessage(chatManagement.getChatMessages());
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
    public void onLobbyChatRequest(SendUserLobbyChatMessageRequest sendLobbyChatMessageRequest) {
        chatManagement.addLobbyChatMessage(sendLobbyChatMessageRequest.getLobby(), getChatMessage(sendLobbyChatMessageRequest.getTimestamp(), sendLobbyChatMessageRequest.getUser().getUsername(), sendLobbyChatMessageRequest.getChatMessage()));

        RetrieveAllUserLobbyChatMessagesServerMessage response = new RetrieveAllUserLobbyChatMessagesServerMessage(chatManagement.getLobbyChatMessages(sendLobbyChatMessageRequest.getLobby()));
        response.setLobby(sendLobbyChatMessageRequest.getLobby());
        response.initWithMessage(sendLobbyChatMessageRequest);
        post(response);

    }

    @Subscribe
    public  void onLobbyDropServerInternalMessage(LobbyDroppedServerInternalMessage lobbyDroppedServerInternalMessage){
        chatManagement.removeLobbyChatMessages(lobbyDroppedServerInternalMessage.getLobby());
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
     * If a RetrieveAllChatMessagesRequest is detected on the EventBus, this method is called.
     * It sends a RetrieveAllChatMessagesServerMessage or a LobbyChatRetrieveAllMessagesMassage with all chat messages to the EventBus.
     *
     * @param msg The RetrieveAllChatMessagesRequest found on the EventBus
     */
    @Subscribe
    public void onRetrieveChatRequest(RetrieveAllChatMessagesRequest msg) {
        if(msg.getLobby() == null) {
            RetrieveAllChatMessagesServerMessage response = new RetrieveAllChatMessagesServerMessage(chatManagement.getChatMessages());
            response.initWithMessage(msg);
            post(response);
        }
        else {
            RetrieveAllUserLobbyChatMessagesServerMessage response = new RetrieveAllUserLobbyChatMessagesServerMessage(chatManagement.getLobbyChatMessages(msg.getLobby()));
            response.setLobby(msg.getLobby());
            response.initWithMessage(msg);
            post(response);
        }
    }
}
