package de.uol.swp.client.lobby;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.chat.ChatPresenter;
import de.uol.swp.client.game.GameService;
import de.uol.swp.client.game.GamePresenter;
import de.uol.swp.client.role.RoleService;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.client.user.UserContainerEntityListPresenter;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.server_message.CreateGameServerMessage;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyStatus;
import de.uol.swp.common.lobby.server_message.*;
import de.uol.swp.common.player.UserPlayer;
import de.uol.swp.common.lobby.server_message.AbstractLobbyServerMessage;
import de.uol.swp.common.lobby.server_message.LobbyJoinUserServerMessage;
import de.uol.swp.common.lobby.server_message.LobbyLeaveUserServerMessage;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.map.response.RetrieveOriginalGameMapTypeResponse;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.plague.response.RetrieveAllPlaguesResponse;
import de.uol.swp.common.role.RoleCard;
import de.uol.swp.common.role.response.RetrieveAllRolesResponse;
import de.uol.swp.common.role.response.RoleAssignmentResponse;
import de.uol.swp.common.role.server_message.RetrieveAllAvailableRolesServerMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserContainerEntity;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.greenrobot.eventbus.Subscribe;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages the lobby window
 *
 * @author Niklas Wrobel
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2024-08-28
 */
@NoArgsConstructor
@Getter
@Setter
public class LobbyPresenter extends AbstractPresenter {
    @FXML
    private Button startGameButton;

    @FXML
    private Label lobbyNameLabel;
    @Inject
    private LobbyService lobbyService;

    @Inject
    private GameService gameService;

    @Inject
    private LoggedInUserProvider loggedInUserProvider;
    @FXML
    private ComboBox<RoleCard> roleComboBox;
    private Lobby lobby;
    private ObservableList<RoleCard> availableRoles;
    @Inject
    private RoleService roleService;
    private RoleCard selectedRole;
    @FXML
    private ChatPresenter chatController;
    @FXML
    private UserContainerEntityListPresenter userContainerEntityListController;

    private GamePresenter gamePresenter;

    private MapType selectedMapType;
    private List<Plague> plagueList;

    public void initialize(final Lobby lobby) {
        this.lobby = lobby;

        updateStartGameButton();
        setTitle(lobby.getName());
        initializeComboBox();

        chatController.setLobby(lobby);

        userContainerEntityListController.setTitle("Mitspieler");
        userContainerEntityListController.setRightClickFunctionToListCells(this::showPlayerListCellContextMenu);
        updatePlayerList();
    }

    /**
     * Method called when the {@link javafx.stage.Window} of the {@link Stage} of the {@link Lobby} or {@link Game} is closed
     *
     * <p>
     *     When the window is closed, this method calls the {@link #lobbyService} to make the currently logged in user leave the lobby.
     *     This {@link LobbyPresenter} object and the associated {@link GamePresenter} object are also unregistered from the {@link #eventBus}.
     * </p>
     *
     * @param event The {@link WindowEvent} that closes the {@link javafx.stage.Window}
     * @see #clearEventBus()
     * @see LobbyService#leaveLobby(Lobby, User)
     * @see WindowEvent
     * @since 2024-09-13
     */
    @Override
    protected void onCloseStageEvent(final WindowEvent event) {
        super.onCloseStageEvent(event);
        lobbyService.leaveLobby(lobby, loggedInUserProvider.get());
    }

    /**
     * Initializes the combo box for selecting roles and sets up a listener to update the selected role.
     * Sends a request to populate the combo box with available roles for the current lobby.
     */
    private void initializeComboBox() {
        roleComboBox.getSelectionModel().selectedItemProperty().addListener((a, b, c) -> selectRole());
        roleService.sendRolesToComboBoxRequest(lobby);
    }

    /**
     * Handles the message when roles are sent to the combo box. If the lobby matches,
     * updates the available roles in the combo box with the received role set.
     *
     * @param message the message containing the roles to be displayed in the combo box.
     */
    @Subscribe
    public void onRetrieveAllRolesResponse(RetrieveAllRolesResponse message) {
        if (lobby.getName().equals(message.getLobby().getName())) {
            Platform.runLater(() -> {
                Set<RoleCard> roles = message.getRoleCardSet();
                availableRoles = FXCollections.observableArrayList(roles);
                roleComboBox.setItems(availableRoles);
            });
        }
    }

    /**
     * Handles the message regarding available roles. Updates the UI based on role availability.
     *
     * @param message the message containing information about available roles.
     */
    @Subscribe
    public void onRolesAvailableServerMessage(RetrieveAllAvailableRolesServerMessage message) {
        Platform.runLater(() -> {
            Set<RoleCard> roles = message.getRoleCards();
            availableRoles = FXCollections.observableArrayList(roles);
            roleComboBox.setItems(availableRoles);

            final Runnable executable = () -> this.lobby = message.getLobby();
            executeOnlyIfMessageIsForThisLobby(message, executable);
        });
    }

