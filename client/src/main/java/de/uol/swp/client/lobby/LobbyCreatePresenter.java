package de.uol.swp.client.lobby;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.lobby.events.ShowLobbyViewEvent;
import de.uol.swp.client.main.events.ShowMainMenuEvent;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.common.lobby.response.CreateLobbyResponse;
import de.uol.swp.common.user.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
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

    @FXML
    private PasswordField lobbyPasswordField;

    @FXML
    private ComboBox<Integer> maxPlayersComboBox;

    @Inject
    private LobbyService lobbyService;
    @Inject
    private LoggedInUserProvider loggedInUserProvider;

    private static final int MIN_NUMBER_OF_PLAYERS = 2;
    private static final int MAX_NUMBER_OF_PLAYERS = 4;

    /**
     * Initializes the presenter
     *
     * Initializes the presenter by setting the range of the player amount combobox
     * and resetting the max players combobox selection.
     *
     */
    public void initialize() {
        setPlayerAmountRange(MIN_NUMBER_OF_PLAYERS, MAX_NUMBER_OF_PLAYERS);
        resetMaxPlayersComboBoxSelection();
    }

    /**
     * Resets the max players combobox selection to the first item.
     */
    private void resetMaxPlayersComboBoxSelection() {
        maxPlayersComboBox.getSelectionModel().selectFirst();
    }

    /**
     * Sets the range of the player amount combobox
     *
     * Sets the range of the player amount combobox to the given values.
     *
     * @param minNumberOfPlayers The minimum number of players
     * @param maxNumberOfPlayers The maximum number of players
     */
    private void setPlayerAmountRange(int minNumberOfPlayers, int maxNumberOfPlayers) {
        for (int i = minNumberOfPlayers; i <= maxNumberOfPlayers; i++) {
            maxPlayersComboBox.getItems().add(i);
        }
    }

    @FXML
    private void onCreateLobbyButtonClicked(final ActionEvent event) {
        final String lobbyName = lobbyNameField.getText();
        final int selectedMaxPlayers = maxPlayersComboBox.getValue();

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
        lobbyService.createNewLobby(lobbyName, loggedInUser, MIN_NUMBER_OF_PLAYERS, selectedMaxPlayers);
    }

    /**
     * Clears the input fields
     *
     * Clears the input fields of the lobby name and password fields.
     *
     */
    private void clearInputFields() {
        lobbyNameField.clear();
        lobbyPasswordField.clear();
        resetMaxPlayersComboBoxSelection();
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