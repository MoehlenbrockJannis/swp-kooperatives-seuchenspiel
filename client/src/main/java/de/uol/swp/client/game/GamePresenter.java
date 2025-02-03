package de.uol.swp.client.game;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.action.ActionService;
import de.uol.swp.client.action.ShareKnowledgeActionPresenter;
import de.uol.swp.client.approvable.ApprovableService;
import de.uol.swp.client.card.InfectionCardsOverviewPresenter;
import de.uol.swp.client.card.PlayerCardsOverviewPresenter;
import de.uol.swp.client.chat.ChatPresenter;
import de.uol.swp.client.lobby.LobbyService;
import de.uol.swp.client.player.PlayerMarker;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.common.action.Action;
import de.uol.swp.common.action.advanced.build_research_laboratory.BuildResearchLaboratoryAction;
import de.uol.swp.common.action.advanced.build_research_laboratory.ReducedCostBuildResearchLaboratoryAction;
import de.uol.swp.common.action.advanced.transfer_card.ReceiveCardAction;
import de.uol.swp.common.action.advanced.transfer_card.SendCardAction;
import de.uol.swp.common.action.simple.WaiveAction;
import de.uol.swp.common.approvable.Approvable;
import de.uol.swp.common.approvable.server_message.ApprovableServerMessage;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.server_message.RetrieveUpdatedGameServerMessage;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.turn.PlayerTurn;
import de.uol.swp.common.player.turn.request.EndPlayerTurnRequest;
import de.uol.swp.common.user.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Pair;
import lombok.Getter;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Manages the game board window
 */
public class GamePresenter extends AbstractPresenter {
    public static final String GAME_FXML_FOLDER_PATH = "game/";

    @Getter
    private Game game;

    @FXML
    private Pane settingsPane;

    @FXML
    private ImageView settingsIcon;

    @FXML
    private ContextMenu settingsContextMenu;

    @FXML
    private MenuItem instructionsMenuItem;

    @FXML
    private MenuItem settingsMenuItem;

    @FXML
    private MenuItem leaveGameMenuItem;

    @FXML
    private GridPane playerContainer;

    @FXML
    private GridPane ownPlayerContainer;
    @Inject
    private LobbyService lobbyService;
    @Inject
    private ApprovableService approvableService;
    @Inject
    private ActionService actionService;

    @Inject
    private LoggedInUserProvider loggedInUserProvider;

    @FXML
    private Pane playerCardStackPane;


    @FXML
    private Pane infectionCardStackPane;

    @FXML
    private GameMapPresenter gameMapController;
    @Inject
    private PlayerCardsOverviewPresenter playerCardsOverviewPresenter;
    @Inject
    private InfectionCardsOverviewPresenter infectionCardsOverviewPresenter;

    @FXML
    private Button topRightChatToggleButton;

    @FXML
    private Button bottomRightChatToggleButton;

    @FXML
    private Pane chatComponent;

    @FXML
    private StackPane chatStackPane;

    @FXML
    private AnchorPane chatAnchorPane;

    @FXML
    private ChatPresenter chatComponentController;

    @FXML
    private Button shareKnowledgeActionButton;

    private boolean isChatVisible = true;

    private List<PlayerPanePresenter> playerPanePresenterList;

    @FXML
    private Button researchLaboratoryButton;
    @FXML
    private Button waiveActionButton;

    /**
     * <p>
     *     Return {@value #DEFAULT_FXML_FOLDER_PATH}+{@value #GAME_FXML_FOLDER_PATH}
     * </p>
     *
     * {@inheritDoc}
     */
    @Override
    public String getFXMLFolderPath() {
        return DEFAULT_FXML_FOLDER_PATH + GAME_FXML_FOLDER_PATH;
    }

