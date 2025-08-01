package de.uol.swp.client.lobby;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.lobby.events.ShowLobbyCreateViewEvent;
import de.uol.swp.client.lobby.events.ShowLobbyViewEvent;
import de.uol.swp.client.main_menu.events.ShowMainMenuEvent;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyStatus;
import de.uol.swp.common.lobby.response.JoinUserLobbyResponse;
import de.uol.swp.common.lobby.response.LobbyIsFullResponse;
import de.uol.swp.common.lobby.response.LobbyNotJoinableResponse;
import de.uol.swp.common.lobby.response.RetrieveAllLobbiesResponse;
import de.uol.swp.common.lobby.server_message.RetrieveAllLobbiesServerMessage;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.user.User;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import lombok.NoArgsConstructor;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Set;

/**
 * Manages the lobbyOverview window
 *
 * @see de.uol.swp.client.AbstractPresenter
 */
@NoArgsConstructor
public class LobbyOverviewPresenter extends AbstractPresenter {
    @Inject
    private LoggedInUserProvider loggedInUserProvider;
    @Inject
    private LobbyService lobbyService;
    private ObservableList<Lobby> lobbies;
    private Timeline autoRefreshTimer;

    @FXML
    private TableView<Lobby> lobbiesTable;

    @FXML
    private TableColumn<Lobby, String> nameColumn;
    @FXML
    private TableColumn<Lobby, String> ownerColumn;
    @FXML
    private TableColumn<Lobby, String> membersColumn;
    @FXML
    private TableColumn<Lobby, String> lobbyStatusColumn;

    /**
     * Called on initialization of LobbyOverviewView FXML file
     *
     * <p>
     *     Method sets the column widths to 0.5 for the {@link #nameColumn}, 0.3 for the {@link #ownerColumn}, 0.2 for the {@link #membersColumn} and 0.2 for the {@link #lobbyStatusColumn}.
     *     It also assigns a ValueFactory to each column.
     *     After that it loads all lobbies using the {@link #lobbyService}.
     * </p>
     *
     */
    @FXML
    public void initialize() {
        setColumnWidths(0.5, 0.2, 0.2, 0.1);

        initializeNameColumnValueFactory();
        initializeOwnerColumnValueFactory();
        initializeMembersColumnValueFactory();
        initializeLobbyStatusColumnValueFactory();

        initializeRowFactory();
    }

    /**
     * Sets widths of all columns of the {@link #lobbiesTable} to the specified values.
     * The sum of all parameters should not exceed 1.0.
     *
     * @param nameColumnWidth Width of the {@link #nameColumn}
     *                        <br>
     *                        <small>Must be between 0.0 and 1.0 (both inclusive), where 1.0 represents 100%.</small>
     * @param ownerColumnWidth Width of the {@link #ownerColumn}
     *                         <br>
     *                         <small>Must be between 0.0 and 1.0 (both inclusive), where 1.0 represents 100%.</small>
     * @param membersColumnWidth Width of the {@link #membersColumn}
     *                           <br>
     *                           <small>Must be between 0.0 and 1.0 (both inclusive), where 1.0 represents 100%.</small>
     *
     */
    public void setColumnWidths(final double nameColumnWidth, final double ownerColumnWidth, final double membersColumnWidth, final double lobbyStatusColumnWidth) {
        nameColumn.prefWidthProperty().bind(lobbiesTable.widthProperty().multiply(nameColumnWidth));
        ownerColumn.prefWidthProperty().bind(lobbiesTable.widthProperty().multiply(ownerColumnWidth));
        membersColumn.prefWidthProperty().bind(lobbiesTable.widthProperty().multiply(membersColumnWidth));
        lobbyStatusColumn.prefWidthProperty().bind(lobbiesTable.widthProperty().multiply(lobbyStatusColumnWidth));
    }

    /**
     * Sets a CellValueFactory to the {@link #nameColumn} to display the name of each lobby.
     *
     * @see de.uol.swp.common.lobby.Lobby
     */
    private void initializeNameColumnValueFactory() {
        nameColumn.setCellValueFactory(lobby -> new SimpleStringProperty(lobby.getValue().getName()));
    }

    /**
     * Sets a CellValueFactory to the {@link #ownerColumn} to display the username of the owner of each lobby.
     *
     * @see de.uol.swp.common.lobby.Lobby
     * @see de.uol.swp.common.user.User
     */
    private void initializeOwnerColumnValueFactory() {
        ownerColumn.setCellValueFactory(lobby -> new SimpleStringProperty(lobby.getValue().getOwner().getUsername()));
    }

