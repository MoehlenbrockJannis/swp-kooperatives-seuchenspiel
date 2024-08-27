package de.uol.swp.client;

import com.google.inject.Provider;
import de.uol.swp.client.lobby.LobbyCreatePresenter;
import de.uol.swp.client.lobby.LobbyPresenter;
import de.uol.swp.client.lobby.event.LobbyCreatedEvent;
import de.uol.swp.client.lobby.event.OpenLobbyWindowEvent;
import de.uol.swp.client.lobby.event.ShowLobbyCreateScreenEvent;
import de.uol.swp.common.lobby.message.LobbyCreatedMessage;
import javafx.scene.image.Image;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.google.inject.Inject;
import de.uol.swp.client.auth.LoginPresenter;
import de.uol.swp.client.auth.events.ShowLoginViewEvent;
import de.uol.swp.client.main.MainMenuPresenter;
import de.uol.swp.client.main.event.ReturnToMainMenuEvent;
import de.uol.swp.client.register.RegistrationPresenter;
import de.uol.swp.client.register.event.RegistrationCanceledEvent;
import de.uol.swp.client.register.event.RegistrationErrorEvent;
import de.uol.swp.client.register.event.ShowRegistrationViewEvent;
import de.uol.swp.common.user.User;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;

/**
 * Class that manages which window/scene is currently shown
 *
 * @author Marco Grawunder
 * @since 2019-09-03
 */
public class SceneManager {

    static final Logger LOG = LogManager.getLogger(SceneManager.class);
    static final String STYLE_SHEET = "css/swp.css";
    static final String DIALOG_STYLE_SHEET = "css/myDialog.css";

    private Stage primaryStage;
    private Scene loginScene;
    private String lastTitle;
    private Scene registrationScene;
    private Scene mainScene;
    private Scene lastScene = null;
    private Scene currentScene = null;
    private Scene lobbyCreateScene;
    private final EventBus eventBus;

    private final Provider<FXMLLoader> loaderProvider;


    @Inject
    public SceneManager(EventBus eventBus, Provider<FXMLLoader> loaderProvider) {
        this.eventBus = eventBus;
        this.loaderProvider = loaderProvider;
        this.eventBus.register(this);
    }

    public void initialize(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        Image iconImage = new Image(getClass().getResourceAsStream("/pictures/PandemielogoSimpel.jpg"));
        primaryStage.getIcons().add(iconImage);
        initViews();
    }



    /**
     * Subroutine to initialize all views
     *
     * This is a subroutine of the constructor to initialize all views
     * @since 2019-09-03
     */
    private void initViews() throws IOException {
        initLoginView();
        initMainView();
        initRegistrationView();
        initLobbyCreateView();
    }
    /**
     * Subroutine creating parent panes from FXML files
     *
     * This Method tries to create a parent pane from the FXML file specified by
     * the URL String given to it. If the LOG-Level is set to Debug or higher loading
     * is written to the LOG.
     * If it fails to load the view a RuntimeException is thrown.
     *
     * @param fxmlFile FXML file to load the view from
     * @return view loaded from FXML or null
     * @since 2019-09-03
     */
    private Parent initPresenter(String fxmlFile) throws IOException {
        Parent rootPane;
        FXMLLoader loader = loaderProvider.get();
        try {
            URL url = getClass().getResource(fxmlFile);
            LOG.debug("Loading {}", url);
            loader.setLocation(url);
            rootPane = loader.load();
        } catch (Exception e) {
            throw new IOException(String.format("Could not load View! %s", e.getMessage()), e);
        }
        return rootPane;
    }

    /**
     * Initializes the main menu view
     *
     * If the mainScene is null it gets set to a new scene containing the
     * a pane showing the main menu view as specified by the MainMenuView
     * FXML file.
     *
     * @see de.uol.swp.client.main.MainMenuPresenter
     * @since 2019-09-03
     */
    private void initMainView() throws IOException {
        if (mainScene == null) {
            Parent rootPane = initPresenter(MainMenuPresenter.FXML);
            mainScene = new Scene(rootPane, 1000, 500);
            mainScene.getStylesheets().add(STYLE_SHEET);
        }
    }

    /**
     * Initializes the login view
     *
     * If the loginScene is null it gets set to a new scene containing the
     * a pane showing the login view as specified by the LoginView FXML file.
     *
     * @see de.uol.swp.client.auth.LoginPresenter
     * @since 2019-09-03
     */
    private void initLoginView() throws IOException {
        if (loginScene == null) {
            Parent rootPane = initPresenter(LoginPresenter.FXML);
            loginScene = new Scene(rootPane, 400, 200);
            loginScene.getStylesheets().add(STYLE_SHEET);
        }
    }