    /**
     * Updates the selected role and sends a request to assign the selected role
     * to the logged-in user in the current lobby.
     */
    private void selectRole() {
        selectedRole = roleComboBox.getValue();
        if (selectedRole != null) {
            User user = loggedInUserProvider.get();
            roleService.sendRoleAssignmentRequest(lobby, user, selectedRole);
        }
    }

    /**
     * Updates the role {@link ComboBox} based on the {@link RoleAssignmentResponse}.
     * If a role is assigned, sets the {@link ComboBox} prompt text. If no role is assigned, clears the selection.
     *
     * @param roleAssignedMessage contains role assignment details.
     */
    @Subscribe
    public void onRoleAssignedResponse(RoleAssignmentResponse roleAssignedMessage) {
        Platform.runLater(() -> {
            if (roleAssignedMessage.isRoleAssigned()) {
                roleComboBox.getSelectionModel().clearSelection();
                roleComboBox.setPromptText(roleAssignedMessage.getRoleCard().getName()); //TODO Der Titel der Combpbx bleibt noch leer.
            }
        });
    }


    private void setTitle(final String lobbyName) {
        Platform.runLater(() -> {
            final String title = "Lobby: " + lobbyName;
            stage.setTitle(title);
            lobbyNameLabel.setText(title);
        });
    }

    @FXML
    private void onLeaveLobbyButtonClicked(final ActionEvent event) {
        closeStage();
    }

    private void executeOnlyIfMessageIsForThisLobby(final AbstractLobbyServerMessage lobbyMessage, final Runnable executable) {
        if (lobby.equals(lobbyMessage.getLobby())) {
            executable.run();
            updateStartGameButton();
            updatePlayerList();
        }
    }

    @Subscribe
    public void onUserJoinedLobbyMessage(final LobbyJoinUserServerMessage userJoinedLobbyMessage) {
        final Runnable executable = () -> this.lobby = userJoinedLobbyMessage.getLobby();
        executeOnlyIfMessageIsForThisLobby(userJoinedLobbyMessage, executable);
    }

    @Subscribe
    public void onUserLeftLobbyMessage(final LobbyLeaveUserServerMessage userLeftLobbyMessage) {
        final Runnable executable = () -> this.lobby = userLeftLobbyMessage.getLobby();
        executeOnlyIfMessageIsForThisLobby(userLeftLobbyMessage, executable);
    }

    /**
     * Handles LobbyKickUserServerMessage events from the EventBus.
     *
     * When a LobbyKickUserServerMessage is detected, this method checks if the kicked user
     * matches the currently logged-in user. If the logged-in user has been kicked, a message
     * is shown to inform them that they have been removed from the lobby, and the lobby stage
     * is closed.
     *
     * The method first ensures the message pertains to the current lobby by using
     * executeOnlyIfMessageIsForThisLobby. If it does, the action is executed.
     *
     * @param kickUserServerMessage The message containing information about the user kicked
     *                              from the lobby.
     * @see LobbyKickUserServerMessage
     * @since 2024-09-23
     */
    @Subscribe
    public void onLobbyKickUserServerMessage(final LobbyKickUserServerMessage kickUserServerMessage) {
        final Runnable executable = () -> {
            this.lobby = kickUserServerMessage.getLobby();
            if (kickUserServerMessage.getUser().equals(loggedInUserProvider.get())) {
                Platform.runLater(() -> showInformationAlert("Du wurdest aus der Lobby gekickt"));
                closeStage();
            }
        };
        executeOnlyIfMessageIsForThisLobby(kickUserServerMessage, executable);
    }

    private void updateStartGameButton() {
        //TODO: Chance 1 before merge to lobby.getMinPlayers()
        startGameButton.setDisable(!lobby.getOwner().equals(loggedInUserProvider.get()) || lobby.getUsers().size() < 1);
    }

    /**
     * Starts the game by retrieving the mapType and plague list first and creating the game afterwards in {@link LobbyPresenter#onRetrieveAllPlaguesResponse(RetrieveAllPlaguesResponse)}
     *
     * @param event The event that triggered the method
     */
    @FXML
    private void onStartGameButtonClicked(final ActionEvent event) {
        lobbyService.updateLobbyStatus(lobby, LobbyStatus.RUNNING);
        lobbyService.getOriginalGameMapType();
    }

    /**
     * Handles the response with the mapType of the original game and creates a request to get a list of all plagues.
     * After the plague list has been received, the game will be created in {@link LobbyPresenter#onRetrieveAllPlaguesResponse(RetrieveAllPlaguesResponse)}
     *
     * @param response The response with the mapType based on the original game
     * @see RetrieveOriginalGameMapTypeResponse
     * @author David Scheffler
     * @since 2024-09-23
     */
    @Subscribe
    public void onRetrieveOriginalGameMapTypeResponse(RetrieveOriginalGameMapTypeResponse response) {
        selectedMapType= response.getMapType();
        lobbyService.getPlagues();
    }

