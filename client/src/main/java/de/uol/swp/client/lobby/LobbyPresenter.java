package de.uol.swp.client.lobby;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.AbstractLobbyMessage;
import de.uol.swp.common.lobby.message.UserJoinedLobbyMessage;
import de.uol.swp.common.lobby.message.UserLeftLobbyMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.NoArgsConstructor;
import org.greenrobot.eventbus.Subscribe;

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
    private Label lobbyNameLabel;

    @Inject
    private LobbyService lobbyService;

    private Stage stage;
    private Lobby lobby;
    @Inject
    private LoggedInUserProvider loggedInUserProvider;

    public void initialize(final Stage stage, final Lobby lobby) {
        this.stage = stage;
        this.lobby = lobby;
        setTitle(lobby.getName());
        stage.getScene().getWindow().setOnCloseRequest(event -> lobbyService.leaveLobby(lobby.getName(), loggedInUserProvider.get()));
        stage.show();
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
    }

    @Subscribe
    public void onUserLeftLobbyMessage(final UserLeftLobbyMessage userLeftLobbyMessage) {
        final Runnable executable = () -> lobby.leaveUser(userLeftLobbyMessage.getUser());
        executeOnlyIfMessageIsForThisLobby(userLeftLobbyMessage, executable);
    }
}