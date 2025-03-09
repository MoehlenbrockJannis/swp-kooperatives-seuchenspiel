package de.uol.swp.client.main_menu;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.SceneManager;
import de.uol.swp.client.user.event.ShowLoginViewEvent;
import de.uol.swp.client.lobby.LobbyService;
import de.uol.swp.client.lobby.events.ShowLobbyCreateViewEvent;
import de.uol.swp.client.lobby.events.ShowLobbyOverviewViewEvent;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.client.user.UserContainerEntityListPresenter;
import de.uol.swp.common.user.UserContainerEntity;
import de.uol.swp.common.user.response.RetrieveAllOnlineUsersResponse;
import de.uol.swp.common.user.server_message.LoginServerMessage;
import de.uol.swp.common.user.server_message.RetrieveAllOnlineUsersServerMessage;
import de.uol.swp.common.user.response.LoginSuccessfulResponse;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collection;
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
    private static final Logger LOG = LogManager.getLogger(MainMenuPresenter.class);

    private static final ShowLobbyOverviewViewEvent showLobbyOverviewViewEvent = new ShowLobbyOverviewViewEvent();

    @Inject
    private LoggedInUserProvider loggedInUserProvider;

    @Inject
    private LobbyService lobbyService;

    @FXML
    private GridPane gameInstructionsGridPane;

    @FXML
    private UserContainerEntityListPresenter userContainerEntityListController;

    /**
     * Returns 1000
     *
     * {@inheritDoc}
     */
    @Override
    public double getWidth() {
        return 1000;
    }

    /**
     * Returns 500
     *
     * {@inheritDoc}
     */
    @Override
    public double getHeight() {
        return 500;
    }

    @Override
    protected void createScene(final Parent root) {
        super.createScene(root);
        this.scene.getStylesheets().add(SceneManager.GAME_INSTRUCTIONS_STYLE_SHEET);

        Platform.runLater(() -> {
            if (scene != null && scene.getWindow() != null) {
                scene.getWindow().setOnCloseRequest(event -> {
                    event.consume();
                    closeApplication();
                });
            }
        });
    }

    /**
     * Initializes the MainMenuPresenter
     *
     * This method initializes the MainMenuPresenter
     *
     */
    @FXML
    public void initialize() {
        gameInstructionsGridPane.setVisible(false);
        userContainerEntityListController.setTitle("Eingeloggte Benutzer");
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
        userService.retrieveAllUsers();
    }

    /**
     * Handles new logged in users
     *
     * If a new UserLoggedInMessage object is posted to the EventBus, the full
     * list of users currently logged in is requested.
     * Furthermore if the LOG-Level is set to DEBUG the message "New user {@literal
     * <Username>} logged in." is displayed in the log.
     *
     * @param message the UserLoggedInMessage object seen on the EventBus
     * @see LoginServerMessage
     * @since 2019-08-29
     */
    @Subscribe
    public void onUserLoggedInMessage(LoginServerMessage message) {
        LOG.debug("New user {}  logged in,", message.getUser().getUsername());
        if (loggedInUserProvider.get() != null) {
            userService.retrieveAllUsers();
        }
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
     * @see RetrieveAllOnlineUsersResponse
     * @see UserContainerEntityListPresenter#setList(Collection)
     * @since 2019-08-29
     */
    @Subscribe
    public void onAllOnlineUsersResponse(RetrieveAllOnlineUsersServerMessage allUsersResponse) {
        LOG.debug("Update of user list {}", allUsersResponse.getUsers());
        final List<UserContainerEntity> usersWithUsernames = new ArrayList<>(allUsersResponse.getUsers());
        this.userContainerEntityListController.setList(usersWithUsernames);
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
        userService.logout(loggedInUserProvider.get());
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

    /**
     * Handles the application close button event
     *
     * @param event The ActionEvent created by pressing the close button
     */
    @FXML
    private void onApplicationCloseButton(ActionEvent event) {
        closeApplication();
    }

    /**
     * Performs a clean shutdown of the application by logging out and closing
     */
    private void closeApplication() {
        userService.logout(loggedInUserProvider.get());
        Platform.runLater(() -> {
            javafx.application.Platform.exit();
            System.exit(0);
        });
    }
}
