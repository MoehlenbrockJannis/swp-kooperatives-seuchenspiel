package de.uol.swp.client.lobby;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.lobby.events.ShowLobbyCreateViewEvent;
import de.uol.swp.client.lobby.events.ShowLobbyViewEvent;
import de.uol.swp.client.main.events.ShowMainMenuEvent;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.response.LobbyFindLobbiesResponse;
import de.uol.swp.common.lobby.response.LobbyJoinUserResponse;
import de.uol.swp.common.user.User;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import lombok.NoArgsConstructor;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Set;

/**
 * Manages the lobbyOverview window
 *
 * @author Tom Weelborg
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2024-08-23
 */
@NoArgsConstructor
public class LobbyOverviewPresenter extends AbstractPresenter {

    public static final String FXML = "/fxml/LobbyOverviewView.fxml";

    @Inject
    private LoggedInUserProvider loggedInUserProvider;
    @Inject
    private LobbyService lobbyService;
    private ObservableList<Lobby> lobbies;

    @FXML
    private TableView<Lobby> lobbiesTable;

    @FXML
    private TableColumn<Lobby, String> nameColumn;
    @FXML
    private TableColumn<Lobby, String> ownerColumn;
    @FXML
    private TableColumn<Lobby, String> membersColumn;

    /**
     * Called on initialization of LobbyOverviewView FXML file
     *
     * <p>
     *     Method sets the column widths to 0.5 for the {@link #nameColumn}, 0.3 for the {@link #ownerColumn} and 0.2 for the {@link #membersColumn}.
     *     It also assigns a ValueFactory to each column.
     *     After that it loads all lobbies using the {@link #lobbyService}.
     * </p>
     *
     * @since 2024-08-24
     */
    @FXML
    public void initialize() {
        setColumnWidths(0.5, 0.3, 0.2);

        initializeNameColumnValueFactory();
        initializeOwnerColumnValueFactory();
        initializeMembersColumnValueFactory();

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
     * @since 2024-08-24
     */
    public void setColumnWidths(final double nameColumnWidth, final double ownerColumnWidth, final double membersColumnWidth) {
        nameColumn.prefWidthProperty().bind(lobbiesTable.widthProperty().multiply(nameColumnWidth));
        ownerColumn.prefWidthProperty().bind(lobbiesTable.widthProperty().multiply(ownerColumnWidth));
        membersColumn.prefWidthProperty().bind(lobbiesTable.widthProperty().multiply(membersColumnWidth));
    }

    /**
     * Sets a CellValueFactory to the {@link #nameColumn} to display the name of each lobby.
     *
     * @see de.uol.swp.common.lobby.Lobby
     * @since 2024-08-24
     */
    private void initializeNameColumnValueFactory() {
        nameColumn.setCellValueFactory(lobby -> new SimpleStringProperty(lobby.getValue().getName()));
    }

    /**
     * Sets a CellValueFactory to the {@link #ownerColumn} to display the username of the owner of each lobby.
     *
     * @see de.uol.swp.common.lobby.Lobby
     * @see de.uol.swp.common.user.User
     * @since 2024-08-24
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
     * @since 2024-08-24
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
                    final Set<User> users = lobby.getUsers();
                    if (users != null) {
                        setText(String.valueOf(users.size()));

                        final Tooltip tooltip = new Tooltip(users.stream()
                                .map(User::getUsername)
                                .reduce((a, b) -> a + "\n" + b)
                                .orElse(""));
                        setTooltip(tooltip);
                    }
                }
            }
        });
    }

    /**
     * Adds a RowFactory to the {@link #lobbiesTable} with every row having an EventListener calling {@link #onLobbyRowClicked(Lobby)} with the corresponding lobby.
     *
     * @see de.uol.swp.common.lobby.Lobby
     * @see de.uol.swp.client.lobby.LobbyOverviewPresenter#onLobbyRowClicked(Lobby)
     * @since 2024-08-25
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
     * If a {@link LobbyFindLobbiesResponse} is detected on the EventBus, this method is called.
     * It calls {@link #setLobbyList(List)} with the list of all lobbies in the lobbyFindLobbiesResponse.
     * </p>
     *
     * @param lobbyFindLobbiesResponse The LobbyFindLobbiesResponse found on the EventBus
     * @see de.uol.swp.common.lobby.response.LobbyFindLobbiesResponse
     * @see #setLobbyList(List)
     * @since 2024-08-24
     */
    @Subscribe
    public void onLobbyFindLobbiesResponse(final LobbyFindLobbiesResponse lobbyFindLobbiesResponse) {
        setLobbyList(lobbyFindLobbiesResponse.getLobbies());
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
     * @since 2024-08-24
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
     * @param event The ActionEvent generated by pressing the back to menu button
     * @see de.uol.swp.client.main.events.ShowMainMenuEvent
     * @since 2024-08-24
     */
    @FXML
    private void onBackToMainMenuButtonPressed(final ActionEvent event) {
        final ShowMainMenuEvent showMainMenuEvent = new ShowMainMenuEvent(loggedInUserProvider.get());
        eventBus.post(showMainMenuEvent);
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
     * @since 2024-08-27
     */
    @FXML
    private void onRefreshButtonPressed(final ActionEvent event) {
        lobbyService.findLobbies();
    }

    /**
     * Method called when the create lobby button is pressed
     *
     * @param event The ActionEvent generated by pressing the create lobby button
     * @since 2024-08-27
     */
    @FXML
    private void onCreateLobbyButtonPressed(final ActionEvent event) {
        final ShowLobbyCreateViewEvent showLobbyCreateViewEvent = new ShowLobbyCreateViewEvent();
        eventBus.post(showLobbyCreateViewEvent);
    }

    /**
     * Callback for the row of the {@link #lobbiesTable} that was clicked
     *
     * <p>
     *     Adds the currently logged in user to the lobby associated with the clicked row by calling a {@link #lobbyService} method.
     * </p>
     *
     * @param lobby Lobby of the clicked row
     * @see LobbyService#joinLobby(String, User)
     */
    private void onLobbyRowClicked(final Lobby lobby) {
        lobbyService.joinLobby(lobby.getName(), loggedInUserProvider.get());
    }

    /**
     * Handles LobbyJoinUserResponse detected on the EventBus
     *
     * <p>
     * If a {@link LobbyJoinUserResponse} is detected on the EventBus, this method gets
     * called. It removes the joined lobby from {@link #lobbies} and posts a {@link ShowLobbyViewEvent} to the EventBus.
     * </p>
     *
     * @param lobbyJoinUserResponse The LobbyJoinUserResponse detected on the EventBus
     * @see de.uol.swp.common.lobby.response.LobbyJoinUserResponse
     * @since 2024-08-29
     */
    @Subscribe
    public void onLobbyJoinUserResponse(final LobbyJoinUserResponse lobbyJoinUserResponse) {
        setLobbyList(lobbies);

        final ShowLobbyViewEvent showLobbyViewEvent = new ShowLobbyViewEvent(lobbyJoinUserResponse.getLobby());
        eventBus.post(showLobbyViewEvent);
    }
}