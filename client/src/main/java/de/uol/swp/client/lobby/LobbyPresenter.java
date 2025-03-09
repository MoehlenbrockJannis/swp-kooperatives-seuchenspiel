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
import de.uol.swp.common.game.GameDifficulty;
import de.uol.swp.common.game.server_message.CreateGameServerMessage;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.server_message.*;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.map.response.RetrieveOriginalGameMapTypeResponse;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.plague.response.RetrieveAllPlaguesResponse;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.Player;
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
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.greenrobot.eventbus.Subscribe;

import java.util.*;

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
    @FXML
    private Button addAIButton;
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

    private int botCounter = 0;
    private List<String> botNames = new ArrayList<>();
    private GameDifficulty selectedDifficulty;

    @FXML
    private ComboBox<GameDifficulty> difficultyComboBox;

    public void initialize(final Lobby lobby) {
        this.lobby = lobby;
        this.selectedDifficulty = GameDifficulty.getDefault();

        updateStartGameButton();
        setTitle(lobby.getName());
        initializeComboBox();
        initializeDifficultyComboBox();

        chatController.setLobby(lobby);

        userContainerEntityListController.setTitle("Mitspieler");
        userContainerEntityListController.setRightClickFunctionToListCells(this::showPlayerListCellContextMenu);
        updatePlayerList();
        disableControlsForNonOwners();
        lobbyService.getOriginalGameMapType();
        lobbyService.getPlagues();
    }

    /**
     * Method called when the {@link javafx.stage.Window} of the {@link Stage} of the {@link Lobby} or {@link Game} is closed.
     *
     * <p>
     * When the window is closed, this method calls the {@link LobbyService#leaveLobby(Lobby, Player)} method
     * to make the currently logged in user leave the lobby. This {@link LobbyPresenter} object and the associated
     * {@link GamePresenter} object are also unregistered from the {@link #eventBus}.
     * </p>
     *
     * @param event The {@link WindowEvent} that closes the {@link javafx.stage.Window}.
     * @see #clearEventBus()
     * @see LobbyService#leaveLobby(Lobby, Player)
     * @see WindowEvent
     * @since 2024-09-13
     */
    @Override
    protected void onCloseStageEvent(final WindowEvent event) {
        super.onCloseStageEvent(event);
        Player player = lobby.getPlayerForUser(loggedInUserProvider.get());
        lobbyService.leaveLobby(lobby, player);
    }

    /**
     * Initializes the combo box for selecting roles and sets up a listener to update the selected role.
     * Sends a request to populate the combo box with available roles for the current lobby.
     */
    private void initializeComboBox() {
        roleComboBox.getSelectionModel().selectedItemProperty().addListener((a, b, c) -> selectRole());
        roleComboBox.setButtonCell(createRoleComboBoxButtonCell());

        Player player = lobby.getPlayerForUser(loggedInUserProvider.get());
        if (player != null && player.getRole() != null) {
            roleComboBox.setPromptText(player.getRole().getName());
            roleComboBox.getSelectionModel().clearSelection();
        }
        roleService.sendRetrieveAllRolesRequest(lobby);
    }

    /**
     * Creates a custom button cell for the role combo box that properly displays
     * either the selected role or the prompt text when no role is selected.
     *
     * @return A ListCell configured to display the role name or prompt text
     */
    private ListCell<RoleCard> createRoleComboBoxButtonCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(RoleCard roleCard, boolean isEmpty) {
                super.updateItem(roleCard, isEmpty);
                if (isEmpty) {
                    setText(roleComboBox.getPromptText());
                } else {
                    setText(roleCard.getName());
                }
            }
        };
    }

    /**
     * Handles the message when roles are sent to the combo box. If the lobby matches,
     * updates the available roles in the combo box with the received role set.
     *
     * @param message the message containing the roles to be displayed in the combo box.
     */
    @Subscribe
    public void onRetrieveAllRolesResponse(RetrieveAllRolesResponse message) {
        if (lobby.getId() == message.getLobby().getId()) {
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
    public void onRetrieveAllAvailableRolesServerMessage(RetrieveAllAvailableRolesServerMessage message) {
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
                roleComboBox.setPromptText(roleAssignedMessage.getRoleCard().getName());
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

    /**
     * Handles the event when the add AI button is clicked in the lobby.
     * <p>
     * This method adds an AI player (bot) to the lobby if the current number of players
     * has not reached the lobby's maximum player limit. It checks whether the lobby is full,
     * and if so, displays an information alert indicating that no more players can be added.
     * Otherwise, it creates a new bot player and adds it to the lobby.
     *
     * @param event The action event triggered by clicking the "Add AI" button.
     * @since 2024-10-06
     */
    @FXML
    private void onAddAIButtonClicked(final ActionEvent event) {
        if (isLobbyFull()) {
            showMaxPlayersAlert();
            return;
        }

        Player bot = createBotPlayer();
        this.lobbyService.playerJoinLobby(this.lobby, bot);
    }

    /**
     * Disables controls for users who are not the lobby owner.
     * <p>
     * This method checks if the currently logged-in user is the owner of the lobby.
     * If the user is not the owner, it disables the "Add AI" button and the difficulty combo box.
     * </p>
     * @since 2024-09-23
     */
    private void disableControlsForNonOwners() {
        User currentUser = loggedInUserProvider.get();
        User lobbyOwner = this.lobby.getOwner();
        boolean isOwner = currentUser.equals(lobbyOwner);

        addAIButton.setDisable(!isOwner);
        difficultyComboBox.setDisable(!isOwner);
    }

    /**
     * Checks whether the lobby has reached its maximum player limit.
     * <p>
     * This method compares the current number of players in the lobby to the maximum allowed
     * number of players.
     *
     * @return true if the lobby has reached or exceeded the maximum number of players, false otherwise.
     * @since 2024-10-06
     */
    private boolean isLobbyFull() {
        return lobby.getMaxPlayers() <= lobby.getPlayers().size();
    }

    /**
     * Displays an information alert indicating that the lobby has reached the maximum number of players.
     * <p>
     * This method is called when a player attempts to join a lobby that is already full.
     * It triggers an alert dialog with a message informing the user that the maximum number
     * of players has been reached and no additional players can be added to the lobby.
     *
     * @since 2024-10-06
     */
    private void showMaxPlayersAlert() {
        showInformationAlert("Maximale Anzahl an Spielern erreicht");
    }

    /**
     * Creates a new AI player (bot) for the lobby.
     * <p>
     * It is used to add non-human players (bots) to the lobby. The bot's name
     * is generated using the {@link #generateBotName()} method to ensure uniqueness.
     *
     * @return A new instance of {@link AIPlayer} with a generated name.
     * @since 2024-10-06
     */
    private Player createBotPlayer() {
        return new AIPlayer(generateBotName());
    }

    /**
     * Generates a unique name for an AI player (bot).
     *
     * @return A unique bot name as a {@link String}.
     * @since 2024-10-06
     */
    private String generateBotName() {
        for (int i = 1; i <= botCounter; i++) {
            String potentialName = createBotName(i);
            if (!botNames.contains(potentialName)) {
                return addAndReturnBotName(potentialName);
            }
        }
        String botName = createBotName(++botCounter);
        return addAndReturnBotName(botName);
    }

    /**
     * Creates a name for a bot player based on the given bot number.
     * The name format is "Bot" followed by the specified number.
     *
     * @param botNumber The number used to create the bot's name.
     * @return The generated name for the bot.
     * @since 2024-10-09
     */
    private String createBotName(int botNumber) {
        return "Bot" + botNumber;
    }

    /**
     * Adds a bot name to the collection of bot names and returns the added name.
     * This method is useful for keeping track of AI players in the lobby.
     *
     * @param botName The name of the bot to be added to the collection.
     * @return The name of the added bot.
     * @since 2024-10-09
     */
    private String addAndReturnBotName(String botName) {
        botNames.add(botName);
        return botName;
    }


    private void executeOnlyIfMessageIsForThisLobby(final AbstractLobbyServerMessage lobbyMessage, final Runnable executable) {
        if (lobby.equals(lobbyMessage.getLobby())) {
            executable.run();
            updateStartGameButton();
            updatePlayerList();
            disableControlsForNonOwners();
        }
    }

    @Subscribe
    public void onUserJoinedLobbyMessage(final JoinUserLobbyServerMessage userJoinedLobbyMessage) {
        final Runnable executable = () -> this.lobby = userJoinedLobbyMessage.getLobby();
        executeOnlyIfMessageIsForThisLobby(userJoinedLobbyMessage, executable);
    }

    @Subscribe
    public void onPlayerJoinedLobbyMessage(final JoinPlayerLobbyServerMessage lobbyJoinPlayerServerMessage) {
        final Runnable executable = () -> this.lobby = lobbyJoinPlayerServerMessage.getLobby();
        executeOnlyIfMessageIsForThisLobby(lobbyJoinPlayerServerMessage, executable);
    }

    @Subscribe
    public void onUserLeftLobbyMessage(final LeavePlayerLobbyServerMessage userLeftLobbyMessage) {
        final Runnable executable = () -> this.lobby = userLeftLobbyMessage.getLobby();
        executeOnlyIfMessageIsForThisLobby(userLeftLobbyMessage, executable);
    }

    /**
     * Handles LobbyKickUserServerMessage events from the EventBus.
     * <p>
     * When a LobbyKickUserServerMessage is detected, this method checks if the kicked user
     * matches the currently logged-in user. If the logged-in user has been kicked, a message
     * is shown to inform them that they have been removed from the lobby, and the lobby stage
     * is closed.
     * <p>
     * The method first ensures the message pertains to the current lobby by using
     * executeOnlyIfMessageIsForThisLobby. If it does, the action is executed.
     *
     * @param kickUserServerMessage The message containing information about the user kicked
     *                              from the lobby.
     * @see KickPlayerLobbyServerMessage
     * @since 2024-09-23
     */
    @Subscribe
    public void onLobbyKickUserServerMessage(final KickPlayerLobbyServerMessage kickUserServerMessage) {
        final Runnable executable = () -> {
            this.lobby = kickUserServerMessage.getLobby();
            if (kickUserServerMessage.getPlayer().containsUser(loggedInUserProvider.get())) {
                Platform.runLater(() -> showInformationAlert("Du wurdest aus der Lobby gekickt"));
                closeStage();
            }
        };
        executeOnlyIfMessageIsForThisLobby(kickUserServerMessage, executable);
    }

    private void updateStartGameButton() {
        startGameButton.setDisable(!lobby.getOwner().equals(loggedInUserProvider.get()) || lobby.getPlayers().size() < lobby.getMinPlayers());
    }

    /**
     * Starts the game by retrieving the mapType and plague list first and creating the game afterwards in {@link LobbyPresenter#onRetrieveAllPlaguesResponse(RetrieveAllPlaguesResponse)}
     *
     * @param event The event that triggered the method
     */
    @FXML
    private void onStartGameButtonClicked(final ActionEvent event) {
        if (selectedMapType != null && plagueList != null) {
            gameService.createGame(lobby, selectedMapType, plagueList, selectedDifficulty);
        }
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
        plagueList = new ArrayList<>(response.getPlagueSet());
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

        if(gameLobby.equals(lobby)) {
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

        if (isContextMenuNotAllowed(currentUser, lobbyOwner, listCellItem)) {
            listCell.setContextMenu(null);
            return;
        }

        ContextMenu contextMenu = createContextMenu(listCellItem);
        listCell.setContextMenu(contextMenu);
    }

    /**
     * Creates a context menu for a user container entity in the lobby.
     * This context menu includes options for managing players, such as kicking a player.
     *
     * @param listCellItem The user container entity representing the player to manage.
     * @return The constructed ContextMenu with available options.
     * @since 2024-10-09
     */
    private ContextMenu createContextMenu(UserContainerEntity listCellItem) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem kickPlayer = new MenuItem("Spieler Kicken");

        kickPlayer.setOnAction(event -> handleKickPlayer(listCellItem));
        contextMenu.getItems().addAll(kickPlayer);

        return contextMenu;
    }

    /**
     * Handles the process of kicking a player from the lobby.
     * This method checks if the provided list cell item is an instance of Player.
     * If it is, it initiates the confirmation process to kick the player.
     *
     * @param listCellItem The user container entity representing the player to be kicked.
     * @since 2024-10-09
     */
    private void handleKickPlayer(UserContainerEntity listCellItem) {
        if (listCellItem instanceof Player player) {
            handleKickPlayerConfirmation(player);
        }
    }

    /**
     * Determines whether the context menu is allowed for a specific user in the lobby.
     * The context menu is not allowed if the current user is not the lobby owner
     * or if the list cell item already contains the lobby owner.
     *
     * @param currentUser   The user currently logged in and interacting with the context menu.
     * @param lobbyOwner    The user who owns the lobby.
     * @param listCellItem  The user container entity representing the item in the context menu.
     * @return true if the context menu is not allowed; false otherwise.
     * @since 2024-10-09
     */
    private boolean isContextMenuNotAllowed(User currentUser, User lobbyOwner, UserContainerEntity listCellItem) {
        return !currentUser.equals(lobbyOwner) || listCellItem.containsUser(lobbyOwner);
    }

    /**
     * Manages the confirmation process for kicking a specified player from the lobby.
     * This method displays a confirmation alert to the user. If the user confirms the action,
     * the specified player is kicked from the lobby, and an informational alert is shown
     * indicating that the player has been successfully kicked. If the user cancels the action,
     * an informational alert is displayed to inform them that the operation has been aborted.
     *
     * @param player The player to be kicked from the lobby.
     * @since 2024-10-09
     */
    private void handleKickPlayerConfirmation(Player player) {
        Alert confirmationAlert = createConfirmationKickPlayerAlert();

        confirmationAlert.showAndWait().ifPresent(response -> handleKickPlayerResponse(response, player));
    }

    /**
     * Creates a confirmation alert for kicking a player from the lobby.
     *
     * @return The configured confirmation alert.
     * @since 2024-10-09
     */
    private Alert createConfirmationKickPlayerAlert() {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Bestätigung");
        confirmationAlert.setHeaderText("Benutzer kicken");
        confirmationAlert.setContentText("Möchten Sie den Benutzer wirklich kicken?");
        return confirmationAlert;
    }

    /**
     * Handles the response from the kick player confirmation dialog.
     *
     * @param response The response from the confirmation dialog.
     * @param player The player to be kicked from the lobby.
     * @since 2024-10-09
     */
    private void handleKickPlayerResponse(ButtonType response, Player player) {
        if (response == ButtonType.OK) {
            kickPlayer(player);
            showInformationAlert("Der Benutzer wurde gekickt!");
        } else {
            showInformationAlert("Vorgang abgebrochen.");
        }
    }

    /**
     * Kicks the specified player from the lobby and removes their name if
     * they are an AI player.
     *
     * @param player The player to be kicked from the lobby.
     * @since 2024-10-09
     */
    private void kickPlayer(Player player) {
        this.lobbyService.kickPlayer(this.lobby, player);
        if (player instanceof AIPlayer) {
            removeBotName(player);
        }
    }

    /**
     * Removes a bot player name from the list of active bots in the lobby.
     *
     * @param bot The AIPlayer instance to be removed from the lobby.
     * @since 2024-09-23
     */
    private void removeBotName(Player bot) {
        if (bot instanceof AIPlayer) {
            String botName = bot.getName();
            botNames.remove(botName);

            checkAndResetBotCounter();
        }
    }

    /**
     * Checks if the bot names list is empty and resets the bot counter if necessary.
     *
     * @since 2024-10-09
     */
    private void checkAndResetBotCounter() {
        if (botNames.isEmpty()) {
            resetBotCounter();
        }
    }

    /**
     * Resets the bot counter to zero.
     *
     * @since 2024-10-09
     */
    private void resetBotCounter() {
        this.botCounter = 0;
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

    private void initializeDifficultyComboBox() {
        difficultyComboBox.setItems(FXCollections.observableArrayList(GameDifficulty.values()));
        difficultyComboBox.setValue(GameDifficulty.getDefault());
        difficultyComboBox.setOnAction(event -> handleDifficultyChange());
    }

    private void handleDifficultyChange() {
        GameDifficulty newDifficulty = difficultyComboBox.getValue();
        if (shouldUpdateDifficulty(newDifficulty)) {
            selectedDifficulty = newDifficulty;
            lobbyService.updateDifficulty(lobby, newDifficulty);
        }
    }

    private boolean shouldUpdateDifficulty(GameDifficulty newDifficulty) {
        return newDifficulty != null && !newDifficulty.equals(selectedDifficulty);
    }

    /**
     * Handles the server message when difficulty is updated in the lobby.
     * Updates the UI to reflect the new difficulty setting for all clients.
     *
     * @param message the message containing the updated difficulty information
     * @since 2025-01-28
     */
    @Subscribe
    public void onDifficultyUpdateServerMessage(DifficultyUpdateServerMessage message) {
        if (lobby.equals(message.getLobby())) {
            GameDifficulty newDifficulty = message.getDifficulty();
            updateDifficultyComboBox(newDifficulty);
        }
    }

    /**
     * Updates the difficulty combo box with the new difficulty setting.
     * Ensures the update happens on the JavaFX Application Thread.
     *
     * @param newDifficulty the new difficulty setting to be displayed
     */
    private void updateDifficultyComboBox(GameDifficulty newDifficulty) {
        Platform.runLater(() -> {
            if (shouldUpdateDifficulty(newDifficulty)) {
                difficultyComboBox.setValue(newDifficulty);
                selectedDifficulty = newDifficulty;
            }
        });
    }
}