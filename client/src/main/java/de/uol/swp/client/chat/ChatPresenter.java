package de.uol.swp.client.chat;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.lobby.events.ShowLobbyViewEvent;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.common.chat.message.ChatRetrieveAllMessagesMessage;
import de.uol.swp.common.chat.message.LobbyChatRetrieveAllMessagesMessage;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.response.LobbyJoinUserResponse;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.response.LoginSuccessfulResponse;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import lombok.Setter;
import org.greenrobot.eventbus.Subscribe;

import java.time.LocalTime;
import java.util.List;

public class ChatPresenter extends AbstractPresenter {

    public static final String FXML = "/fxml/component/ChatComponent.fxml";
    private Lobby lobby;
    @FXML
    private ListView<String> chatView;
    private ObservableList<String> chatMessages;
    @FXML
    private TextField chatMessageInput;
    @Inject
    private ChatService chatService;
    @Inject
    private LoggedInUserProvider loggedInUserProvider;

    public void initialize() {
        chatMessageInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onSendChatRequest();
            }
        });
    }

    private LocalTime getCurrentTimeStamp() {
        return LocalTime.now().withNano(0);
    }

    @FXML
    private void onSendChatRequest() {
        String chatMessage = chatMessageInput.getText();

        if (chatMessage == null || chatMessage.isEmpty()) {
            return;
        }

        LocalTime timeStamp = getCurrentTimeStamp();
        User user = loggedInUserProvider.get();

        chatMessageInput.clear();

        if (this.lobby != null) {
            String lobbyName = lobby.getName();
            chatService.sendLobbyChatRequest(user, chatMessage, timeStamp, lobbyName);
        }
        else {
            chatService.sendChatRequest(user.getUsername(), chatMessage, timeStamp);
        }
    }

    /**
     * Sets the lobby for the chat presenter and retrieves the chat messages for the specified lobby.
     *
     * @param lobby The lobby to set for the chat presenter
     */
    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
        chatService.retrieveChat(this.lobby);
    }


    /**
     * Handles successful login
     *
     * If a LoginSuccessfulResponse is posted to the EventBus the loggedInUser
     * of this client is set to the one in the message received and the full
     * list of users currently logged in is requested.
     *
     * @param message the LoginSuccessfulResponse object seen on the EventBus
     * @see de.uol.swp.common.user.response.LoginSuccessfulResponse
     * @since 2019-09-05
     */
    @Subscribe
    public void onLoginSuccessfulResponse(LoginSuccessfulResponse message) {
        chatService.retrieveChat();
    }


    /**
     * Handles chat responses
     *
     * If a ChatRetrieveAllMessagesMessage object is posted to the EventBus the chat view is updated
     * according to the chat messages in the message received.
     *
     * @param chatRetrieveAllMessagesMessage the ChatRetrieveAllMessagesMessage object seen on the EventBus
     * @see ChatRetrieveAllMessagesMessage
     * @since 2024-08-26
     */
    @Subscribe
    public void onChatMessage(ChatRetrieveAllMessagesMessage chatRetrieveAllMessagesMessage) {
        if (this.lobby == null){
            updateChat(chatRetrieveAllMessagesMessage.getChatMessages());
        }
    }

    /**
     * Handles lobby chat messages.
     *
     * If a LobbyChatRetrieveAllMessagesMessage object is posted to the EventBus, this method updates the chat view
     * according to the chat messages in the received message.
     *
     * @param lobbyChatRetrieveAllMessagesMessage the LobbyChatRetrieveAllMessagesMessage object seen on the EventBus
     * @see LobbyChatRetrieveAllMessagesMessage
     * @since 2024-09-09
     */
    @Subscribe
    public void onLobbyChatMessage(LobbyChatRetrieveAllMessagesMessage lobbyChatRetrieveAllMessagesMessage) {
        if( this.lobby != null && this.lobby.getName().equals(lobbyChatRetrieveAllMessagesMessage.getLobbyName())) {
            updateChat(lobbyChatRetrieveAllMessagesMessage.getChatMessage());
        }

    }


    /**
     * Updates the chat view according to the list given
     *
     * This method clears the entire chat view and then adds the message of each chat
     * message in the list given to the chat view. If there is no chat view
     * this it creates one.
     *
     * @implNote The code inside this Method has to run in the JavaFX-application
     * thread. Therefore it is crucial not to remove the {@code Platform.runLater()}
     * @param chatMessages A list of chat messages
     * @since 2024-08-26
     */
    private void updateChat(List<String> chatMessages) {
        // Attention: This must be done on the FX Thread!
        Platform.runLater(() -> {
            if (this.chatMessages == null) {
                this.chatMessages = FXCollections.observableArrayList();
                chatView.setItems(this.chatMessages);
            }
            this.chatMessages.clear();
            this.chatMessages.addAll(chatMessages);
            int lastIndex = this.chatMessages.size() - 1;
            chatView.scrollTo(lastIndex);
        });
    }
}