    /**
     * Initializes the registration view
     *
     * If the registrationScene is null it gets set to a new scene containing the
     * a pane showing the registration view as specified by the RegistrationView
     * FXML file.
     *
     * @see de.uol.swp.client.register.RegistrationPresenter
     * @since 2019-09-03
     */
    private void initRegistrationView() throws IOException {
        if (registrationScene == null){
            Parent rootPane = initPresenter(RegistrationPresenter.FXML);
            registrationScene = new Scene(rootPane, 400,200);
            registrationScene.getStylesheets().add(STYLE_SHEET);
        }
    }

    private void initLobbyCreateView() throws IOException {
        if (lobbyCreateScene == null) {
            Parent rootPane = initPresenter("/fxml/LobbyCreateView.fxml");
            lobbyCreateScene = new Scene(rootPane, 400, 300);
            lobbyCreateScene.getStylesheets().add(STYLE_SHEET);
        }
    }

    /**
     * Handles ShowRegistrationViewEvent detected on the EventBus
     *
     * If a ShowRegistrationViewEvent is detected on the EventBus, this method gets
     * called. It calls a method to switch the current screen to the registration
     * screen.
     *
     * @param event The ShowRegistrationViewEvent detected on the EventBus
     * @see de.uol.swp.client.register.event.ShowRegistrationViewEvent
     * @since 2019-09-03
     */
    @Subscribe
    public void onShowRegistrationViewEvent(ShowRegistrationViewEvent event){
        showRegistrationScreen();
    }

    /**
     * Handles ShowLoginViewEvent detected on the EventBus
     *
     * If a ShowLoginViewEvent is detected on the EventBus, this method gets
     * called. It calls a method to switch the current screen to the login screen.
     *
     * @param event The ShowLoginViewEvent detected on the EventBus
     * @see de.uol.swp.client.auth.events.ShowLoginViewEvent
     * @since 2019-09-03
     */
    @Subscribe
    public void onShowLoginViewEvent(ShowLoginViewEvent event){
        showLoginScreen();
    }

    /**
     * Handles RegistrationCanceledEvent detected on the EventBus
     *
     * If a RegistrationCanceledEvent is detected on the EventBus, this method gets
     * called. It calls a method to show the screen shown before registration.
     *
     * @param event The RegistrationCanceledEvent detected on the EventBus
     * @see de.uol.swp.client.register.event.RegistrationCanceledEvent
     * @since 2019-09-03
     */
    @Subscribe
    public void onRegistrationCanceledEvent(RegistrationCanceledEvent event){
        showScene(lastScene, lastTitle);
    }

    /**
     * Handles RegistrationErrorEvent detected on the EventBus
     *
     * If a RegistrationErrorEvent is detected on the EventBus, this method gets
     * called. It shows the error message of the event in a error alert.
     *
     * @param event The RegistrationErrorEvent detected on the EventBus
     * @see de.uol.swp.client.register.event.RegistrationErrorEvent
     * @since 2019-09-03
     */
    @Subscribe
    public void onRegistrationErrorEvent(RegistrationErrorEvent event) {
        showError(event.getMessage());
    }

