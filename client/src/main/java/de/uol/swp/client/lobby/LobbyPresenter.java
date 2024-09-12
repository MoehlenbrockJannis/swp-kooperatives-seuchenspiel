package de.uol.swp.client.lobby;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.chat.ChatPresenter;
import de.uol.swp.client.game.GamePresenter;
import de.uol.swp.client.game.GameService;
import de.uol.swp.client.role.RoleService;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.client.user.UserContainerEntityListPresenter;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.response.GameCreatedResponse;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyStatus;
import de.uol.swp.common.lobby.message.AbstractLobbyMessage;
import de.uol.swp.common.lobby.message.UserJoinedLobbyMessage;
import de.uol.swp.common.lobby.message.UserLeftLobbyMessage;
import de.uol.swp.common.role.RoleCard;
import de.uol.swp.common.role.message.RolesAvailableMessage;
import de.uol.swp.common.role.response.RoleAssignedMessage;
import de.uol.swp.common.role.response.RolesToComboBoxMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserContainerEntity;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
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

    public static final String FXML = "/fxml/LobbyView.fxml";

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
    private Stage stage;
    private Lobby lobby;
    private ObservableList<RoleCard> availableRoles;
    @Inject
    private RoleService roleService;
    private RoleCard selectedRole;
    @FXML
    private ChatPresenter chatController;
    @FXML
    private UserContainerEntityListPresenter userContainerEntityListController;

    static final String STYLE_SHEET = "css/swp.css";

    public void initialize(final Stage stage, final Lobby lobby) {
        this.stage = stage;
        this.lobby = lobby;

        updateStartGameButton();
        setTitle(lobby.getName());
        stage.getScene().getWindow().setOnCloseRequest(event -> lobbyService.leaveLobby(lobby.getName(), loggedInUserProvider.get()));
        stage.show();
        initializeComboBox();

        chatController.setLobby(lobby);

        userContainerEntityListController.setTitle("Mitspieler");
        updatePlayerList();
    }

    /**
     * Initializes the combo box for selecting roles and sets up a listener to update the selected role.
     * Sends a request to populate the combo box with available roles for the current lobby.
     */
    private void initializeComboBox() {
        roleComboBox.getSelectionModel().selectedItemProperty().addListener((a, b, c) -> updateRole());
        roleService.sendRolesToComboBoxRequest(lobby);
    }

    /**
     * Handles the message when roles are sent to the combo box. If the lobby matches,
     * updates the available roles in the combo box with the received role set.
     *
     * @param message the message containing the roles to be displayed in the combo box.
     */
    @Subscribe
    public void onRolesSendToComboBox(RolesToComboBoxMessage message) {
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
     * The method is a placeholder for future role filtering logic.
     *
     * @param message the message containing information about available roles.
     */
    @Subscribe
    public void onRolesAvailableMessage(RolesAvailableMessage message) {
        Platform.runLater(() -> {
            //TODO Remove unavailable roles or disable them. Implementation available in RoleManagement.java.
        });
    }

    /**
     * Updates the selected role and sends a request to assign the selected role
     * to the logged-in user in the current lobby.
     */
    @FXML
    private void updateRole() {
        selectedRole = roleComboBox.getValue();
        if (selectedRole != null) {
            Lobby lobbyName = lobby;
            User user = loggedInUserProvider.get();
            roleService.sendRoleAssignmentRequest(lobbyName, user, selectedRole);
        }
    }

    /**
     * Updates the role {@link ComboBox} based on the {@link RoleAssignedMessage}.
     *
     * If a role is assigned, sets the {@link ComboBox} prompt text. If no role is assigned, clears the selection.
     *
     * @param roleAssignedMessage contains role assignment details.
     */
    @Subscribe
    public void onRoleAssignedMessage(RoleAssignedMessage roleAssignedMessage) {
        Platform.runLater(() -> {
            if(roleAssignedMessage.isRoleAssigned()) {
                roleComboBox.setPromptText(roleAssignedMessage.getRoleCardName());
            } else {
                roleComboBox.setValue(null);
            }
        });
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

    private void executeOnlyIfMessageIsForThisLobby(final AbstractLobbyMessage lobbyMessage, final Runnable executable) {
        if (lobby.getName().equals(lobbyMessage.getLobbyName())) {
            executable.run();
        }
    }

    @Subscribe
    public void onUserJoinedLobbyMessage(final UserJoinedLobbyMessage userJoinedLobbyMessage) {
        final Runnable executable = () -> lobby.joinUser(userJoinedLobbyMessage.getUser());
        executeOnlyIfMessageIsForThisLobby(userJoinedLobbyMessage, executable);
        updateStartGameButton();
        updatePlayerList();
    }

    @Subscribe
    public void onUserLeftLobbyMessage(final UserLeftLobbyMessage userLeftLobbyMessage) {
        final Runnable executable = () -> lobby.leaveUser(userLeftLobbyMessage.getUser());
        executeOnlyIfMessageIsForThisLobby(userLeftLobbyMessage, executable);
        updateStartGameButton();
        updatePlayerList();
    }

    private void updateStartGameButton() {
        startGameButton.setDisable(!lobby.getOwner().equals(loggedInUserProvider.get()) || lobby.getUsers().size() < 2);
    }

    /**
     * Starts the game
     *
     * @param event The event that triggered the method
     */
    @FXML
    private void onStartGameButtonClicked(final ActionEvent event) {
        //TODO: add configuration as import parameters in createGame method
        lobbyService.updateLobbyStatus(lobby, LobbyStatus.RUNNING);
        gameService.createGame(lobby);
    }

    /**
     * Handles the response of the game creation
     *
     * @param event The event that triggered the method
     */
    @Subscribe
    public void onGameCreatedResponse(GameCreatedResponse event) {
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
     */
    private void loadGameScene(Game game) {
        Platform.runLater(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/GameBoardView.fxml"));
                final Parent gameView = fxmlLoader.load();
                final GamePresenter gamePresenter = fxmlLoader.getController();

                final Scene gameScene = new Scene(gameView);
                gameScene.getStylesheets().add(STYLE_SHEET);

                stage.setScene(gameScene);
                stage.getScene().getWindow().setOnCloseRequest(event -> lobbyService.leaveLobby(lobby.getName(), loggedInUserProvider.get()));
                stage.setTitle("Game: " + game.getLobby().getName());

                gamePresenter.initialize(stage, game);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Updates the player list by setting it to the users of the associated {@link #lobby}
     *
     * @see Lobby#getUsers()
     * @see UserContainerEntityListPresenter#setList(Collection)
     */
    private void updatePlayerList() {
        userContainerEntityListController.highlightUser(lobby.getOwner());

        final Set<UserContainerEntity> userContainerEntities = new HashSet<>(lobby.getUsers());
        userContainerEntityListController.setList(userContainerEntities);
    }
}