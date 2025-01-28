package de.uol.swp.client.chat;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.common.chat.server_message.RetrieveAllChatMessagesServerMessage;
import de.uol.swp.common.chat.server_message.RetrieveAllUserLobbyChatMessagesServerMessage;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.response.LoginSuccessfulResponse;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.greenrobot.eventbus.Subscribe;

import java.time.LocalTime;
import java.util.List;

public class ChatPresenter extends AbstractPresenter {
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

    /**
     * <p>
     *     Return {@value #DEFAULT_FXML_FOLDER_PATH}+{@value #COMPONENT_FXML_FOLDER_PATH}
     * </p>
     *
     * {@inheritDoc}
     */
    @Override
    public String getFXMLFolderPath() {
        return DEFAULT_FXML_FOLDER_PATH + COMPONENT_FXML_FOLDER_PATH;
    }

    public void initialize() {
        chatMessageInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onSendChatRequest();
            }
        });
        configureChatViewTextWrapping();
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
            chatService.sendLobbyChatRequest(user, chatMessage, timeStamp, lobby);
        }
        else {
            chatService.sendChatRequest(user, chatMessage, timeStamp);
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
     * <p>
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
     * <p>
     * If a RetrieveAllChatMessagesServerMessage object is posted to the EventBus the chat view is updated
     * according to the chat messages in the message received.
     *
     * @param chatRetrieveAllMessagesMessage the RetrieveAllChatMessagesServerMessage object seen on the EventBus
     * @see RetrieveAllChatMessagesServerMessage
     * @since 2024-08-26
     */
    @Subscribe
    public void onChatMessage(RetrieveAllChatMessagesServerMessage chatRetrieveAllMessagesMessage) {
        if (this.lobby == null){
            updateChat(chatRetrieveAllMessagesMessage.getChatMessages());
        }
    }

    /**
     * Handles lobby chat messages.
     * <p>
     * If a RetrieveAllLobbyChatMessagesServerMessage object is posted to the EventBus, this method updates the chat view
     * according to the chat messages in the received message.
     *
     * @param lobbyChatRetrieveAllMessagesMessage the RetrieveAllLobbyChatMessagesServerMessage object seen on the EventBus
     * @see RetrieveAllUserLobbyChatMessagesServerMessage
     * @since 2024-09-09
     */
    @Subscribe
    public void onLobbyChatMessage(RetrieveAllUserLobbyChatMessagesServerMessage lobbyChatRetrieveAllMessagesMessage) {
        if (this.lobby != null && this.lobby.equals(lobbyChatRetrieveAllMessagesMessage.getLobby())) {
            updateChat(lobbyChatRetrieveAllMessagesMessage.getChatMessage());
        }
    }


    /**
     * Updates the chat view according to the list given
     * <p>
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

    /**
     * Configures the chat view to wrap text
     * <p>
     * Quelle: https://stackoverflow.com/questions/53493111/javafx-wrapping-text-in-listview
     */
    private void configureChatViewTextWrapping(){
        chatView.setCellFactory(param -> new ListCell<>(){
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item==null) {
                    setGraphic(null);
                    setText(null);

                }else{
                    
                    prefWidthProperty().bind(param.widthProperty().subtract(20));
                    setMaxWidth(Double.MAX_VALUE);
                    
                    setWrapText(true);

                    setText(item);


                }
            }
        });
    }
}
