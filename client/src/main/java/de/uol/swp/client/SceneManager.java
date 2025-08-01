package de.uol.swp.client;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import de.uol.swp.client.lobby.LobbyCreatePresenter;
import de.uol.swp.client.lobby.LobbyOverviewPresenter;
import de.uol.swp.client.lobby.LobbyPresenter;
import de.uol.swp.client.lobby.events.ShowLobbyCreateViewEvent;
import de.uol.swp.client.lobby.events.ShowLobbyOverviewViewEvent;
import de.uol.swp.client.lobby.events.ShowLobbyViewEvent;
import de.uol.swp.client.main_menu.MainMenuPresenter;
import de.uol.swp.client.main_menu.events.ShowMainMenuEvent;
import de.uol.swp.client.user.LoginPresenter;
import de.uol.swp.client.user.RegistrationPresenter;
import de.uol.swp.client.user.event.RegistrationCanceledEvent;
import de.uol.swp.client.user.event.RegistrationErrorEvent;
import de.uol.swp.client.user.event.ShowLoginViewEvent;
import de.uol.swp.client.user.event.ShowRegistrationViewEvent;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.response.JoinUserUserAlreadyInLobbyLobbyResponse;
import de.uol.swp.common.role.response.RoleUnavailableResponse;
import de.uol.swp.common.user.User;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.Objects;

/**
 * Class that manages which window/scene is currently shown
 */
public class SceneManager {

    static final Logger LOG = LogManager.getLogger(SceneManager.class);
    public static final String STYLE_SHEET = "css/swp.css";
    static final String DIALOG_STYLE_SHEET = "css/myDialog.css";
    static final String ERROR_DIALOG_STYLE_SHEET = "css/errorDialog.css";
    static final String ICON_IMAGE_PATH = "/images/Logo.png";
    static final String ERROR_ICON_IMAGE_PATH = "/images/ErrorIcon.png";

    public static final String GAME_INSTRUCTIONS_STYLE_SHEET = "css/gameInstructions.css";

    private final Stage primaryStage;
    private final Image iconImage;
    private final Image errorIconImage;
    private Scene lastScene = null;
    private Scene currentScene = null;
    private String lastTitle;

    private final LoginPresenter loginPresenter = AbstractPresenter.loadFXMLPresenter(LoginPresenter.class);
    private final RegistrationPresenter registrationPresenter = AbstractPresenter.loadFXMLPresenter(RegistrationPresenter.class);
    private final MainMenuPresenter mainMenuPresenter = AbstractPresenter.loadFXMLPresenter(MainMenuPresenter.class);
    private final LobbyCreatePresenter lobbyCreatePresenter = AbstractPresenter.loadFXMLPresenter(LobbyCreatePresenter.class);
    private final LobbyOverviewPresenter lobbyOverviewPresenter = AbstractPresenter.loadFXMLPresenter(LobbyOverviewPresenter.class);

    private final Provider<FXMLLoader> loaderProvider;