    /**
     * Initializes the game board window
     *
     * @param stage The stage to display the game board window
     * @param game  The game to be displayed
     */
    public void initialize(final Stage stage, final Game game)  {
        setStage(stage, false);
        this.game = game;
        final Supplier<Game> gameSupplier = () -> getGame();

        stage.setTitle("Game: " + game.getLobby().getName());
        stage.show();

        this.playerCardStackPane.getChildren().add(this.playerCardsOverviewPresenter.getScene().getRoot());
        this.playerCardsOverviewPresenter.initialize(
                gameSupplier,
                Game::getPlayerDrawStack,
                Game::getPlayerDiscardStack,
                this.playerCardStackPane
        );
        this.playerCardsOverviewPresenter.setWindow(stage);
        this.associatedPresenters.add(playerCardsOverviewPresenter);

        this.infectionCardStackPane.getChildren().add(this.infectionCardsOverviewPresenter.getScene().getRoot());
        this.infectionCardsOverviewPresenter.initialize(
                gameSupplier,
                Game::getInfectionDrawStack,
                Game::getInfectionDiscardStack,
                this.infectionCardStackPane
        );
        this.infectionCardsOverviewPresenter.setWindow(stage);
        this.associatedPresenters.add(this.infectionCardsOverviewPresenter);

        settingsIcon.fitWidthProperty().bind(settingsPane.widthProperty());
        settingsIcon.fitHeightProperty().bind(settingsPane.heightProperty());

        settingsPane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                settingsContextMenu.show(settingsPane, event.getScreenX(), event.getScreenY());
            }
        });
        playerPanePresenterList = new ArrayList<>();
        addAllPlayers();
        gameMapController.initialize(game);
        initializeMenuItems();
        chatComponentController.setLobby(game.getLobby());
        initializeChat();
        updateShareKnowledgeActionButton();
        updateResearchLaboratoryButtonState();
        initializeResearchLaboratoryButton();
        updateWaiveButtonPressed();
    }

    /**
     * Handles the reception of an updated game message from the server.
     * Updates the current game state and checks if the current player's turn is over.
     * If the turn is over, sends a request to end the player's turn.
     *
     * @param message the message containing the updated game state
     */
    @Subscribe
    public void onReceiveUpdatedGameMessage(RetrieveUpdatedGameServerMessage message) {
        Runnable executable = () -> this.game = message.getGame();
        executeIfTheUpdatedGameMessageRetrieves(message, executable);

        Lobby currentLobby = this.game.getLobby();
        User currentLoggedInUser = loggedInUserProvider.get();
        Player currentPlayerForUser = currentLobby.getPlayerForUser(currentLoggedInUser);

        Player currentPlayer = this.game.getCurrentPlayer();

        if (isTurnOver(this.game)) {
            EndPlayerTurnRequest endTurnMessage = new EndPlayerTurnRequest(game);
            eventBus.post(endTurnMessage);
            updateResearchLaboratoryButtonState();
        }

        initializeResearchLaboratoryButton();
        updateWaiveButtonPressed();
    }

    /**
     * Checks if the current turn is over
     * @param game The game to check
     * @return True if the turn is over, false otherwise
     */
    private boolean isTurnOver(Game game) {
       return this.game.getCurrentTurn().isOver();
    }

    @Subscribe
    public void onApprovableServerMessage(final ApprovableServerMessage approvableServerMessage) {
        final Approvable approvable = approvableServerMessage.getApprovable();

        if (approvable.getGame().getId() != this.game.getId()) {
            return;
        }

        switch (approvableServerMessage.getStatus()) {
            case APPROVED:
                createApprovableInfoAlert("Aktion angenommen", approvable, approvable.getApprovedMessage());
                if (approvable instanceof Action action && action.getExecutingPlayer().containsUser(loggedInUserProvider.get())) {
                    actionService.sendAction(action.getGame(), action);
                }
                break;
            case REJECTED:
                createApprovableInfoAlert("Aktion abgelehnt", approvable, approvable.getRejectedMessage());
                break;
            case OUTBOUND:
                createApprovableAlertIfLoggedInUserControlsApprovingPlayer(approvable);
                break;
        }
    }

    private void createApprovableInfoAlert(final String title, final Approvable approvable, final String contentText) {
        Platform.runLater(() -> {
            final Alert alert = createAlert(Alert.AlertType.INFORMATION, title, approvable.toString(), contentText);
            alert.showAndWait();
        });
    }

    private void createApprovableAlertIfLoggedInUserControlsApprovingPlayer(final Approvable approvable) {
        if (approvable.getApprovingPlayer().containsUser(loggedInUserProvider.get())) {
            createApprovableAlert(approvable);
        }
    }

    private void createApprovableAlert(final Approvable approvable) {
        showConfirmationAlert(
                "Aktion annehmen?",
                approvable.toString(),
                approvable.getApprovalRequestMessage(),
                () -> approvableService.approveApprovable(approvable),
                () -> approvableService.rejectApprovable(approvable)
        );
    }

    /**
     * Initializes the menu items with their respective actions
     */
    private void initializeMenuItems() {
        instructionsMenuItem.setOnAction(event -> showGameInstructions());
        settingsMenuItem.setOnAction(event -> showSettings());
        leaveGameMenuItem.setOnAction(event -> confirmAndLeaveGame());
    }

    /**
     * Displays the game instructions.
     * This method is called when the user selects the "Spielanleitung" option.
     */
    private void showGameInstructions() {
        // TODO: Implement game instructions display logic
    }

    /**
     * Displays the game settings.
     * This method is called when the user selects the "Einstellungen" option.
     */
    private void showSettings() {
        // TODO: Implement settings display logic
    }

    /**
     * Displays a confirmation dialog for leaving the game.
     * If confirmed, calls the leaveGame method.
     */
    private void confirmAndLeaveGame() {
        showConfirmationAlert(
                "Spiel verlassen",
                "Möchten Sie das Spiel wirklich verlassen?",
                "Wenn Sie das Spiel verlassen, wird das Spiel-Fenster geschlossen.",
                this::closeStage,
                () -> {}
        );
    }

    private void showConfirmationAlert(final String title, final String headerText, final String contentText, final Runnable acceptFunction, final Runnable rejectFunction) {
        Platform.runLater(() -> {
            final Alert alert = createAlert(Alert.AlertType.CONFIRMATION, title, headerText, contentText);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isEmpty()) {
                throw new IllegalStateException();
            }

            if (result.get() == ButtonType.OK) {
                acceptFunction.run();
            } else if (result.get() == ButtonType.CANCEL || result.get() == ButtonType.CLOSE) {
                rejectFunction.run();
            }
        });
    }

    private Alert createAlert(final Alert.AlertType alertType, final String title, final String headerText, final String contentText) {
        final Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        return alert;
    }

    private void executeIfTheUpdatedGameMessageRetrieves(RetrieveUpdatedGameServerMessage retrieveUpdatedGameServerMessage,final Runnable executable) {
        if(retrieveUpdatedGameServerMessage.getGame().getId() == this.game.getId()) {
            executable.run();
            playerCardsOverviewPresenter.updateLabels();
            infectionCardsOverviewPresenter.updateLabels();
            updatePlayerInfo();
            updateShareKnowledgeActionButton();
        }
    }

    /**
     * Updates the player information for all players in the game.
     *
     * @since 2025-01-16
     * @author Marvin Tischer
     */
    private void updatePlayerInfo() {
        for (Player player : this.game.getPlayersInTurnOrder()) {
            for (PlayerPanePresenter playerPanePresenter : playerPanePresenterList) {
                if (playerPanePresenter.hasPlayer(player)) {
                    playerPanePresenter.setPlayerInfo(player);
                }
            }
        }
    }

    /**
     * Adds all players to the player container in the correct order.
     *
     * @since 2025-01-16
     * @author Marvin Tischer
     */
    private void addAllPlayers() {
        resetPlayerContainer();

        List<Player> playersInTurnOrder = this.game.getPlayersInTurnOrder();
        int index = 0;

        for (Player player : playersInTurnOrder) {
            if (isLobbyPlayer(player)) {
                addPlayerPane(player, -1, true);
            } else {
                addPlayerPane(player, index, false);
                index++;
            }
        }
    }

    /**
     * Clears the player container and resets the list of player pane presenters.
     *
     * @since 2025-01-16
     * @author Marvin Tischer
     */
    private void resetPlayerContainer() {
        playerContainer.getChildren().clear();
        playerPanePresenterList.clear();
    }

    /**
     * Checks whether the given player is the lobby player for the logged-in user.
     *
     * @param player the player to check
     * @return true if the player is the lobby player for the logged-in user, false otherwise
     * @since 2025-01-16
     * @author Marvin Tischer
     */
    private boolean isLobbyPlayer(Player player) {
        return player.equals(this.game.getLobby().getPlayerForUser(loggedInUserProvider.get()));
    }

    /**
     * Creates and initializes a player pane presenter for a given player and adds it to the player pane list.
     *
     * @param player the player for whom the pane should be created
     * @param index the index at which to place the player in the container
     * @since 2025-01-16
     * @author Marvin Tischer
     */
    private void addPlayerPane(Player player, int index, boolean isLoggedInPlayer) {
        PlayerPanePresenter playerPanePresenter = createAndInitializePlayerPanePresenter(player);
        this.playerPanePresenterList.add(playerPanePresenter);


        final Parent root = playerPanePresenter.getScene().getRoot();

        addPlayerToContainer(player, root, index, isLoggedInPlayer);
    }

    /**
     * Adds a player's root pane to the appropriate container.
     *
     * @param player the player whose pane is being added
     * @param root the root pane of the player pane presenter
     * @param index the index at which to place the player in the container
     * @since 2025-01-16
     * @author Marvin Tischer
     */
    private void addPlayerToContainer(Player player, Parent root, int index, boolean isLoggedInPlayer) {
        if (isLoggedInPlayer) {
            ownPlayerContainer.addRow(0, root);
        } else {
            playerContainer.add(root, 0, index);
        }
    }

    /**
     * Creates and initializes a `PlayerPanePresenter` for a given player.
     *
     * @param player the player to create the presenter for
     * @return the created and initialized `PlayerPanePresenter` object
     * @since 2025-01-16
     * @author Marvin Tischer
     */
    private PlayerPanePresenter createAndInitializePlayerPanePresenter(Player player) {
        PlayerPanePresenter playerPanePresenter = AbstractPresenter.loadFXMLPresenter(PlayerPanePresenter.class);
        playerPanePresenter.setPlayerInfo(player);

        PlayerMarker playerMarker = gameMapController.createNewPlayerMarker(player);
        playerPanePresenter.setPlayerMarker(playerMarker);

        return playerPanePresenter;
    }

    /**
     * Handles the event when the waive button is pressed.
     * This method checks if the current player is in the game and if they have actions to perform.
     * If both conditions are met, it sends a waive action and updates the state of the waive button.
     */
    @FXML
    private void addWaiveButtonPressed() {
        if (isCurrentPlayerInGame()) {
            if (game.getCurrentTurn().hasActionsToDo()) {
                gameMapController.sendWaiveAction();
                updateWaiveButtonPressed();
            }
        }
    }

    /**
     * Updates the state of the waive button based on the current game state.
     * The button is enabled if the current player is in the game and has actions to perform.
     * Otherwise, the button is disabled.
     */
    private void updateWaiveButtonPressed() {
        if (isCurrentPlayerInGame() && isWaiveActionAvailable() && game.getCurrentTurn().hasActionsToDo()) {
            waiveActionButton.setDisable(false);
        } else {
            waiveActionButton.setDisable(true);
        }
    }


    /**
     * Checks if a waive action is available in the list of possible actions for the current turn.
     *
     * @return true if a waive action is available, false otherwise
     */
    private boolean isWaiveActionAvailable() {
        List<Action> possibleActions = game.getCurrentTurn().getPossibleActions();
        return possibleActions.stream()
                .anyMatch(this::isResearchWaiveAction);
    }

    /**
     * Checks if the given action is an instance of WaiveAction.
     *
     * @param action the action to check
     * @return true if the action is an instance of WaiveAction, false otherwise
     */
    private boolean isResearchWaiveAction(Action action) {
        return action instanceof WaiveAction;
    }

    /**
     * Handles the action of adding a research laboratory when the corresponding button is pressed.
     * Checks if the current player can perform the action and updates the UI accordingly.
     */
    @FXML
    private void addResearchLaboratoryButtonPressed() {
        game.setResearchLaboratoryButtonClicked(true);
        if (isCurrentPlayerInGame()) {
            if(requireMoveResearchLaboratory()) {
                gameMapController.requireMoveResearchLaboratory();
            }
            handleResearchLaboratoryAddition();
        }
    }

    /**
     * @return true if the laboratory should be moved
     * and false if the laboratory not should be moved
     */
    public boolean requireMoveResearchLaboratory() {
        return game.getResearchLaboratories().size() >= Game.DEFAULT_NUMBER_OF_RESEARCH_LABORATORIES;
    }

    /**
     * Initializes the research laboratory button's state based on available actions.
     */
    private void initializeResearchLaboratoryButton() {
        if (isCurrentPlayerInGame()) {
            updateResearchLaboratoryButtonState();
        }
    }

    /**
     * Determines if the research laboratory can be added
     */
    private void handleResearchLaboratoryAddition() {
        if (isResearchLaboratoryActionAvailable()) {
            gameMapController.addResearchLaboratoryMarkerToField();
            updateResearchLaboratoryButtonState();
        }
    }

    /**
     * Checks if a research laboratory action is currently possible.
     *
     * @return true if a research laboratory can be built, false otherwise
     */
    private boolean isResearchLaboratoryActionAvailable() {
        return game.getCurrentTurn().getPossibleActions().stream()
                .anyMatch(this::isResearchLaboratoryBuildAction);
    }

    /**
     * Updates the research laboratory button's disabled state based on available actions.
     */
    public void updateResearchLaboratoryButtonState() {
        if(game.getCurrentTurn().hasActionsToDo() && isCurrentPlayerInGame()) {
            boolean isActionAvailable = game.getCurrentTurn().getPossibleActions().stream()
                    .anyMatch(this::isResearchLaboratoryBuildAction);
            researchLaboratoryButton.setDisable(!isActionAvailable);
        } else {
            researchLaboratoryButton.setDisable(true);
        }
    }

    /**
     * Checks if the current player is in the game.
     *
     * @return true if the current player contains the logged-in user, false otherwise
     */
    private boolean isCurrentPlayerInGame() {
        return game.getCurrentPlayer().containsUser(loggedInUserProvider.get());
    }

    /**
     * Checks if the given action is a research laboratory build action.
     *
     * @param action the action to check
     * @return true if the action is a research laboratory build action, false otherwise
     */
    private boolean isResearchLaboratoryBuildAction(Action action) {
        return action instanceof BuildResearchLaboratoryAction
                || action instanceof ReducedCostBuildResearchLaboratoryAction;
    }

    /**
     * Returns {@code true} if the current {@link Player} of {@link #game} contains the user specified by {@link #loggedInUserProvider}.
     *
     * @return {@code true} if the current {@link Player} of {@link #game} contains the user specified by {@link #loggedInUserProvider}, {@code false} otherwise
     */
    private boolean isAssociatedPlayerCurrentPlayerInGame() {
        return this.game.getCurrentPlayer().containsUser(loggedInUserProvider.get());
    }

    private void updateShareKnowledgeActionButton() {
        Platform.runLater(() -> shareKnowledgeActionButton.setDisable(true));

        if (!isAssociatedPlayerCurrentPlayerInGame() || !game.getCurrentTurn().isInActionPhase()) {
            return;
        }

        final Pair<SendCardAction, ReceiveCardAction> shareKnowledgeActions = getShareKnowledgeActionsFromGame();

        if (shareKnowledgeActions.getKey() != null || shareKnowledgeActions.getValue() != null) {
            Platform.runLater(() -> {
                bindShareKnowledgeActionButtonClickEvent(shareKnowledgeActions);

                shareKnowledgeActionButton.setDisable(false);
            });
        }
    }

    private Pair<SendCardAction, ReceiveCardAction> getShareKnowledgeActionsFromGame() {
        final PlayerTurn playerTurn = game.getCurrentTurn();

        SendCardAction sendCardAction = null;
        ReceiveCardAction receiveCardAction = null;

        for (final Action action : playerTurn.getPossibleActions()) {
            if (action instanceof SendCardAction sca) {
                sendCardAction = sca;
            } else if (action instanceof ReceiveCardAction rca) {
                receiveCardAction = rca;
            }
        }

        return new Pair<>(sendCardAction, receiveCardAction);
    }

    private void bindShareKnowledgeActionButtonClickEvent(final Pair<SendCardAction, ReceiveCardAction> shareKnowledgeActions) {
        shareKnowledgeActionButton.setOnMouseClicked(event -> {
            final ShareKnowledgeActionPresenter shareKnowledgeActionPresenter =
                    AbstractPresenter.loadFXMLPresenter(ShareKnowledgeActionPresenter.class);
            this.associatedPresenters.add(shareKnowledgeActionPresenter);
            shareKnowledgeActionPresenter.initialize(shareKnowledgeActions.getKey(), shareKnowledgeActions.getValue());
            shareKnowledgeActionPresenter.openInNewWindow();
        });
    }

    /**
     * Initializes the chat component and its visibility state
     */
    private void initializeChat() {
        if (chatComponent != null) {
            String cssFile = "/css/GameChatStyle.css";
            if (getClass().getResource(cssFile) != null) {
                chatComponent.getScene().getStylesheets().add(getClass().getResource(cssFile).toExternalForm());

                chatComponent.getStyleClass().add("game-chat-component");

                chatComponent.lookupAll(".text-field").forEach(node -> {
                    HBox.setHgrow(node, Priority.ALWAYS);
                });
            }

            this.chatStackPane.setPickOnBounds(false);
            this.chatAnchorPane.setPickOnBounds(false);
            this.chatComponent.setPickOnBounds(false);

            updateChatVisibility();
        }
    }

    /**
     * Toggles the chat visibility when either of the chat toggle buttons is clicked
     */
    @FXML
    private void toggleChat() {
        isChatVisible = !isChatVisible;
        updateChatVisibility();
    }

    /**
     * Updates the chat visibility based on the current state
     */
    private void updateChatVisibility() {
        if (chatComponent != null) {
            chatComponent.setVisible(isChatVisible);
            chatComponent.setManaged(isChatVisible);

            topRightChatToggleButton.setText(isChatVisible ? "Chat ↓" : "+");
            bottomRightChatToggleButton.setText(isChatVisible ? "Chat" : "Chat ↑");

            topRightChatToggleButton.setVisible(isChatVisible);
            bottomRightChatToggleButton.setVisible(!isChatVisible);
        }
    }
}