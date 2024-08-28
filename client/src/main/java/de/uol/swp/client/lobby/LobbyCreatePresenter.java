package de.uol.swp.client.lobby;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.main.event.ReturnToMainMenuEvent;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Manages the lobby create window
 *
 * @author Niklas Wrobel
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2024-08-28
 */
public class LobbyCreatePresenter extends AbstractPresenter {

    public static final String FXML = "/fxml/LobbyCreateView.fxml";

    @FXML
    private TextField lobbyNameField;

    @Inject
    private LobbyService lobbyService;
    private User loggedInUser; // TODO: replace on other branch

    @FXML
    private void onCreateButtonClicked(final ActionEvent event) {
        final String lobbyName = lobbyNameField.getText();

        if (lobbyName.isEmpty()) {
            // TODO: throw exception and show error message
            return;
        }

        if (loggedInUser == null) {
            // TODO: throw exception and show error message
            return;
        }

        lobbyService.createNewLobby(lobbyName, (UserDTO) loggedInUser);

        backToMainMenu();
    }

    @FXML
    private void onCancelButtonClicked(final ActionEvent event) {
        backToMainMenu();
    }

    private void backToMainMenu() {
        final ReturnToMainMenuEvent returnToMainMenuEvent = new ReturnToMainMenuEvent(loggedInUser);
        eventBus.post(returnToMainMenuEvent);
    }
}