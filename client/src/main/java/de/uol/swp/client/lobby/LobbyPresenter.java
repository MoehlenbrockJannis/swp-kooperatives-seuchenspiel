package de.uol.swp.client.lobby;

import com.google.inject.Inject;
import de.uol.swp.common.user.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;

public class LobbyPresenter {

    @FXML
    private Label lobbyNameLabel;

    private final EventBus eventBus;
    private final LobbyService lobbyService;
    private User loggedInUser;

    @Inject
    public LobbyPresenter(EventBus eventBus, LobbyService lobbyService) {
        this.eventBus = eventBus;
        this.lobbyService = lobbyService;
    }

    public void initialize(String lobbyName) {
        lobbyNameLabel.setText("Lobby: " + lobbyName);
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    @FXML
    void onLeaveLobbyButtonClicked(ActionEvent event) {
        lobbyService.leaveLobby(lobbyNameLabel.getText().replace("Lobby: ", ""));
        Stage stage = (Stage) lobbyNameLabel.getScene().getWindow();
        stage.close();
    }
}