    /**
     * Sets a CellValueFactory to the {@link #membersColumn} to display the number of users in each lobby.
     * Also adds a ToolTip to each cell containing the names of all users in the corresponding lobby.
     *
     * @see de.uol.swp.common.lobby.Lobby
     * @see de.uol.swp.common.user.User
     */
    private void initializeMembersColumnValueFactory() {
        membersColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(final String item, final boolean empty) {
                super.updateItem(item, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    final Lobby lobby = getTableRow().getItem();
                    final Set<Player> players = lobby.getPlayers();
                    if (players != null) {
                        setText(players.size() + " / " + lobby.getMaxPlayers());

                        final Tooltip tooltip = new Tooltip(players.stream()
                                .map(Player::getName)
                                .reduce((a, b) -> a + "\n" + b)
                                .orElse(""));
                        setTooltip(tooltip);
                    }
                }
            }
        });
    }

    /**
     * Sets a CellValueFactory to the {@link #lobbyStatusColumn} to display the status of each lobby.
     *
     * @see de.uol.swp.common.lobby.Lobby
     */
    private void initializeLobbyStatusColumnValueFactory() {
        lobbyStatusColumn.setCellValueFactory(lobby -> new SimpleStringProperty(lobby.getValue().getStatus().getStatus()));
    }

    /**
     * Adds a RowFactory to the {@link #lobbiesTable} with every row having an EventListener calling {@link #onLobbyRowClicked(Lobby)} with the corresponding lobby.
     *
     * @see de.uol.swp.common.lobby.Lobby
     * @see de.uol.swp.client.lobby.LobbyOverviewPresenter#onLobbyRowClicked(Lobby)
     */
    private void initializeRowFactory() {
        lobbiesTable.setRowFactory(tableView -> {
            final TableRow<Lobby> row = new TableRow<>();

            row.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                if (!row.isEmpty()) {
                    final Lobby lobby = row.getItem();
                    onLobbyRowClicked(lobby);
                }
            });

            return row;
        });
    }

    /**
     * Handles LobbyFindLobbiesResponse found on the EventBus
     *
     * <p>
     * If a {@link RetrieveAllLobbiesResponse} is detected on the EventBus, this method is called.
     * It calls {@link #setLobbyList(List)} with the list of all lobbies in the lobbyFindLobbiesResponse.
     * </p>
     *
     * @param retrieveAllLobbiesResponse The LobbyFindLobbiesResponse found on the EventBus
     * @see RetrieveAllLobbiesResponse
     * @see #setLobbyList(List)
     */
    @Subscribe
    public void onLobbyFindLobbiesResponse(final RetrieveAllLobbiesResponse retrieveAllLobbiesResponse) {
        setLobbyList(retrieveAllLobbiesResponse.getLobbies());
        startAutoRefreshTimer();
    }

    /**
     * Sets the {@link #lobbies} attribute that is displayed in the {@link #lobbiesTable}
     *
     * <p>
     *     If {@link #lobbies} is null, it is initialized first and is assigned to {@link #lobbiesTable} as items.
     *     Adds all lobbies in lobbyList the logged in user is not a member of to {@link #lobbies}.
     * </p>
     *
     * @param lobbyList List of all lobbies that should be displayed in the {@link #lobbiesTable}
     */
    public void setLobbyList(final List<Lobby> lobbyList) {
        Platform.runLater(() -> {
            if (lobbies == null) {
                lobbies = FXCollections.observableArrayList();
                lobbiesTable.setItems(lobbies);
            }
            lobbies.clear();
            final List<Lobby> lobbiesWithoutLoggedInUser = lobbyList.stream()
                    .filter(lobby -> !lobby.containsUser(loggedInUserProvider.get()))
                    .toList();
            lobbies.addAll(lobbiesWithoutLoggedInUser);
        });
    }

    /**
     * Method called when the back to menu button is pressed
     *
     * <p>
     * This Method is called when the back to menu button is pressed.
     * It posts a ShowMainMenuEvent to the EventBus.
     * </p>
     *
     * @see de.uol.swp.client.main_menu.events.ShowMainMenuEvent
     */
    @FXML
    private void onBackToMainMenuButtonPressed() {
        backToMainMenu();
    }

    /**
     * Method called when the refresh button is pressed
     *
     * <p>
     * Calls the {@link LobbyService#findLobbies()} method on the {@link #lobbyService}
     * </p>
     *
     * @param event The ActionEvent generated by pressing the refresh button
     * @see LobbyService#findLobbies()
     */
    @FXML
    private void onRefreshButtonPressed(final ActionEvent event) {
        lobbyService.findLobbies();
    }

    /**
     * Method called when the create lobby button is pressed
     *
     * @param event The ActionEvent generated by pressing the create lobby button
     */
    @FXML
    private void onCreateLobbyButtonPressed(final ActionEvent event) {
        final ShowLobbyCreateViewEvent showLobbyCreateViewEvent = new ShowLobbyCreateViewEvent();
        eventBus.post(showLobbyCreateViewEvent);
        resetExistingTimer();
    }

    /**
     * Callback for the row of the {@link #lobbiesTable} that was clicked
     *
     * <p>
     *     Adds the currently logged in user to the lobby associated with the clicked row by calling a {@link #lobbyService} method.
     * </p>
     *
     * @param lobby Lobby of the clicked row
     * @see LobbyService#joinLobby(Lobby, User)
     */
    private void onLobbyRowClicked(final Lobby lobby) {
        lobbyService.findLobbies();
        lobbyService.joinLobby(lobby, loggedInUserProvider.get());
    }

    /**
     * Shows an alert when a user tries to join a lobby that is not open based on the lobby status
     *
     * @param clickedLobbyStatus The status of the lobby the user tried to join
     */
    private void showLobbyJoinAlert(LobbyStatus clickedLobbyStatus) {
        String statusText = clickedLobbyStatus.getStatus();
        Alert lobbyJoinAlert = new Alert(Alert.AlertType.INFORMATION);
        lobbyJoinAlert.setTitle("Das hat leider nicht geklappt!");
        lobbyJoinAlert.setHeaderText("Ups! Diese Lobby ist gerade " + statusText + ".");
        lobbyJoinAlert.setContentText("Die Lobby, welcher Du beitreten wolltest, ist gerade leider " + statusText + ". Bitte versuche es bei einer anderen Lobby!");
        lobbyJoinAlert.showAndWait();
    }

    /**
     * Handles JoinUserLobbyResponse detected on the EventBus
     *
     * <p>
     * If a {@link JoinUserLobbyResponse} is detected on the EventBus, this method gets
     * called. It removes the joined lobby from {@link #lobbies} and posts a {@link ShowLobbyViewEvent} to the EventBus.
     * </p>
     *
     * @param joinUserLobbyResponse The JoinUserLobbyResponse detected on the EventBus
     * @see JoinUserLobbyResponse
     */
    @Subscribe
    public void onLobbyJoinUserResponse(final JoinUserLobbyResponse joinUserLobbyResponse) {
        setLobbyList(lobbies);

        final ShowLobbyViewEvent showLobbyViewEvent = new ShowLobbyViewEvent(joinUserLobbyResponse.getLobby());
        eventBus.post(showLobbyViewEvent);
        backToMainMenu();
    }

    /**
     * Handles UpdatedLobbyListServerMessage detected on the EventBus
     *
     * <p>
     * If a {@link RetrieveAllLobbiesServerMessage} is detected on the EventBus, this method gets
     * called. It calls {@link #setLobbyList(List)} with the list of all lobbies in the UpdatedLobbyListServerMessage.
     * </p>
     *
     * @param message The UpdatedLobbyListServerMessage detected on the EventBus
     * @see RetrieveAllLobbiesServerMessage
     * @see #setLobbyList(List)
     */
    @Subscribe
    public void onLobbyStatusUpdatedResponse(final RetrieveAllLobbiesServerMessage message) {
        setLobbyList(message.getLobbies());
    }

    /**
     * Navigates back to the main menu after resetting any active timers.
     * Posts a ShowMainMenuEvent to the event bus for the currently logged in user.
     */
    private void backToMainMenu() {
        resetExistingTimer();
        final ShowMainMenuEvent showMainMenuEvent = new ShowMainMenuEvent(loggedInUserProvider.get());
        eventBus.post(showMainMenuEvent);
    }

    /**
     * Handles LobbyIsFullResponse detected on the EventBus
     *
     * <p>
     * If a {@link LobbyIsFullResponse} is detected on the EventBus, this method gets
     * called. It shows an alert to the user indicating that the lobby they tried to join is full.
     * </p>
     *
     * @param response The LobbyIsFullResponse detected on the EventBus
     * @see LobbyIsFullResponse
     * @see #showLobbyJoinAlert(LobbyStatus)
     */
    @Subscribe
    public void onLobbyIsFullResponse(LobbyIsFullResponse response) {
        Platform.runLater(() -> showLobbyJoinAlert(LobbyStatus.FULL));
    }

    /**
     * Handles LobbyNotJoinableResponse detected on the EventBus
     *
     * <p>
     * If a {@link LobbyNotJoinableResponse} is detected on the EventBus, this method gets
     * called. It shows an alert to the user indicating that the lobby they tried to join
     * is not joinable with the specific status from the response.
     * </p>
     *
     * @param response The LobbyNotJoinableResponse detected on the EventBus
     * @see LobbyNotJoinableResponse
     * @see #showLobbyJoinAlert(LobbyStatus)
     */
    @Subscribe
    public void onLobbyNotJoinableResponse(LobbyNotJoinableResponse response) {
        Platform.runLater(() -> showLobbyJoinAlert(response.getStatus()));
    }

    /**
     * Stops and nullifies the auto-refresh timer for the lobby list
     */
    private void resetExistingTimer() {
        if (autoRefreshTimer != null) {
            autoRefreshTimer.stop();
            autoRefreshTimer = null;
        }
    }

    /**
     * Creates and starts a new auto-refresh timer for the lobby list
     */
    private void startAutoRefreshTimer() {
        if (autoRefreshTimer != null) {
            autoRefreshTimer.stop();
        }
        autoRefreshTimer = new Timeline(new KeyFrame(Duration.seconds(10), event -> lobbyService.findLobbies()));
        autoRefreshTimer.setCycleCount(Timeline.INDEFINITE);
        autoRefreshTimer.play();
    }
}