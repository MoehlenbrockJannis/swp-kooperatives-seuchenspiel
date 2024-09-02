package de.uol.swp.client.lobby;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.common.lobby.Lobby;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.NoArgsConstructor;

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
}