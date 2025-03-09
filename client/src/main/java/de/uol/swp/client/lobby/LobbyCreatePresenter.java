package de.uol.swp.client.lobby;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.lobby.events.ShowLobbyViewEvent;
import de.uol.swp.client.main_menu.events.ShowMainMenuEvent;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.common.lobby.response.CreateLobbyResponse;
import de.uol.swp.common.user.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import lombok.NoArgsConstructor;
import org.greenrobot.eventbus.Subscribe;

/**
 * Manages the lobby create window
 *
 * @author Niklas Wrobel
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2024-08-28
 */
@NoArgsConstructor
public class LobbyCreatePresenter extends AbstractPresenter {
    @FXML
    private TextField lobbyNameField;

    @Inject
    private LobbyService lobbyService;
    @Inject
    private LoggedInUserProvider loggedInUserProvider;

    @FXML
    private void onCreateLobbyButtonClicked(final ActionEvent event) {
        final String lobbyName = lobbyNameField.getText();

        if (lobbyName.isEmpty()) {
            // TODO: throw exception and show error message
            return;
        }

        final User loggedInUser = loggedInUserProvider.get();
        if (loggedInUser == null) {
            // TODO: throw exception and show error message
            return;
        }

        clearInputFields();
        lobbyService.createNewLobby(lobbyName, loggedInUser);
    }

    /**
     * Clears the input fields
     *
     * Clears the input fields of the lobby name and password fields.
     *
     */
    private void clearInputFields() {
        lobbyNameField.clear();
    }

    @FXML
    private void onCancelButtonClicked(final ActionEvent event) {
        clearInputFields();
        backToMainMenu();
    }

    private void backToMainMenu() {
        final ShowMainMenuEvent showMainMenuEvent = new ShowMainMenuEvent(loggedInUserProvider.get());
        eventBus.post(showMainMenuEvent);
    }

    /**
     * Handles LobbyCreatedResponse detected on the EventBus
     *
     * If a {@link CreateLobbyResponse} is detected on the EventBus, this method gets
     * called. It posts a {@link ShowLobbyViewEvent} to the EventBus.
     *
     * @param event The LobbyCreatedResponse detected on the EventBus
     * @see CreateLobbyResponse
     * @since 2024-08-28
     */
    @Subscribe
    public void onLobbyCreatedResponse(final CreateLobbyResponse event) {
        final ShowLobbyViewEvent showLobbyViewEvent = new ShowLobbyViewEvent(event.getLobby());
        eventBus.post(showLobbyViewEvent);

        backToMainMenu();
    }
}