    @Inject
    public SceneManager(EventBus eventBus, Provider<FXMLLoader> loaderProvider, @Assisted Stage primaryStage) throws IOException {
        eventBus.register(this);
        this.primaryStage = primaryStage;
        this.loaderProvider = loaderProvider;
        this.iconImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ICON_IMAGE_PATH)));
        this.errorIconImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ERROR_ICON_IMAGE_PATH)));
        primaryStage.getIcons().add(iconImage);
    }

    /**
     * Handles ShowMainMenuEvent detected on the EventBus
     *
     * <p>
     * If a {@link ShowMainMenuEvent} is detected on the EventBus, this method gets
     * called. It calls a method to switch the current screen to the main menu
     * screen.
     * </p>
     *
     * @param event The ShowMainMenuEvent detected on the EventBus
     * @see de.uol.swp.client.main_menu.events.ShowMainMenuEvent
     */
    @Subscribe
    public void onShowMainMenuEvent(final ShowMainMenuEvent event){
        showMainScreen(event.getUser());
    }

    /**
     * Handles ShowRegistrationViewEvent detected on the EventBus
     * <p>
     * If a ShowRegistrationViewEvent is detected on the EventBus, this method gets
     * called. It calls a method to switch the current screen to the registration
     * screen.
     *
     * @param event The ShowRegistrationViewEvent detected on the EventBus
     * @see ShowRegistrationViewEvent
     */
    @Subscribe
    public void onShowRegistrationViewEvent(ShowRegistrationViewEvent event){
        showRegistrationScreen();
    }

    /**
     * Handles ShowLoginViewEvent detected on the EventBus
     * <p>
     * If a ShowLoginViewEvent is detected on the EventBus, this method gets
     * called. It calls a method to switch the current screen to the login screen.
     *
     * @param event The ShowLoginViewEvent detected on the EventBus
     * @see ShowLoginViewEvent
     */
    @Subscribe
    public void onShowLoginViewEvent(ShowLoginViewEvent event){
        showLoginScreen();
    }

    /**
     * Handles ShowLobbyCreateViewEvent detected on the EventBus
     * <p>
     * If a ShowLobbyCreateViewEvent is detected on the EventBus, this method gets
     * called. It calls a method to switch the current screen to the lobby create screen.
     *
     * @param event The ShowLobbyCreateViewEvent detected on the EventBus
     * @see ShowLobbyCreateViewEvent
     */
    @Subscribe
    public void onShowLobbyCreateViewEvent(final ShowLobbyCreateViewEvent event) {
        showLobbyCreateScreen();
    }

    /**
     * Handles RegistrationCanceledEvent detected on the EventBus
     * <p>
     * If a RegistrationCanceledEvent is detected on the EventBus, this method gets
     * called. It calls a method to show the screen shown before registration.
     *
     * @param event The RegistrationCanceledEvent detected on the EventBus
     * @see RegistrationCanceledEvent
     */
    @Subscribe
    public void onRegistrationCanceledEvent(RegistrationCanceledEvent event){
        showScene(loginPresenter, lastTitle);
    }

    /**
     * Handles RegistrationErrorEvent detected on the EventBus
     * <p>
     * If a RegistrationErrorEvent is detected on the EventBus, this method gets
     * called. It shows the error message of the event in a error alert.
     *
     * @param event The RegistrationErrorEvent detected on the EventBus
     * @see RegistrationErrorEvent
     */
    @Subscribe
    public void onRegistrationErrorEvent(RegistrationErrorEvent event) {
        showError(event.getMessage());
    }

    /**
     * Handles ShowLobbyOverviewViewEvent detected on the EventBus
     *
     * <p>
     * If a ShowLobbyOverviewViewEvent is detected on the EventBus, this method gets called.
     * It calls a method to switch the current screen to the lobbyOverview screen.
     * </p>
     *
     * @param event The ShowLobbyOverviewViewEvent detected on the EventBus
     * @see de.uol.swp.client.lobby.events.ShowLobbyOverviewViewEvent
     */
    @Subscribe
    public void onShowLobbyOverviewEvent(final ShowLobbyOverviewViewEvent event) {
        showLobbyOverviewScreen();
    }

    /**
     * Handles JoinUserUserAlreadyInLobbyLobbyResponse detected on the EventBus
     *
     * <p>
     * If a {@link JoinUserUserAlreadyInLobbyLobbyResponse} is detected on the EventBus, this method gets called.
     * It calls a method to open an error screen.
     * </p>
     *
     * @param event The JoinUserUserAlreadyInLobbyLobbyResponse detected on the EventBus
     * @see JoinUserUserAlreadyInLobbyLobbyResponse
     */
    @Subscribe
    public void onLobbyJoinUserUserAlreadyInLobbyResponse(final JoinUserUserAlreadyInLobbyLobbyResponse event) {
        showError("", "Du kannst der Lobby \""+event.getLobby().getName()+"\" nicht beitreten, da du bereits in ihr bist.");
    }

    /**
     * Handles ShowLobbyViewEvent detected on the EventBus
     * <p>
     * If a {@link ShowLobbyViewEvent} is detected on the EventBus, this method gets
     * called. It opens a new lobby window for the created lobby.
     *
     * @param event The ShowLobbyViewEvent detected on the EventBus
     * @see ShowLobbyViewEvent
     */
    @Subscribe
    public void onShowLobbyViewEvent(final ShowLobbyViewEvent event) {
        showLobbyScreen(event.getLobby());
    }

    /**
     * Shows an error message inside an error alert
     *
     * @param message The type of error to be shown
     * @param e       The error message
     */
    public void showError(String message, String e) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.ERROR, message + e);
            // based on: https://stackoverflow.com/questions/28417140/styling-default-javafx-dialogs/28421229#28421229
            DialogPane pane = a.getDialogPane();
            ImageView imageView = new ImageView(this.errorIconImage);
            a.setGraphic(imageView);
            if (pane.getScene().getWindow() instanceof Stage stage) {
                stage.getIcons().add(iconImage);
            }
            pane.getStylesheets().add(ERROR_DIALOG_STYLE_SHEET);
            a.showAndWait();
        });
    }

    /**
     * Shows a server error message inside an error alert
     *
     * @param e The error message
     */
    public void showServerError(String e) {
        showError("" , e);
    }

    /**
     * Shows an error message inside an error alert
     *
     * @param e The error message
     */
    public void showError(String e) {
        showError("" , e);
    }

    /**
     * Switches the current scene and title to the given ones
     * <p>
     * The current scene and title are saved in the lastScene and lastTitle variables,
     * before the new scene and title are set and shown.
     *
     * @param presenter Presenter of the new scene to show
     * @param title New window title
     */
    private void showScene(final AbstractPresenter presenter, final String title) {
        this.lastScene = currentScene;
        this.lastTitle = primaryStage.getTitle();
        this.currentScene = presenter.getScene();
        Platform.runLater(() -> {
            primaryStage.setTitle(title);
            primaryStage.setScene(presenter.getScene());
            presenter.setStage(primaryStage);
            primaryStage.show();
        });
    }

    /**
     * Shows the main menu
     * <p>
     * Switches the current Scene to the mainScene and sets the title of
     * the window to "Welcome " and the username of the current user
     *
     */
    public void showMainScreen(User currentUser) {
        showScene(mainMenuPresenter, "Welcome " + currentUser.getUsername());
    }

    /**
     * Shows the login screen
     * <p>
     * Switches the current Scene to the loginScene and sets the title of
     * the window to "Login"
     *
     */
    public void showLoginScreen() {
        showScene(loginPresenter,"Login");
    }

    /**
     * Shows the registration screen
     * <p>
     * Switches the current Scene to the registrationScene and sets the title of
     * the window to "Registration"
     *
     */
    public void showRegistrationScreen() {
        showScene(registrationPresenter,"Registrierung");
    }

    /**
     * Shows the lobby create screen
     * <p>
     * Switches the current Scene to the {@link #lobbyCreatePresenter} screen and sets the title of
     * the window to "Lobby erstellen"
     *
     */
    public void showLobbyCreateScreen() {
        showScene(lobbyCreatePresenter,"Lobby erstellen");
    }

    /**
     * Shows the lobbyOverview screen
     *
     * <p>
     * Switches the current Scene to the lobbyOverviewScene and sets the title of the window
     * </p>
     *
     */
    public void showLobbyOverviewScreen() {
        showScene(lobbyOverviewPresenter, "Lobby-Übersicht");
    }

    /**
     * Shows the lobby screen associated with the given lobby
     *
     * <p>
     * Opens a new window with the lobby screen associated with the given lobby.
     * </p>
     *
     * @param lobby Lobby to show window for
     */
    public void showLobbyScreen(final Lobby lobby) {
        try {
            final LobbyPresenter lobbyPresenter = AbstractPresenter.loadFXMLPresenter(LobbyPresenter.class);
            lobbyPresenter.openInNewWindow(iconImage);
            lobbyPresenter.initialize(lobby);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the event when a role is unavailable.
     * Displays an information alert with a message indicating that the role is already taken.
     *
     * @param roleUnavailableMessage The message containing information about the unavailable role.
     */
    @Subscribe
    public void onRoleUnavailableMessage(RoleUnavailableResponse roleUnavailableMessage) {
        onShowRoleIsUnavailable();
    }

    /**
     * Shows an alert indicating that the selected role is unavailable.
     * The alert provides the user with instructions to choose a different role.
     */
    public void onShowRoleIsUnavailable() {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Achtung!");
            a.setContentText("Die Rolle ist bereits vergeben.");
            DialogPane pane = a.getDialogPane();
            ImageView imageView = new ImageView(this.errorIconImage);
            a.setGraphic(imageView);
            if (pane.getScene().getWindow() instanceof Stage stage) {
                stage.getIcons().add(iconImage);
            }
            pane.getStylesheets().add(ERROR_DIALOG_STYLE_SHEET);
            a.showAndWait();
        });
    }

}
