package de.uol.swp.client.lobby;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.game.GamePresenter;
import de.uol.swp.client.game.GameService;
import de.uol.swp.client.chat.ChatPresenter;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.response.GameCreatedResponse;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyStatus;
import de.uol.swp.common.lobby.message.AbstractLobbyMessage;
import de.uol.swp.common.lobby.message.UserJoinedLobbyMessage;
import de.uol.swp.common.lobby.message.UserLeftLobbyMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

/**
 * Manages the lobby window
 *
 * @author Niklas Wrobel
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2024-08-28
 */
@NoArgsConstructor
public class LobbyPresenter extends AbstractPresenter {

    public static final String FXML = "/fxml/LobbyView.fxml";

    @FXML
    private Button startGameButton;

    @FXML
    private Label lobbyNameLabel;
    @Inject
    private LobbyService lobbyService;

    @Inject
    private GameService gameService;

    private Stage stage;
    @Getter
    private Lobby lobby;
    @Inject
    private LoggedInUserProvider loggedInUserProvider;
    @FXML
    private ChatPresenter chatController;

    static final String STYLE_SHEET = "css/swp.css";

    public void initialize(final Stage stage, final Lobby lobby) {
        this.stage = stage;
        this.lobby = lobby;
        updateStartGameButton();
        setTitle(lobby.getName());
        stage.getScene().getWindow().setOnCloseRequest(event -> lobbyService.leaveLobby(lobby.getName(), loggedInUserProvider.get()));
        stage.show();
        chatController.setLobby(lobby);
    }

    private void setTitle(final String lobbyName) {
        final String title = "Lobby: " + lobbyName;
        stage.setTitle(title);
        lobbyNameLabel.setText(title);
    }

    @FXML
    private void onLeaveLobbyButtonClicked(final ActionEvent event) {
        lobbyService.leaveLobby(lobby.getName(), loggedInUserProvider.get());
        stage.close();
    }

    private void executeOnlyIfMessageIsForThisLobby(final AbstractLobbyMessage lobbyMessage, final Runnable executable) {
        if (lobby.getName().equals(lobbyMessage.getLobbyName())) {
            executable.run();
        }
    }

    @Subscribe
    public void onUserJoinedLobbyMessage(final UserJoinedLobbyMessage userJoinedLobbyMessage) {
        final Runnable executable = () -> lobby.joinUser(userJoinedLobbyMessage.getUser());
        executeOnlyIfMessageIsForThisLobby(userJoinedLobbyMessage, executable);
        updateStartGameButton();
    }

    @Subscribe
    public void onUserLeftLobbyMessage(final UserLeftLobbyMessage userLeftLobbyMessage) {
        final Runnable executable = () -> lobby.leaveUser(userLeftLobbyMessage.getUser());
        executeOnlyIfMessageIsForThisLobby(userLeftLobbyMessage, executable);
        updateStartGameButton();
    }

    private void updateStartGameButton() {
        startGameButton.setDisable(!lobby.getOwner().equals(loggedInUserProvider.get()) || lobby.getUsers().size() < 2);
    }

    /**
     * Starts the game
     *
     * @param event The event that triggered the method
     */
    @FXML
    private void onStartGameButtonClicked(final ActionEvent event) {
        //TODO: add configuration as import parameters in createGame method
        lobbyService.updateLobbyStatus(lobby, LobbyStatus.RUNNING);
        gameService.createGame(lobby);
    }

    /**
     * Handles the response of the game creation
     *
     * @param event The event that triggered the method
     */
    @Subscribe
    public void onGameCreatedResponse(GameCreatedResponse event) {
        final Game game = event.getGame();
        final Lobby gameLobby = game.getLobby();

        final String gameLobbyName = gameLobby.getName();
        final String currentLobbyName = lobby.getName();

        if(gameLobbyName.equals(currentLobbyName)) {
            loadGameScene(game);
        }
    }

    /**
     * Loads the game scene
     *
     * @param game The game to be loaded
     */
    private void loadGameScene(Game game) {
        Platform.runLater(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/GameBoardView.fxml"));
                final Parent gameView = fxmlLoader.load();
                final GamePresenter gamePresenter = fxmlLoader.getController();

                final Scene gameScene = new Scene(gameView);
                gameScene.getStylesheets().add(STYLE_SHEET);

                stage.setScene(gameScene);
                stage.getScene().getWindow().setOnCloseRequest(event -> lobbyService.leaveLobby(lobby.getName(), loggedInUserProvider.get()));
                stage.setTitle("Game: " + game.getLobby().getName());

                gamePresenter.initialize(stage, game);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}