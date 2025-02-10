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
import de.uol.swp.client.plague.PlagueCubeIcon;
import de.uol.swp.client.player.PlayerMarker;
import de.uol.swp.client.research_laboratory.ResearchLaboratoryMarker;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.client.util.ColorService;
import de.uol.swp.common.action.Action;
import de.uol.swp.common.action.advanced.build_research_laboratory.BuildResearchLaboratoryAction;
import de.uol.swp.common.action.advanced.build_research_laboratory.ReducedCostBuildResearchLaboratoryAction;
import de.uol.swp.common.action.advanced.transfer_card.ReceiveCardAction;
import de.uol.swp.common.action.advanced.transfer_card.SendCardAction;
import de.uol.swp.common.action.simple.WaiveAction;
import de.uol.swp.common.approvable.Approvable;
import de.uol.swp.common.approvable.server_message.ApprovableServerMessage;
import de.uol.swp.common.card.event_card.EventCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.server_message.RetrieveUpdatedGameServerMessage;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.plague.PlagueCube;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.server_message.SendMessageByPlayerServerMessage;
import de.uol.swp.common.player.turn.PlayerTurn;
import de.uol.swp.common.player.turn.request.EndPlayerTurnRequest;
import de.uol.swp.common.util.Color;
import de.uol.swp.common.triggerable.server_message.TriggerableServerMessage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
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
import java.util.Map;
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

    @FXML
    private GridPane remainingComponentsPane;

    private static final double RESEARCH_LABORATORY_MARKER_SIZE = 0.7;
    private static final double PLAGUE_CUBE_MARKER_SIZE = 20.0;

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

        setRemainingComponentsComponent();
    }

    /**
     * Sets the remaining components of the current game to be displayed.
     * This includes the remaining research laboratories and plague cubes.
     */
    private void setRemainingComponentsComponent() {
        Platform.runLater(() -> {
            clearRemainingComponentsComponent();
            addRemainingResearchLaboratoriesToRemainingComponentsComponent();
            addRemainingPlagueCubesToRemainingComponentsComponent();
        });
    }

    /**
     * Clears the remaining components component.
     */
    private void clearRemainingComponentsComponent() {
        remainingComponentsPane.getChildren().clear();
    }

    /**
     * Adds the remaining research laboratories to the remaining components component.
     */
    private void addRemainingResearchLaboratoriesToRemainingComponentsComponent() {
        int remainingResearchLaboratories = Game.DEFAULT_NUMBER_OF_RESEARCH_LABORATORIES - game.getResearchLaboratories().size();
        int researchLaboratoryColumnIndex = 0;

        ResearchLaboratoryMarker researchLaboratoryMarker = new ResearchLaboratoryMarker(RESEARCH_LABORATORY_MARKER_SIZE);
        createRemainingComponentsPresenter(remainingResearchLaboratories, researchLaboratoryMarker);

        addComponentToRemainingComponents(researchLaboratoryMarker, researchLaboratoryColumnIndex);
    }

    /**
     * Adds the remaining plague cubes to the remaining components component.
     */
    private void addRemainingPlagueCubesToRemainingComponentsComponent() {
        int columnIndex = 1;

        for (Map.Entry<Plague, List<PlagueCube>> mapEntry : game.getPlagueCubes().entrySet()) {
            Plague plague = mapEntry.getKey();
            List<PlagueCube> plagueCubes = mapEntry.getValue();
            int amountOfRemainingPlagueCubes = plagueCubes.size();
            Color plagueColor = plague.getColor();
            boolean isForeignPlagueCube = false;

            PlagueCubeIcon plagueCubeIcon = new PlagueCubeIcon(PLAGUE_CUBE_MARKER_SIZE, ColorService.convertColorToJavaFXColor(plagueColor), isForeignPlagueCube);
            createRemainingComponentsPresenter(amountOfRemainingPlagueCubes, plagueCubeIcon);
            addComponentToRemainingComponents(plagueCubeIcon, columnIndex);
            columnIndex++;
        }
    }

    /**
     * Creates a RemainingComponentsPresenter with the specified number of components and marker.
     */
    private void createRemainingComponentsPresenter(int numberOfComponents, Group marker) {
        RemainingComponentsPresenter presenter = AbstractPresenter.loadFXMLPresenter(RemainingComponentsPresenter.class);
        presenter.setNumberOfRemainingComponents(numberOfComponents);
        presenter.setComponentSymbol(marker);
        associatedPresenters.add(presenter);
    }

    /**
     * Adds the given component to the remaining components component at the specified column index.
     */
    private void addComponentToRemainingComponents(Group marker, int columnIndex) {
        int firstRowIndex = 0;

        final Parent root = marker.getScene().getRoot();
        remainingComponentsPane.getChildren().add(root);

        GridPane.setColumnIndex(root, columnIndex);
        GridPane.setRowIndex(root, firstRowIndex);
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

        if (isTurnOver(this.game)) {
            EndPlayerTurnRequest endTurnMessage = new EndPlayerTurnRequest(game);
            eventBus.post(endTurnMessage);
            updateResearchLaboratoryButtonState();
        }

        initializeResearchLaboratoryButton();
        updateWaiveButtonPressed();
        setRemainingComponentsComponent();
    }

    /**
     * Checks if the current turn is over
     * @param game The game to check
     * @return True if the turn is over, false otherwise
     */
    private boolean isTurnOver(Game game) {
       return this.game.getCurrentTurn().isOver();
    }

    /**
     * Handles a {@link TriggerableServerMessage} detected on the {@link #eventBus}.
     * Sends the {@link Message} to process after it back if requirements are met.
     *
     * @param triggerableServerMessage {@link TriggerableServerMessage} from the {@link #eventBus}
     * @see #answerSendMessageByPlayerServerMessageIfRequirementsAreMet(SendMessageByPlayerServerMessage)
     */
    @Subscribe
    public void onTriggerableServerMessage(final TriggerableServerMessage triggerableServerMessage) {
        answerSendMessageByPlayerServerMessageIfRequirementsAreMet(triggerableServerMessage);
    }

    /**
     * Handles an {@link ApprovableServerMessage} detected on the {@link #eventBus}.
     * Determines what to do with it depending on its status.
     *
     * @param approvableServerMessage {@link ApprovableServerMessage} from the {@link #eventBus}
     * @see #determineUnansweredApprovableStrategy(Approvable, Message, Player, Message, Player)
     * @see #answerSendMessageByPlayerServerMessageIfRequirementsAreMet(SendMessageByPlayerServerMessage)
     */
    @Subscribe
    public void onApprovableServerMessage(final ApprovableServerMessage approvableServerMessage) {
        final Approvable approvable = approvableServerMessage.getApprovable();

        if (approvable.getGame().getId() != this.game.getId()) {
            return;
        }

        switch (approvableServerMessage.getStatus()) {
            case OUTBOUND:
                determineUnansweredApprovableStrategy(
                        approvable,
                        approvableServerMessage.getOnApproved(),
                        approvableServerMessage.getOnApprovedPlayer(),
                        approvableServerMessage.getOnRejected(),
                        approvableServerMessage.getOnRejectedPlayer()
                );
                break;
            case APPROVED, REJECTED, TEMPORARILY_REJECTED:
                answerSendMessageByPlayerServerMessageIfRequirementsAreMet(approvableServerMessage);
                break;
        }
    }

    /**
     * Determine what to do with an unanswered given {@link Approvable}
     * depending on whether an answer is required for it or not.
     * If an answer is required, creates an alert that this {@link Player} has to respond to.
     *
     * @param approvable {@link Approvable} to answer
     * @param onApproved {@link Message} to send if {@link Approvable} is approved
     * @param onApprovedPlayer {@link Player} to send {@code onApproved}
     * @param onRejected {@link Message} to send if {@link Approvable} is rejected
     * @param onRejectedPlayer {@link Player} to send {@code onRejected}
     */
    private void determineUnansweredApprovableStrategy(final Approvable approvable,
                                                       final Message onApproved,
                                                       final Player onApprovedPlayer,
                                                       final Message onRejected,
                                                       final Player onRejectedPlayer) {
        if (!approvable.getApprovingPlayer().containsUser(loggedInUserProvider.get())) {
            return;
        }

        final Runnable approveApprovable = () -> approvableService.approveApprovable(approvable, onApproved, onApprovedPlayer, onRejected, onRejectedPlayer);
        final Runnable rejectApprovable = () -> approvableService.rejectApprovable(approvable, onApproved, onApprovedPlayer, onRejected, onRejectedPlayer);
        final Runnable temporarilyRejectApprovable = () -> approvableService.temporarilyRejectApprovable(approvable, onApproved, onApprovedPlayer, onRejected, onRejectedPlayer);

        if (approvable.isResponseRequired()) {
            createApprovableAlert(
                    approvable,
                    approveApprovable,
                    rejectApprovable
            );
        } else {
            highlightApprovableAndReject(
                    approvable,
                    approveApprovable,
                    onApproved,
                    temporarilyRejectApprovable,
                    onRejected
            );
        }
    }

    /**
     * Answers a given {@link SendMessageByPlayerServerMessage} if the requirements are met.
     *
     * @param serverMessage {@link SendMessageByPlayerServerMessage} to answer
     * @see #sendMessageIfGameIsCurrentGameAndPlayerIsCurrentPlayer(Message, Game, Player)
     */
    private void answerSendMessageByPlayerServerMessageIfRequirementsAreMet(final SendMessageByPlayerServerMessage serverMessage) {
        sendMessageIfGameIsCurrentGameAndPlayerIsCurrentPlayer(
                serverMessage.getMessageToSend(),
                serverMessage.getGame(),
                serverMessage.getReturningPlayer()
        );
    }

    /**
     * Sends given {@link Message} if given {@link Game} equals {@link #game} and given {@link Player} contains the logged-in user.
     *
     * @param message {@link Message} to send
     * @param game {@link Game} to check equality for
     * @param player {@link Player} to check whether it contains logged-in user
     */
    private void sendMessageIfGameIsCurrentGameAndPlayerIsCurrentPlayer(final Message message, final Game game, final Player player) {
        if (game.equals(this.game) && player.containsUser(this.loggedInUserProvider.get()) && message != null) {
            eventBus.post(message);
        }
    }

    /**
     * Creates an alert prompting the {@link Player} to either accept or reject given {@link Approvable}.
     * The given runnables are what is executed if
     *  either the {@link Player} accepts ({@code approveApprovable})
     *  or the {@link Player} rejects ({@code rejectApprovable}).
     *
     * @param approvable {@link Approvable} to either accept or reject
     * @param approveApprovable {@link Runnable} that is executed if the {@link Player} accepts
     * @param rejectApprovable {@link Runnable} that is executed if the {@link Player} rejects
     */
    private void createApprovableAlert(final Approvable approvable, final Runnable approveApprovable, final Runnable rejectApprovable) {
        showConfirmationAlert(
                "Aktion annehmen?",
                approvable.toString(),
                approvable.getApprovalRequestMessage(),
                approveApprovable,
                rejectApprovable
        );
    }

    /**
     * <p>
     *     Highlights given {@link Approvable} and temporarily rejects it.
     * </p>
     *
     * <p>
     *     Also prevents an infinite cycle by resetting the {@link Message} to send after
     *     on given {@code onApproved} {@link Message} if it is a {@link SendMessageByPlayerServerMessage}.
     * </p>
     *
     * @param approvable {@link Approvable} to highlight and reject
     * @param approveApprovable {@link Runnable} to approve the {@link Approvable}
     * @param onApproved {@link Message} to send after {@link Approvable} is approved
     * @param rejectApprovable {@link Runnable} to reject the {@link Approvable}
     * @param onRejected {@link Message} to send after {@link Approvable} is rejected
     */
    private void highlightApprovableAndReject(final Approvable approvable,
                                              final Runnable approveApprovable,
                                              final Message onApproved,
                                              final Runnable rejectApprovable,
                                              final Message onRejected) {
        resetMessageToSendIfMessageIsSendMessageByPlayerServerMessageAndMessagesToSendAreEqual(onApproved, onRejected);

        rejectApprovable.run();

        highlightApprovable(approvable, approveApprovable);
    }

    /**
     * Resets the {@link Message} to send if given {@code message} is a {@link SendMessageByPlayerServerMessage} and
     * given {@code messageToSend} is equal to the {@link Message} to send of {@code message}.
     *
     * @param message {@link Message} that may be a {@link SendMessageByPlayerServerMessage}
     * @param messageToSend {@link Message} that may be given {@code message}'s {@link Message} to send
     */
    private void resetMessageToSendIfMessageIsSendMessageByPlayerServerMessageAndMessagesToSendAreEqual(final Message message,
                                                                                                        final Message messageToSend) {
        if (message instanceof SendMessageByPlayerServerMessage serverMessage && serverMessage.getMessageToSend().equals(messageToSend)) {
            serverMessage.setMessageToSend(null);
        }
    }

    /**
     * Highlights given {@link Approvable} and sets a function to approve it.
     *
     * @param approvable {@link Approvable} to highlight
     * @param approveApprovable function to approve given {@link Approvable}
     */
    private void highlightApprovable(final Approvable approvable, final Runnable approveApprovable) {
        if (approvable instanceof EventCard eventCard) {
            highlightEventCard(eventCard, approveApprovable);
        }
    }

    /**
     * Highlights given {@link EventCard} by delegating to the fitting {@link PlayerPanePresenter}.
     * Also gives that a function to play it.
     *
     * @param eventCard {@link EventCard} to highlight
     * @param playEventCard function to play given {@link EventCard}
     */
    private void highlightEventCard(final EventCard eventCard, final Runnable playEventCard) {
        findPlayerPanePresenterByPlayer(eventCard.getPlayer()).ifPresent(
                playerPanePresenter -> playerPanePresenter.highlightHandCardAndAssignClickListener(eventCard, playEventCard)
        );
    }

    /**
     * Finds a {@link PlayerPanePresenter} for a given {@link Player} in {@link #playerPanePresenterList}.
     *
     * @param player {@link Player} to search associated {@link PlayerPanePresenter} for
     * @return {@link Optional} with {@link PlayerPanePresenter} for given {@link Player} or empty {@link Optional} if there is none
     */
    private Optional<PlayerPanePresenter> findPlayerPanePresenterByPlayer(final Player player) {
        return this.playerPanePresenterList.stream()
                .filter(playerPanePresenter -> playerPanePresenter.hasPlayer(player))
                .findFirst();
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

    /**
     * Creates and shows a confirmation alert with given {@code title}, {@code headerText}, {@code contentText} and
     * a function to be executed on accepting ({@code acceptFunction}) and
     * a function to be executed on rejecting ({@code rejectFunction}).
     *
     * @param title title of the alert
     * @param headerText header text of the alert
     * @param contentText content text of the alert
     * @param acceptFunction function to bind to the accept button
     * @param rejectFunction function to bind to the reject button
     */
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

    /**
     * Creates an alert with given parameters for {@link Alert.AlertType}, {@code title}, {@code headerText} and {@code contentText}.
     *
     * @param alertType the type of the {@link Alert}
     * @param title the title of the {@link Alert}
     * @param headerText the header text of the {@link Alert}
     * @param contentText the content text of the {@link Alert}
     * @return a new {@link Alert} with given parameters
     */
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

                chatComponent.lookupAll(".text-field").forEach(node -> HBox.setHgrow(node, Priority.ALWAYS));
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