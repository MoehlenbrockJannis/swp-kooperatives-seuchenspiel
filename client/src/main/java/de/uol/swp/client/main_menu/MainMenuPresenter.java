package de.uol.swp.client.main_menu;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.SceneManager;
import de.uol.swp.client.lobby.LobbyService;
import de.uol.swp.client.lobby.events.ShowLobbyCreateViewEvent;
import de.uol.swp.client.lobby.events.ShowLobbyOverviewViewEvent;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.client.user.UserContainerEntityListPresenter;
import de.uol.swp.client.user.event.ShowLoginViewEvent;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.server_message.CreateGameServerMessage;
import de.uol.swp.common.game.server_message.RetrieveUpdatedGameServerMessage;
import de.uol.swp.common.user.UserContainerEntity;
import de.uol.swp.common.user.response.LoginSuccessfulResponse;
import de.uol.swp.common.user.server_message.LoginServerMessage;
import de.uol.swp.common.user.server_message.RetrieveAllOnlineUsersServerMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.greenrobot.eventbus.Subscribe;

import java.util.*;

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

    private final Set<Game> games = new HashSet<>();

    /**
     * Returns 1000
     * <p>
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
    }

    @Override
    protected void onCloseStageEvent(final WindowEvent event) {
        event.consume();
        Platform.runLater(this::closeApplication);
    }

    /**
     * Initializes the MainMenuPresenter
     */
    @FXML
    public void initialize() {
        gameInstructionsGridPane.setVisible(false);
        userContainerEntityListController.setTitle("Eingeloggte Benutzer");
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
        userService.retrieveAllUsers();
    }

    /**
     * Handles new logged in users
     * <p>
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
     * <p>
     * If a new AllOnlineUsersResponse object is posted to the EventBus the names
     * of currently logged in users are put onto the user list in the main menu.
     * Furthermore if the LOG-Level is set to DEBUG the message "Update of user
     * list" with the names of all currently logged in users is displayed in the
     * log.
     *
     * @param allUsersResponse the AllOnlineUsersResponse object seen on the EventBus
     * @see RetrieveAllOnlineUsersServerMessage
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
     * <p>
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
        final Runnable logoutCallback = () -> {
            userService.logout(loggedInUserProvider.get());
            this.eventBus.post(new ShowLoginViewEvent());
        };
        showGameLogoutConfirmationDialog("Ausloggen", logoutCallback);
    }

    /**
     * Shows a logout confirmation dialog if the user is in a game.
     * If the user accepts the dialog or he is not in a game, the {@code callback} is executed.
     *
     * @param title title of the dialog
     * @param callback executed callback after accepting
     */
    private void showGameLogoutConfirmationDialog(final String title, final Runnable callback) {
        if (!this.games.isEmpty()) {
            final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(title);
            alert.setHeaderText("Achtung! Du befindest dich in einem aktivem Spiel!");
            alert.setContentText("Möchtest du dich trotzdem ausloggen?");

            final ButtonType confirmButton = new ButtonType("Ja", ButtonBar.ButtonData.OK_DONE);
            final ButtonType cancelButton = new ButtonType("Nein", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(confirmButton, cancelButton);

            final Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == confirmButton) {
                callback.run();
            }
        } else {
            callback.run();
        }
    }

    /**
     * Handles the event when the game instructions button is pressed.
     * <p>
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
     * Performs a clean shutdown of the application.
     * Shows a confirmation dialog if the user is in an active lobby.
     */
    private void closeApplication() {
        final Runnable shutdown = this::performShutdown;
        showGameLogoutConfirmationDialog("Anwendung schließen", shutdown);
    }

    /**
     * Performs the actual shutdown sequence for the application.
     */
    private void performShutdown() {
        userService.logout(loggedInUserProvider.get());
        Platform.runLater(() -> {
            Platform.exit();
            System.exit(0);
        });
    }

    /**
     * Event handler called when a game is created.
     *
     * @param message the message containing the created game information
     */
    @Subscribe
    public void onGameCreated(CreateGameServerMessage message) {
        final Game game = message.getGame();
        addOrRemoveGame(game);
    }

    /**
     * Event handler called when a game is updated.
     *
     * @param retrieveUpdatedGameServerMessage the message containing the created game information
     */
    @Subscribe
    public void onRetrieveUpdatedGameServerMessage(final RetrieveUpdatedGameServerMessage retrieveUpdatedGameServerMessage) {
        final Game game = retrieveUpdatedGameServerMessage.getGame();
        addOrRemoveGame(game);
    }

    /**
     * Adds to or removes from {@link #games} the given {@link Game}.
     *
     * @param game {@link Game} to add or remove
     */
    private void addOrRemoveGame(final Game game) {
        if (game.getLobby().containsUser(loggedInUserProvider.get())) {
            this.games.add(game);
        } else {
            this.games.remove(game);
        }
    }
}