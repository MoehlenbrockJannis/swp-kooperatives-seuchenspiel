package de.uol.swp.client.main;

import de.uol.swp.client.lobby.events.ShowLobbyOverviewViewEvent;
import de.uol.swp.client.auth.events.ShowLoginViewEvent;
import de.uol.swp.common.user.message.UsersListMessage;
import javafx.scene.layout.GridPane;
import de.uol.swp.client.lobby.events.ShowLobbyCreateViewEvent;
import org.greenrobot.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.lobby.LobbyService;
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

    private static final ShowLobbyOverviewViewEvent showLobbyOverviewViewEvent = new ShowLobbyOverviewViewEvent();

    private ObservableList<String> users;

    private User loggedInUser;

    @Inject
    private LobbyService lobbyService;

    private final HighlightLoggedInUserCellFactory highlightLoggedInUserCellFactory = new HighlightLoggedInUserCellFactory();

    @FXML
    private ListView<String> usersView;

    @FXML
    private GridPane gameInstructionsGridPane;

    /**
     * Initializes the MainMenuPresenter
     *
     * This method initializes the MainMenuPresenter
     *
     */
    @FXML
    public void initialize() {
        gameInstructionsGridPane.setVisible(false);
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
        this.loggedInUser = message.getUser();
        userService.retrieveAllUsers();
        highlightLoggedInUserCellFactory.setLoggedInUserProvider(loggedInUser);
        usersView.setCellFactory(highlightLoggedInUserCellFactory);
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
    public void onAllOnlineUsersResponse(UsersListMessage allUsersResponse) {
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
    private void updateUsersList(List<String> userList) {
        // Attention: This must be done on the FX Thread!
        Platform.runLater(() -> {
            if (users == null) {
                users = FXCollections.observableArrayList();
                usersView.setItems(users);
            }
            users.clear();
            userList.forEach(u -> users.add(u));
        });
    }

    /**
     * Method called when the create lobby button is pressed
     *
     * <p>
     * If the create lobby button is pressed, this method posts a new {@link ShowLobbyCreateViewEvent} to the eventBus.
     * </p>
     *
     * @param event The ActionEvent created by pressing the create lobby button
     * @see ShowLobbyCreateViewEvent
     * @since 2019-11-20
     */
    @FXML
    void onCreateLobby(final ActionEvent event) {
        final ShowLobbyCreateViewEvent showLobbyCreateViewEvent = new ShowLobbyCreateViewEvent();
        eventBus.post(showLobbyCreateViewEvent);
    }

    /**
     * Method called when the join lobby button is pressed
     *
     * <p>
     * If the join lobby button is pressed, this method opens a window with all active lobbies.
     * </p>
     *
     * @param event The ActionEvent created by pressing the join lobby button
     * @see de.uol.swp.client.lobby.LobbyService
     * @since 2024-08-23
     */
    @FXML
    void onJoinLobby(ActionEvent event) {
        lobbyService.findLobbies();
        eventBus.post(showLobbyOverviewViewEvent);
    }

    /**
     * Method called when the logout button is pressed
     *
     * This Method is called when the logout button is pressed. It calls the logout
     * function from the user service to log out the currently logged in user from the server.
     * After logging out the scence is changed to the login menu.
     *
     * @param event The ActionEvent generated by pressing the logout button
     * @see de.uol.swp.client.user.UserService
     * @since 2024-08-22
     *
     */
    @FXML
    private void onLogoutButtonPressed(ActionEvent event) {
        userService.logout(loggedInUser);
        loggedInUser = null;
        this.eventBus.post(new ShowLoginViewEvent());
    }

    /**
     * Handles the event when the game instructions button is pressed.
     *
     * This method makes the game instructions panel visible in the UI, allowing
     * the user to view the game instructions.
     *
     * @param event The ActionEvent created by pressing the game instructions button
     * @since 2024-08-27
     */
    @FXML
    void onGameInstructionsButtonPressed(ActionEvent event) {
        gameInstructionsGridPane.setVisible(true);
    }
}
