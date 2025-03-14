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
 * @see de.uol.swp.client.AbstractPresenter
 */
@NoArgsConstructor
public class LobbyCreatePresenter extends AbstractPresenter {
    @FXML
    private TextField lobbyNameField;

    @Inject
    private LobbyService lobbyService;
    @Inject
    private LoggedInUserProvider loggedInUserProvider;

    /**
     * Handles the create lobby button click event.
     * Creates a new game lobby with the specified name if fields are valid
     * and the user is logged in.
     *
     * @param event The ActionEvent triggered by the button click
     */
    @FXML
    private void onCreateLobbyButtonClicked(final ActionEvent event) {
        final String lobbyName = lobbyNameField.getText();

        if (lobbyName.isEmpty()) {
            return;
        }

        final User loggedInUser = loggedInUserProvider.get();
        if (loggedInUser == null) {
            return;
        }

        clearInputFields();
        lobbyService.createNewLobby(lobbyName, loggedInUser);
    }

    /**
     * Clears the input fields
     * <p>
     * Clears the input fields of the lobby name and password fields.
     *
     */
    private void clearInputFields() {
        lobbyNameField.clear();
    }

    /**
     * Handles the cancel button click event by clearing input fields and returning to the main menu.
     *
     * @param event The ActionEvent triggered by the button click
     */
    @FXML
    private void onCancelButtonClicked(final ActionEvent event) {
        clearInputFields();
        backToMainMenu();
    }

    /**
     * Navigates back to the main menu for the currently logged in user.
     * Posts a ShowMainMenuEvent to the event bus.
     */
    private void backToMainMenu() {
        final ShowMainMenuEvent showMainMenuEvent = new ShowMainMenuEvent(loggedInUserProvider.get());
        eventBus.post(showMainMenuEvent);
    }

    /**
     * Handles LobbyCreatedResponse detected on the EventBus
     * <p>
     * If a {@link CreateLobbyResponse} is detected on the EventBus, this method gets
     * called. It posts a {@link ShowLobbyViewEvent} to the EventBus.
     *
     * @param event The LobbyCreatedResponse detected on the EventBus
     * @see CreateLobbyResponse
     */
    @Subscribe
    public void onLobbyCreatedResponse(final CreateLobbyResponse event) {
        final ShowLobbyViewEvent showLobbyViewEvent = new ShowLobbyViewEvent(event.getLobby());
        eventBus.post(showLobbyViewEvent);

        backToMainMenu();
    }
}