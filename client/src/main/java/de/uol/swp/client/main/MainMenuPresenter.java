package de.uol.swp.client.main;

import de.uol.swp.common.chat.message.ChatMessage;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.Subscribe;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.lobby.LobbyService;
import de.uol.swp.client.chat.ChatService;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.message.UserLoggedInMessage;
import de.uol.swp.common.user.message.UserLoggedOutMessage;
import de.uol.swp.common.user.response.AllOnlineUsersResponse;
import de.uol.swp.common.user.response.LoginSuccessfulResponse;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the main menu
 *
 * @author Marco Grawunder
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2019-08-29
 *
 */
public class MainMenuPresenter extends AbstractPresenter {

    public static final String FXML = "/fxml/MainMenuView.fxml";

    private static final Logger LOG = LogManager.getLogger(MainMenuPresenter.class);

    @FXML
    public ListView<String> chatView;

    @FXML
    private TextField chatMessageInput;

    private ObservableList<String> users;

    private User loggedInUser;

    @Inject
    private LobbyService lobbyService;

    @Inject
    private ChatService chatService;

    private ObservableList<String> chat;

    @FXML
    private ListView<String> usersView;

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
        this.loggedInUser = message.getUser();
        userService.retrieveAllUsers();
        chatService.retrieveChat();
    }

    /**
     * Handles new logged in users
     *
     * If a new UserLoggedInMessage object is posted to the EventBus the name of the newly
     * logged in user is appended to the user list in the main menu.
     * Furthermore if the LOG-Level is set to DEBUG the message "New user {@literal
     * <Username>} logged in." is displayed in the log.
     *
     * @param message the UserLoggedInMessage object seen on the EventBus
     * @see de.uol.swp.common.user.message.UserLoggedInMessage
     * @since 2019-08-29
     */
    @Subscribe
    public void onUserLoggedInMessage(UserLoggedInMessage message) {

        LOG.debug("New user {}  logged in,", message.getUsername());
        Platform.runLater(() -> {
            if (users != null && loggedInUser != null && !loggedInUser.getUsername().equals(message.getUsername()))
                users.add(message.getUsername());
        });
    }

    /**
     * Handles new logged out users
     *
     * If a new UserLoggedOutMessage object is posted to the EventBus the name of the newly
     * logged out user is removed from the user list in the main menu.
     * Furthermore if the LOG-Level is set to DEBUG the message "User {@literal
     * <Username>} logged out." is displayed in the log.
     *
     * @param message the UserLoggedOutMessage object seen on the EventBus
     * @see de.uol.swp.common.user.message.UserLoggedOutMessage
     * @since 2019-08-29
     */
    @Subscribe
    public void onUserLoggedOutMessage(UserLoggedOutMessage message) {
        LOG.debug("User {}  logged out.",  message.getUsername() );
        Platform.runLater(() -> users.remove(message.getUsername()));
    }

    /**
     * Handles new list of users
     *
     * If a new AllOnlineUsersResponse object is posted to the EventBus the names
     * of currently logged in users are put onto the user list in the main menu.
     * Furthermore if the LOG-Level is set to DEBUG the message "Update of user
     * list" with the names of all currently logged in users is displayed in the
     * log.
     *
     * @param allUsersResponse the AllOnlineUsersResponse object seen on the EventBus
     * @see de.uol.swp.common.user.response.AllOnlineUsersResponse
     * @since 2019-08-29
     */
    @Subscribe
    public void onAllOnlineUsersResponse(AllOnlineUsersResponse allUsersResponse) {
        LOG.debug("Update of user list {}", allUsersResponse.getUsers());
        updateUsersList(allUsersResponse.getUsers());
    }

    /**
     * Updates the main menus user list according to the list given
     *
     * This method clears the entire user list and then adds the name of each user
     * in the list given to the main menus user list. If there ist no user list
     * this it creates one.
     *
     * @implNote The code inside this Method has to run in the JavaFX-application
     * thread. Therefore it is crucial not to remove the {@code Platform.runLater()}
     * @param userList A list of UserDTO objects including all currently logged in
     *                 users
     * @see de.uol.swp.common.user.UserDTO
     * @since 2019-08-29
     */
    private void updateUsersList(List<UserDTO> userList) {
        // Attention: This must be done on the FX Thread!
        Platform.runLater(() -> {
            if (users == null) {
                users = FXCollections.observableArrayList();
                usersView.setItems(users);
            }
            users.clear();
            userList.forEach(u -> users.add(u.getUsername()));
        });
    }

    /**
     * Method called when the create lobby button is pressed
     *
     * If the create lobby button is pressed, this method requests the lobby service
     * to create a new lobby. Therefore it currently uses the lobby name "test"
     * and an user called "ich"
     *
     * @param event The ActionEvent created by pressing the create lobby button
     * @see de.uol.swp.client.lobby.LobbyService
     * @since 2019-11-20
     */
    @FXML
    void onCreateLobby(ActionEvent event) {
        lobbyService.createNewLobby("test", new UserDTO("ich", "", ""));
    }

    /**
     * Method called when the join lobby button is pressed
     *
     * If the join lobby button is pressed, this method requests the lobby service
     * to join a specified lobby. Therefore it currently uses the lobby name "test"
     * and an user called "ich"
     *
     * @param event The ActionEvent created by pressing the join lobby button
     * @see de.uol.swp.client.lobby.LobbyService
     * @since 2019-11-20
     */
    @FXML
    void onJoinLobby(ActionEvent event) {
        lobbyService.joinLobby("test", new UserDTO("ich", "", ""));
    }

    /**
     * Method called when the send chat message button is pressed
     *
     * If the send chat message button is pressed, this method sends the chat message
     * written in the chat message input field to the chat service.
     *
     * @param event The ActionEvent created by pressing the send chat message button
     * @since 2024-08-25
     */
    @FXML
    void onSendChatRequest(ActionEvent event) {
        String chatMessage = chatMessageInput.getText();

        if (chatMessage == null || chatMessage.isEmpty()) {
            return;
        }

        LocalTime timeStamp = getTimeStamp();
        String userName = loggedInUser.getUsername();

        chatMessageInput.clear();

        chatService.sendChatRequest(userName, chatMessage, timeStamp);
    }

    /**
     * Returns the current time stamp
     *
     * This method returns the current time stamp in the format of HH:mm
     *
     * @return The current time stamp
     * @since 2024-08-26
     */
    private LocalTime getTimeStamp() {
        LocalDateTime now = LocalDateTime.now();
        return now.toLocalTime().withNano(0);
    }

    /**
     * Handles chat responses
     *
     * If a ChatMessage object is posted to the EventBus the chat view is updated
     * according to the chat messages in the message received.
     *
     * @param chatMessage the ChatMessage object seen on the EventBus
     * @see ChatMessage
     * @since 2024-08-26
     */
    @Subscribe
    public void onChatResponse(ChatMessage chatMessage) {
        updateChat(chatMessage.getChatMessages());
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
    private void updateChat(ArrayList<String> chatMessages) {
        // Attention: This must be done on the FX Thread!
        Platform.runLater(() -> {
            if (chat == null) {
                chat = FXCollections.observableArrayList();
                chatView.setItems(chat);
            }
            chat.clear();
            chat.addAll(chatMessages);
            int lastIndex = chat.size() - 1;
            chatView.scrollTo(lastIndex);
        });
    }
}