    /**
     * Shows an error message inside an error alert
     *
     * @param message The type of error to be shown
     * @param e       The error message
     * @since 2019-09-03
     */
    public void showError(String message, String e) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.ERROR, message + e);
            // based on: https://stackoverflow.com/questions/28417140/styling-default-javafx-dialogs/28421229#28421229
            DialogPane pane = a.getDialogPane();
            pane.getStylesheets().add(DIALOG_STYLE_SHEET);
            a.showAndWait();
        });
    }

    /**
     * Shows a server error message inside an error alert
     *
     * @param e The error message
     * @since 2019-09-03
     */
    public void showServerError(String e) {
        showError("Server returned an error:\n" , e);
    }

    /**
     * Shows an error message inside an error alert
     *
     * @param e The error message
     * @since 2019-09-03
     */
    public void showError(String e) {
        showError("Error:\n" , e);
    }

    /**
     * Switches the current scene and title to the given ones
     *
     * The current scene and title are saved in the lastScene and lastTitle variables,
     * before the new scene and title are set and shown.
     *
     * @param scene New scene to show
     * @param title New window title
     * @since 2019-09-03
     */
    private void showScene(final Scene scene, final String title) {
        this.lastScene = currentScene;
        this.lastTitle = primaryStage.getTitle();
        this.currentScene = scene;
        Platform.runLater(() -> {
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.show();
        });
    }

    /**
     * Shows the login error alert
     *
     * Opens an ErrorAlert popup saying "Error logging in to server"
     *
     * @since 2019-09-03
     */
    public void showLoginErrorScreen() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error logging in to server");
            // based on: https://stackoverflow.com/questions/28417140/styling-default-javafx-dialogs/28421229#28421229
            DialogPane pane = alert.getDialogPane();
            pane.getStylesheets().add(DIALOG_STYLE_SHEET);
            alert.showAndWait();
            showLoginScreen();
        });
    }

    /**
     * Shows the main menu
     *
     * Switches the current Scene to the mainScene and sets the title of
     * the window to "Welcome " and the username of the current user
     *
     * @since 2019-09-03
     */
    public void showMainScreen(User currentUser) {
        showScene(mainScene, "Welcome " + currentUser.getUsername());
    }

    /**
     * Shows the login screen
     *
     * Switches the current Scene to the loginScene and sets the title of
     * the window to "Login"
     *
     * @since 2019-09-03
     */
    public void showLoginScreen() {
        showScene(loginScene,"Login");
    }

    /**
     * Shows the registration screen
     *
     * Switches the current Scene to the registrationScene and sets the title of
     * the window to "Registration"
     *
     * @since 2019-09-03
     */
    public void showRegistrationScreen() {
        showScene(registrationScene,"Registration");
    }

    public void showLobbyCreateScreen(User loggedInUser) {
        if (loggedInUser == null) {
            System.out.println("Error: Cannot show lobby create screen with null user");
            return;
        }
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = loaderProvider.get();
                loader.setLocation(getClass().getResource("/fxml/LobbyCreateView.fxml"));
                Parent rootPane = loader.load();
                Scene lobbyCreateScene = new Scene(rootPane, 400, 300);
                lobbyCreateScene.getStylesheets().add(STYLE_SHEET);

                LobbyCreatePresenter presenter = loader.getController();
                System.out.println("Setting loggedInUser in SceneManager: " + loggedInUser.getUsername());
                presenter.setLoggedInUser(loggedInUser);

                primaryStage.setTitle("Create Lobby");
                primaryStage.setScene(lobbyCreateScene);
                primaryStage.show();
            } catch (IOException e) {
                LOG.error("Error showing lobby create screen", e);
                showError("Could not open lobby create screen");
            }
        });
    }

    public void showLobbyScreen(String lobbyName, User loggedInUser) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = loaderProvider.get();
                loader.setLocation(getClass().getResource("/fxml/LobbyView.fxml"));
                Parent rootPane = loader.load();

                LobbyPresenter presenter = loader.getController();
                presenter.setLoggedInUser(loggedInUser);
                presenter.initialize(lobbyName);

                Scene lobbyScene = new Scene(rootPane, 400, 300);
                lobbyScene.getStylesheets().add(STYLE_SHEET);

                primaryStage.setTitle("Lobby: " + lobbyName);
                primaryStage.setScene(lobbyScene);
                primaryStage.show();
            } catch (IOException e) {
                LOG.error("Error showing lobby screen", e);
                showError("Could not open lobby screen");
            }
        });
    }

    @Subscribe
    public void onShowLobbyCreateScreenEvent(ShowLobbyCreateScreenEvent event) {
        User user = event.getUser();
        if (user != null) {
            showLobbyCreateScreen(user);
        } else {
            System.out.println("Error: Cannot show lobby create screen with null user");
        }
    }

    @Subscribe
    public void onReturnToMainMenuEvent(ReturnToMainMenuEvent event) {
        Platform.runLater(() -> {
            User currentUser = event.getUser();
            if (currentUser != null) {
                showMainScreen(currentUser);
            } else {
                System.out.println("Warning: User is null in ReturnToMainMenuEvent");
                // Hier könnten Sie einen alternativen Weg zum Hauptmenü implementieren
                // Zum Beispiel: showLoginScreen();
            }
        });
    }

    @Subscribe
    public void onLobbyCreatedEvent(LobbyCreatedEvent event) {
        showLobbyScreen(event.getLobbyName(), event.getUser());
    }

    @Subscribe
    public void onLobbyCreatedMessage(LobbyCreatedMessage message) {
        Platform.runLater(() -> {
            eventBus.post(new OpenLobbyWindowEvent(message.getName(), message.getUser()));
        });
    }

    @Subscribe
    public void onOpenLobbyWindowEvent(OpenLobbyWindowEvent event) {
        Platform.runLater(() -> {
            openLobbyInNewWindow(event.getLobbyName(), event.getUser());
        });
    }

    private void openLobbyInNewWindow(String lobbyName, User user) {
        try {
            FXMLLoader loader = loaderProvider.get();
            loader.setLocation(getClass().getResource("/fxml/LobbyView.fxml"));
            Parent rootPane = loader.load();

            LobbyPresenter presenter = loader.getController();
            presenter.setLoggedInUser(user);
            presenter.initialize(lobbyName);

            Scene lobbyScene = new Scene(rootPane, 400, 300);
            lobbyScene.getStylesheets().add(STYLE_SHEET);

            Stage lobbyStage = new Stage();
            lobbyStage.setTitle("Lobby: " + lobbyName);
            lobbyStage.setScene(lobbyScene);
            lobbyStage.show();
        } catch (IOException e) {
            LOG.error("Error opening lobby in new window", e);
            showError("Could not open lobby window");
        }
    }


}
