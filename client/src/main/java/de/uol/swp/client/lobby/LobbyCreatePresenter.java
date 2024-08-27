package de.uol.swp.client.lobby;

import com.google.inject.Inject;
import de.uol.swp.client.lobby.event.LobbyCreatedEvent;
import de.uol.swp.client.main.event.ReturnToMainMenuEvent;
import de.uol.swp.common.user.User;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;

public class LobbyCreatePresenter {
    @FXML
    private TextField lobbyNameField;

    private final EventBus eventBus;
    private final LobbyService lobbyService;
    private User loggedInUser;

    @Inject
    public LobbyCreatePresenter(EventBus eventBus, LobbyService lobbyService) {
        this.eventBus = eventBus;
        this.lobbyService = lobbyService;
    }

    public void setLoggedInUser(User user) {
        System.out.println("Setting loggedInUser: " + (user != null ? user.getUsername() : "null"));
        this.loggedInUser = user;
    }

    @FXML
    void onCreateButtonClicked() {
        String lobbyName = lobbyNameField.getText();
        if (!lobbyName.isEmpty() && loggedInUser != null) {
            lobbyService.createNewLobby(lobbyName);
            eventBus.post(new LobbyCreatedEvent(lobbyName, loggedInUser));
            eventBus.post(new ReturnToMainMenuEvent(loggedInUser));
            closeCurrentWindow();
        }
    }

    private void closeCurrentWindow() {
        Stage stage = (Stage) lobbyNameField.getScene().getWindow();
        stage.close();
    }

    @FXML
    void onCancelButtonClicked() {
        System.out.println("Cancel button clicked. loggedInUser: " + (loggedInUser != null ? loggedInUser.getUsername() : "null"));
        eventBus.post(new ReturnToMainMenuEvent(this.loggedInUser));
    }
}