    /**
     * Handles the response with the plague list and creates a new game
     *
     * @param response The response with the plague list
     * @see RetrieveOriginalGameMapTypeResponse
     * @author David Scheffler
     * @since 2024-09-23
     */
    @Subscribe
    public void onRetrieveAllPlaguesResponse(RetrieveAllPlaguesResponse response) {
        plagueList = response.getPlagueSet().stream().toList();

        if (selectedMapType != null) {
            gameService.createGame(lobby, selectedMapType, plagueList);
        }
    }

    /**
     * Handles the response of the game creation
     *
     * @param event The event that triggered the method
     */
    @Subscribe
    public void onGameCreatedResponse(CreateGameServerMessage event) {
        final Game game = event.getGame();
        final Lobby gameLobby = game.getLobby();

        final String gameLobbyName = gameLobby.getName();
        final String currentLobbyName = lobby.getName();

        if(gameLobbyName.equals(currentLobbyName)) {
            loadGameScene(game);
        }
    }

    /**
     * Loads the game scene
     *
     * @param game The game to be loaded
     * @implNote Must be called on FX application thread for {@link javafx.scene.web.WebView}
     * @see AbstractPresenter#loadFXMLPresenter(Class) 
     */
    private void loadGameScene(Game game) {
        Platform.runLater(() -> {
            try {
                gamePresenter = AbstractPresenter.loadFXMLPresenter(GamePresenter.class);
                associatedPresenters.add(gamePresenter);
                gamePresenter.initialize(stage, game);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Updates the player list by setting it to the players of the associated {@link #lobby}
     *
     * @see Lobby#getPlayers()
     * @see UserContainerEntityListPresenter#setList(Collection)
     */
    private void updatePlayerList() {
        userContainerEntityListController.highlightUser(lobby.getOwner());

        final Set<UserContainerEntity> userContainerEntities = new HashSet<>(lobby.getPlayers());
        userContainerEntityListController.setList(userContainerEntities);
    }

    /**
     * Displays a context menu for a user list cell in the lobby, allowing the lobby owner to kick a player.
     * This method checks if the currently logged-in user is the owner of the lobby. If not, it removes any
     * existing context menu from the list cell. If the user is the owner, it creates a context menu with
     * an option to kick a player. When the "Spieler Kicken" menu item is selected, it calls the kickUser
     * method for the selected user player.
     *
     * @param listCell The list cell for which the context menu is being created.
     * @since 2024-09-23
     */
    private void showPlayerListCellContextMenu(final ListCell<UserContainerEntity> listCell) {
        User currentUser = loggedInUserProvider.get();
        User lobbyOwner = this.lobby.getOwner();
        UserContainerEntity listCellItem = listCell.getItem();

        if (!currentUser.equals(lobbyOwner) || listCellItem.containsUser(lobbyOwner)) {
            listCell.setContextMenu(null);
            return;
        }
        ContextMenu contextMenu = new ContextMenu();
        MenuItem kickPlayer = new MenuItem("Spieler Kicken");

        contextMenu.getItems().addAll(kickPlayer);

        kickPlayer.setOnAction(event -> {
            if (listCellItem instanceof UserPlayer userPlayer) {
                kickUser(userPlayer.getUser());
            }
        });

        listCell.setContextMenu(contextMenu);
    }

    /**
     * Kicks a specified user from the lobby if they are not the lobby owner.
     * This method checks if the provided user is the owner of the lobby.
     * If the user is not the owner, it displays a confirmation alert
     * to proceed with kicking the user and logs a message indicating
     * the user has been removed from the lobby. If the user is the owner,
     * it logs a message indicating that the owner cannot kick themselves.
     *
     * @param user The user to be kicked from the lobby.
     * @since 2024-09-23
     */
    private void kickUser(User user) {
        if (!user.equals(this.lobby.getOwner())) {
            showConfirmationAlertForKickUser(user);
        }
    }

    /**
     * Displays a confirmation alert to kick a specified user from the lobby.
     * This method creates a confirmation alert.
     * If the user confirms, it shows an informational alert indicating
     * that the user has been kicked and proceeds to call the kickUser method
     * of the lobby service. If the user cancels, it shows an alert indicating
     * that the operation has been aborted.
     *
     * @param user The user to be kicked from the lobby.
     * @since 2024-09-23
     */
    private void showConfirmationAlertForKickUser(User user) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Bestätigung");
        confirmationAlert.setHeaderText("Benutzer kicken");
        confirmationAlert.setContentText("Möchten Sie den Benutzer wirklich kicken?");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                showInformationAlert("Der Benutzer wurde gekickt!");
                this.lobbyService.kickUser(this.lobby, user);
            } else {
                showInformationAlert("Vorgang abgebrochen.");
            }
        });
    }

    /**
     * Displays an informational alert dialog with the specified message.
     * This method creates an alert with a default title.
     * The alert has no header text and displays the provided message as the content.
     * It waits for the user to close the alert before returning control to the calling method.
     *
     * @param message The message to be displayed in the alert dialog.
     * @since 2024-09-23
     */
    private void showInformationAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText("Hinweis");
        alert.setContentText(message);
        alert.showAndWait();
    }

}