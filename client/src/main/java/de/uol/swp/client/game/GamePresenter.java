package de.uol.swp.client.game;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.SceneManager;
import de.uol.swp.client.action.ActionService;
import de.uol.swp.client.action.SelectCityCardsForAntidoteResearchPresenter;
import de.uol.swp.client.action.ShareKnowledgeActionPresenter;
import de.uol.swp.client.approvable.ApprovableService;
import de.uol.swp.client.card.InfectionCardsOverviewPresenter;
import de.uol.swp.client.card.PlayerCardsOverviewPresenter;
import de.uol.swp.client.chat.ChatPresenter;
import de.uol.swp.client.lobby.LobbyService;
import de.uol.swp.client.map.GameMapPresenter;
import de.uol.swp.client.map.research_laboratory.ResearchLaboratoryMarker;
import de.uol.swp.client.marker.LevelableMarkerPresenter;
import de.uol.swp.client.marker.OutbreakMarkerPresenter;
import de.uol.swp.client.plague.PlagueCubeIcon;
import de.uol.swp.client.player.PlayerMarker;
import de.uol.swp.client.player.PlayerPanePresenter;
import de.uol.swp.client.triggerable.TriggerableService;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.client.util.*;
import de.uol.swp.common.action.Action;
import de.uol.swp.common.action.advanced.build_research_laboratory.BuildResearchLaboratoryAction;
import de.uol.swp.common.action.advanced.cure_plague.CurePlagueAction;
import de.uol.swp.common.action.advanced.discover_antidote.DiscoverAntidoteAction;
import de.uol.swp.common.action.advanced.transfer_card.ReceiveCardAction;
import de.uol.swp.common.action.advanced.transfer_card.SendCardAction;
import de.uol.swp.common.action.advanced.transfer_card.ShareKnowledgeAction;
import de.uol.swp.common.action.simple.WaiveAction;
import de.uol.swp.common.answerable.server_message.AnswerableServerMessage;
import de.uol.swp.common.approvable.Approvable;
import de.uol.swp.common.approvable.server_message.ApprovableServerMessage;
import de.uol.swp.common.card.event_card.EventCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.request.LeaveGameRequest;
import de.uol.swp.common.game.server_message.RetrieveUpdatedGameServerMessage;
import de.uol.swp.common.game.turn.PlayerTurn;
import de.uol.swp.common.marker.AntidoteMarker;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.plague.PlagueCube;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.server_message.SendMessageByPlayerServerMessage;
import de.uol.swp.common.triggerable.ManualTriggerable;
import de.uol.swp.common.triggerable.Triggerable;
import de.uol.swp.common.triggerable.server_message.TriggerableServerMessage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.*;

import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;
import lombok.Getter;
import org.greenrobot.eventbus.Subscribe;
import org.kordamp.ikonli.javafx.FontIcon;


import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Manages the game board window
 */
public class GamePresenter extends AbstractPresenter {
    @Getter
    private Game game;

    @FXML
    private Pane settingsPane;

    @FXML
    private FontIcon settingsIcon;

    @FXML
    private ContextMenu settingsContextMenu;

    @FXML
    private MenuItem instructionsMenuItem;

    @FXML
    private GridPane gameInstructionsGridPane;

    @FXML
    private MenuItem leaveGameMenuItem;

    @FXML
    private GridPane playerContainer;

    @FXML
    private StackPane ownPlayerContainer;

    @FXML
    private StackPane outbreakStackPane;

    @FXML
    private StackPane infectionMarkerStackPane;

    @Inject
    private LobbyService lobbyService;
    @Inject
    private ApprovableService approvableService;
    @Inject
    private TriggerableService triggerableService;
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
    private List<RemainingComponentsPresenter> remainingComponentsPresenters;
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
    private Button curePlagueActionButton;

    @FXML
    private GridPane buttonContainer;

    private Map<Plague, Button> antidoteButtons;
    private Map<Plague, ScalableSVGStackPane> antidoteButtonCheckMarks;

    @FXML
    private GridPane remainingComponentsPane;

    private OutbreakMarkerPresenter outbreakMarkerPresenter;
    private LevelableMarkerPresenter infectionMarkerPresenter;


    private static final double RESEARCH_LABORATORY_MARKER_SIZE = 0.7;
    private static final double PLAGUE_CUBE_MARKER_SIZE = 20.0;

    public static final String SVG_PATH_PREFIX_ACTION = "action/";
    private static final String RESEARCH_LAB_SVG = SVG_PATH_PREFIX_ACTION + "addResearchLaboratory.svg";
    private static final String CURE_PLAGUE_SVG = SVG_PATH_PREFIX_ACTION + "curePlague.svg";
    private static final String SHARE_KNOWLEDGE_SVG = SVG_PATH_PREFIX_ACTION + "cards.svg";
    private static final String WAIVE_SVG = SVG_PATH_PREFIX_ACTION + "close.svg";
    private static final Color DEFAULT_ACTION_COLOR = Color.BLACK;
    private static final Color WAIVE_ACTION_COLOR = Color.DARKRED;


    /**
     * Initializes the game board window
     *
     * @param stage The stage to display the game board window
     * @param game  The game to be displayed
     */
    public void initialize(final Stage stage, final Game game)  {
        setStage(stage, false);
        this.game = game;
        final Supplier<Game> gameSupplier = this::getGame;

        stage.setTitle("Game: " + game.getLobby().getName());

        stage.setOnCloseRequest(event -> {
            event.consume();
            confirmAndLeaveGame();
        });

        stage.show();

        gameMapController.initialize(gameSupplier);

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
        this.scene.getStylesheets().add(SceneManager.GAME_INSTRUCTIONS_STYLE_SHEET);

        initializeSettingsIcon();

        playerPanePresenterList = new ArrayList<>();
        addAllPlayers();
        initializeMenuItems();
        chatComponentController.setLobby(game.getLobby());
        initializeChat();
        initializeActionButtons();
        updateShareKnowledgeActionButton();
        updateResearchLaboratoryButtonState();
        updateCurePlagueButtonState();
        initializeResearchLaboratoryButton();
        updateWaiveButtonPressed();
        addAntidoteMarkerButtons();
        initializeOutbreakMarkerPane();
        initializeInfectionMarkerPane();
        this.remainingComponentsPresenters = new ArrayList<>();
        setRemainingComponentsComponent();
    }

    /**
     * Initializes the settings icon with default properties and event listeners.
     */
    private void initializeSettingsIcon() {
        settingsIcon.setIconLiteral("fas-cog");
        settingsIcon.setIconSize(24);
        settingsIcon.setIconColor(Color.GRAY);
        settingsIcon.getStyleClass().add("settings-icon");

        addResizeListeners();
        setupClickListener();

        gameInstructionsGridPane.setVisible(false);
    }

    /**
     * Adds listeners to update the icon size when the settings pane is resized.
     */
    private void addResizeListeners() {
        settingsPane.widthProperty().addListener((obs, oldVal, newVal) -> updateIconSize());
        settingsPane.heightProperty().addListener((obs, oldVal, newVal) -> updateIconSize());
    }

    /**
     * Updates the settings icon size and adjusts its vertical position.
     */
    private void updateIconSize() {
        double newWidth = settingsPane.getWidth();
        double newHeight = settingsPane.getHeight();
        double iconSize = Math.max(1, Math.min(newWidth, newHeight));

        settingsIcon.setIconSize((int) iconSize);
        settingsIcon.setTranslateY(newHeight * 0.9);
    }

    /**
     * Sets up the click listener to open the settings menu when clicking on the icon.
     */
    private void setupClickListener() {
        settingsPane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && isClickOnIcon(event.getX(), event.getY())) {
                settingsContextMenu.show(settingsPane, event.getScreenX(), event.getScreenY());
            }
        });
    }

    /**
     * Checks if a given click position is within the settings icon area.
     *
     * @param clickX The X-coordinate of the click.
     * @param clickY The Y-coordinate of the click.
     * @return True if the click was on the icon, false otherwise.
     */
    private boolean isClickOnIcon(double clickX, double clickY) {
        double iconX = settingsIcon.getLayoutX();
        double iconY = settingsIcon.getLayoutY();
        double iconSize = settingsIcon.getIconSize();

        return clickX >= iconX && clickX <= iconX + iconSize && clickY >= iconY && clickY <= iconY + iconSize;
    }

    /**
     * Sets the remaining components of the current game to be displayed.
     * This includes the remaining research laboratories and plague cubes.
     */
    private void setRemainingComponentsComponent() {
        Platform.runLater(() -> {
            addRemainingResearchLaboratoriesToRemainingComponentsComponent();
            addRemainingPlagueCubesToRemainingComponentsComponent();
        });
    }

    /**
     * Adds the remaining research laboratories to the remaining components component.
     */
    private void addRemainingResearchLaboratoriesToRemainingComponentsComponent() {
        int researchLaboratoryColumnIndex = 0;

        ResearchLaboratoryMarker researchLaboratoryMarker = new ResearchLaboratoryMarker(RESEARCH_LABORATORY_MARKER_SIZE);
        createRemainingComponentsPresenter(
                g -> Game.DEFAULT_NUMBER_OF_RESEARCH_LABORATORIES - g.getResearchLaboratories().size(),
                researchLaboratoryMarker
        );
        addComponentToRemainingComponents(researchLaboratoryMarker, researchLaboratoryColumnIndex);
    }

    /**
     * Adds the remaining plague cubes to the remaining components component.
     */
    private void addRemainingPlagueCubesToRemainingComponentsComponent() {
        int columnIndex = 1;

        for (Map.Entry<Plague, List<PlagueCube>> mapEntry : game.getPlagueCubes().entrySet()) {
            Plague plague = mapEntry.getKey();
            boolean isForeignPlagueCube = false;

            PlagueCubeIcon plagueCubeIcon = new PlagueCubeIcon(PLAGUE_CUBE_MARKER_SIZE, isForeignPlagueCube, plague);
            createRemainingComponentsPresenter(
                    g -> g.getPlagueCubes().get(plague).size(),
                    plagueCubeIcon
            );
            addComponentToRemainingComponents(plagueCubeIcon, columnIndex);
            columnIndex++;
        }
    }

    /**
     * Creates a RemainingComponentsPresenter with the specified number of components and marker.
     */
    private void createRemainingComponentsPresenter(Function<Game,Integer> numberOfComponents, Group marker) {
        RemainingComponentsPresenter presenter = AbstractPresenter.loadFXMLPresenter(RemainingComponentsPresenter.class);
        presenter.initialize(
                this::getGame,
                numberOfComponents,
                marker
        );
        remainingComponentsPresenters.add(presenter);
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

        updateResearchLaboratoryButtonState();
        initializeResearchLaboratoryButton();
        updateWaiveButtonPressed();
        updateAntidoteMarkerButtons();
        updateCurePlagueButtonState();

        outbreakMarkerPresenter.updateLevelIndicator(game.getOutbreakMarker().getLevel());
        infectionMarkerPresenter.updateLevelIndicator(game.getInfectionMarker().getLevel());
    }

    /**
     * Handles an {@link AnswerableServerMessage} detected on the {@link #eventBus}.
     * Sends the {@link Message} to process after it back if requirements are met.
     *
     * @param answerableServerMessage {@link AnswerableServerMessage} from the {@link #eventBus}
     * @see #answerSendMessageByPlayerServerMessageIfRequirementsAreMet(SendMessageByPlayerServerMessage)
     */
    @Subscribe
    public void onAnswerableServerMessage(final AnswerableServerMessage answerableServerMessage) {
        answerSendMessageByPlayerServerMessageIfRequirementsAreMet(answerableServerMessage);
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
        final Triggerable triggerable = triggerableServerMessage.getTriggerable();
        if (triggerable instanceof ManualTriggerable manualTriggerable) {
            highlightManualTriggerable(manualTriggerable, () -> triggerableService.sendManualTriggerable(
                    manualTriggerable,
                    null,
                    null
            ));
        }
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
            case APPROVED, REJECTED:
                answerSendMessageByPlayerServerMessageIfRequirementsAreMet(approvableServerMessage);
                break;
        }
    }

    /**
     * Determine what to do with an unanswered given {@link Approvable}.
     * Creates an alert that this {@link Player} has to respond to.
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

        createApprovableAlert(
                approvable,
                approveApprovable,
                rejectApprovable
        );
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
        if (game.equals(this.game) && player != null && player.containsUser(this.loggedInUserProvider.get()) && message != null) {
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
     * Highlights given {@link ManualTriggerable} and sets a function to approve it.
     *
     * @param manualTriggerable {@link ManualTriggerable} to highlight
     * @param playManualTriggerable function to play given {@link ManualTriggerable}
     */
    private void highlightManualTriggerable(final ManualTriggerable manualTriggerable, final Runnable playManualTriggerable) {
        final Player answeringPlayer = manualTriggerable.getAnsweringPlayer();
        if (!answeringPlayer.containsUser(loggedInUserProvider.get())) {
            return;
        }

        if (manualTriggerable instanceof EventCard eventCard) {
            highlightEventCard(eventCard, playManualTriggerable);
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
        leaveGameMenuItem.setOnAction(event -> confirmAndLeaveGame());
    }

    /**
     * Displays the game instructions.
     * This method is called when the user selects the "Spielanleitung" option.
     */
    private void showGameInstructions() {
        gameInstructionsGridPane.setVisible(true);
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
                () -> {
                    LeaveGameRequest leaveGameRequest = new LeaveGameRequest(
                            game,
                            game.getLobby().getPlayerForUser(loggedInUserProvider.get())
                    );
                    if (!game.isGameLost() && !game.isGameWon()) {
                        eventBus.post(leaveGameRequest);
                    }
                    stage.close();
                },
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
            final Alert alert = createAlert(title, headerText, contentText);

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
     * @param title       the title of the {@link Alert}
     * @param headerText  the header text of the {@link Alert}
     * @param contentText the content text of the {@link Alert}
     * @return a new {@link Alert} with given parameters
     */
    private Alert createAlert(final String title, final String headerText, final String contentText) {
        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        return alert;
    }

    private void executeIfTheUpdatedGameMessageRetrieves(RetrieveUpdatedGameServerMessage retrieveUpdatedGameServerMessage,final Runnable executable) {
        if(retrieveUpdatedGameServerMessage.getGame().getId() == this.game.getId()) {
            executable.run();
            Player currentPlayer = game.getCurrentPlayer();
            playerCardsOverviewPresenter.updateLabels();
            infectionCardsOverviewPresenter.updateLabels();
            infectionCardsOverviewPresenter.updateInfectionMarkerLabel();
            gameMapController.updateGameState(game);
            updatePlayerInfo();
            playerPanePresenterList.forEach(playerPanePresenter -> playerPanePresenter.updateHandCardGridPane(currentPlayer));
            updateShareKnowledgeActionButton();
            updateRemainingComponents();
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
                    playerPanePresenter.setGame(this.game);
                    playerPanePresenter.setPlayerInfo(player);
                }
            }
        }
    }

    /**
     * Updates the labels of all Components.
     * This method iterates through the list of remaining components presenters
     * and calls the updateLabel method on each presenter.
     */
    private void updateRemainingComponents() {
        remainingComponentsPresenters.forEach(RemainingComponentsPresenter::updateLabel);
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
        final Supplier<Game> gameSupplier = this::getGame;
        playerPanePresenter.setGameSupplier(gameSupplier);
        this.playerPanePresenterList.add(playerPanePresenter);

        GridPane playerGridPane = playerPanePresenter.getPlayerGridPane();

        addPlayerToContainer(playerGridPane, index, isLoggedInPlayer);
    }

    /**
     * Adds a player's root pane to the appropriate container.
     *
     * @param playerGridPane the playerGridPane of the player pane presenter
     * @param index the index at which to place the player in the container
     * @since 2025-01-16
     * @author Marvin Tischer
     */
    private void addPlayerToContainer(GridPane playerGridPane, int index, boolean isLoggedInPlayer) {
        if (isLoggedInPlayer) {
            ownPlayerContainer.getChildren().add(playerGridPane);
        } else {
            StackPane otherPlayerStackPane = new StackPane(playerGridPane);
            playerContainer.add(otherPlayerStackPane, 0, index);
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
        playerPanePresenter.setGame(this.game);
        playerPanePresenter.setPlayerInfo(player);
        playerPanePresenter.createHandCardStackPane(isLobbyPlayer(player), game.getMaxHandCards());
        playerPanePresenter.setGameMapPresenter(this.gameMapController);
        playerPanePresenter.updateHandCardGridPane(game.getCurrentPlayer());

        PlayerMarker playerMarker = gameMapController.createNewPlayerMarker(player);
        playerPanePresenter.setPlayerMarker(playerMarker);

        return playerPanePresenter;
    }

    /**
     * Initializes all action buttons by configuring SVG images, dynamic scaling and tooltips.
     */
    private void initializeActionButtons() {
        configureActionButton(
                researchLaboratoryButton,
                RESEARCH_LAB_SVG,
                DEFAULT_ACTION_COLOR,
                new BuildResearchLaboratoryAction().toString()
        );

        configureActionButton(
                curePlagueActionButton,
                CURE_PLAGUE_SVG,
                DEFAULT_ACTION_COLOR,
                new CurePlagueAction().toString()
        );

        configureActionButton(
                shareKnowledgeActionButton,
                SHARE_KNOWLEDGE_SVG,
                DEFAULT_ACTION_COLOR,
                ShareKnowledgeAction.NAME
        );

        configureActionButton(
                waiveActionButton,
                WAIVE_SVG,
                WAIVE_ACTION_COLOR,
                new WaiveAction().toString()
        );
    }

    /**
     * Adds a tooltip to a button.
     *
     * @param button The button to add the tooltip to
     * @param tooltipText The text for the tooltip
     */
    private void addTooltipToButton(Button button, String tooltipText) {
        Tooltip tooltip = TooltipsUtil.createConsistentTooltip(tooltipText);
        Tooltip.install(button, tooltip);
    }

    /**
     * Adds the given SVG image to the given {@link Button} using {@link ScalableSVGStackPane}.
     *
     * @param button    The {@link Button} to which the SVG will be added to.
     * @param svgFile   The SVG image.
     * @param fillColor The {@link Color} used as fill color for the SVG.
     */
    private void addSvgToButton(Button button, File svgFile, Color fillColor) {
        ScalableSVGStackPane svgStackPane = new ScalableSVGStackPane(svgFile, 0.2, fillColor);
        button.setGraphic(svgStackPane);
    }

    /**
     * Bind the size of a given {@link Button} to its parent {@link StackPane}.
     *
     * @param button The {@link Button} whose size will be bound to its parent {@link StackPane}.
     */
    private void bindButtonToParentStackPane(Button button) {
        Parent parent = button.getParent();
        if (parent instanceof StackPane stackPane) {
            stackPane.setMinSize(0, 0);
            NodeBindingUtils.bindRegionSizeToButton(stackPane, button);
        }
    }

    /**
     * Configures an action button completely including SVG, size binding and tooltip.
     *
     * @param button The button to configure
     * @param svgPath The path to the SVG file
     * @param color The color for the SVG
     * @param tooltipText The text for the tooltip
     */
    private void configureActionButton(Button button, String svgPath, Color color, String tooltipText) {
        addSvgToButton(button, FileLoader.readImageFile(svgPath), color);
        bindButtonToParentStackPane(button);
        addTooltipToButton(button, tooltipText);
    }

    /**
     * Handles the event when the waive button is pressed.
     * This method checks if the current player is in the game and if they have actions to perform.
     * If both conditions are met, it sends a waive action and updates the state of the waive button.
     */
    @FXML
    private void addWaiveButtonPressed() {
        if (isCurrentPlayerInGame() && hasActionToDo()) {
            gameMapController.sendWaiveAction();
            updateWaiveButtonPressed();
        }

    }

    private boolean hasActionToDo() {
        return game.getCurrentTurn().hasActionsToDo();
    }

    /**
     * Updates the state of the waive button based on the current game state.
     * The button is enabled if the current player is in the game and has actions to perform.
     * Otherwise, the button is disabled.
     */
    private void updateWaiveButtonPressed() {
        waiveActionButton.setDisable(!isCurrentPlayerInGame() ||
                !isWaiveActionAvailable() ||
                !hasActionToDo() ||
                game.isGameWon() ||
                game.isGameLost());
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
        return action instanceof BuildResearchLaboratoryAction;
    }

    /**
     * Handles cure plague action button press.
     * Prompts user to select plague cubes or prepares single cube cure action
     * based on current game state. Updates button state afterward.
     */
    @FXML
    private void executeCurePlagueActionButtonPressed() {
        if(hasActionToDo() && isCurrentPlayerInGame() && isCurePlagueActionAvailable()) {
            gameMapController.requireChoosePlagueCube();
        }

        updateCurePlagueButtonState();
    }

    /**
     * Updates the state of the cure plague button based on the game state.
     */
    private void updateCurePlagueButtonState() {
        if ((hasActionToDo() && isCurrentPlayerInGame())) {
            curePlagueActionButton.setDisable(!isCurePlagueActionAvailable());
        } else {
            curePlagueActionButton.setDisable(true);
        }
    }

    /**
     * Checks if the cure plague action is available.
     *
     * @return true if the cure plague action is available, false otherwise
     */
    private boolean isCurePlagueActionAvailable() {
        return game.getCurrentTurn().getPossibleActions().stream().anyMatch(CurePlagueAction.class::isInstance);
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
                chatComponent.getScene().getStylesheets().add(Objects.requireNonNull(getClass().getResource(cssFile)).toExternalForm());

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

    /**
     * Initializes antidote marker buttons for each plague in the game.
     * These buttons are initially disabled and display an antidote marker image.
     *
     * @author Marvin Tischer
     * @since 2025-02-05
     */
    private void addAntidoteMarkerButtons(){
        antidoteButtons = new HashMap<>();
        antidoteButtonCheckMarks = new HashMap<>();

        List<Plague> plagues = new ArrayList<>(game.getPlagueCubes().keySet());

        for (int plagueCounter = 0; plagueCounter < plagues.size(); plagueCounter++) {
            Plague plague = plagues.get(plagueCounter);

            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100.0 / plagues.size());
            buttonContainer.getColumnConstraints().add(columnConstraints);

            Button button = new Button();
            button.setDisable(true);

            Tooltip tooltip = TooltipsUtil.createConsistentTooltip(new DiscoverAntidoteAction().toString());
            Tooltip.install(button, tooltip);

            Color fillColor = ColorService.convertColorToJavaFXColor(plague.getColor());
            File svgFile = FileLoader.readImageFile(SVG_PATH_PREFIX_ACTION + "flask.svg");
            addSvgToButton(button, svgFile, fillColor);

            StackPane stackPane = new StackPane();

            stackPane.getChildren().add(button);
            bindButtonToParentStackPane(button);

            if (button.getGraphic() instanceof ScalableSVGStackPane scalableSVGStackPane) {
                addCheckMarkToScalableSVG(scalableSVGStackPane, plague);
            }
            buttonContainer.add(stackPane, plagueCounter, 0);
            antidoteButtons.put(plague, button);
        }
    }

    /**
     * Adds a green check mark SVG to the given {@link ScalableSVGStackPane}.
     * The check mark is not visible by default.
     *
     * @param svgStackPane The {@link ScalableSVGStackPane} to which a check mark SVG will be added to.
     * @param plague       The {@link Plague} associated with the given {@link ScalableSVGStackPane}.
     */
    private void addCheckMarkToScalableSVG(ScalableSVGStackPane svgStackPane, Plague plague) {
        File svgFile = FileLoader.readImageFile(SVG_PATH_PREFIX_ACTION + "check.svg");
        ScalableSVGStackPane checkMarkSvgStackPane = new ScalableSVGStackPane(svgFile, 0, Color.GREEN);
        svgStackPane.getChildren().add(checkMarkSvgStackPane);
        checkMarkSvgStackPane.setVisible(false);
        antidoteButtonCheckMarks.put(plague, checkMarkSvgStackPane);
    }

    /**
     * Retrieves the current {@code DiscoverAntidoteAction} for the ongoing turn, if available.
     *
     * @return An {@code Optional} containing the current {@code DiscoverAntidoteAction} if present,
     *         otherwise an empty {@code Optional}.
     * @author Marvin Tischer
     * @since 2025-02-07
     */
    private Optional<DiscoverAntidoteAction> getCurrentDiscoverAntidoteAction() {
        return game.getCurrentTurn().findActionOfType(DiscoverAntidoteAction.class);
    }

    /**
     * Configures the given button to enable or disable antidote-related actions based on the
     * availability of the specified plague and whether the current player is in the game.
     *
     * @param button The button to configure.
     * @param plague The plague associated with the antidote action.
     * @param action The {@code DiscoverAntidoteAction} that determines if the plague can be targeted.
     * @author Marvin Tischer
     * @since 2025-02-07
     */
    private void configureAntidoteButton(Button button, Plague plague, DiscoverAntidoteAction action) {

        boolean hasPlague =  action.getAvailablePlagues().contains(plague);

        if (hasPlague && isCurrentPlayerInGame()) {
            button.setDisable(false);
            button.setOnAction(event -> handleAntidoteMarkerButtonAction(plague, action));
        } else {
            button.setDisable(true);
        }
    }

    /**
     * Updates the antidote marker buttons by configuring them based on the current
     * {@code DiscoverAntidoteAction} and the available plagues in the game.
     * If no {@code DiscoverAntidoteAction} is present, the method exits early.
     *
     * @author Marvin Tischer
     * @since 2025-02-07
     */
    private void updateAntidoteMarkerButtons() {
        List<Plague> plagues = game.getPlagues();

        for (Plague plague : plagues) {
            if (checkIfAntidoteIsDiscovered(plague)) {
                antidoteButtonCheckMarks.get(plague).setVisible(true);
            }
        }

        Optional<DiscoverAntidoteAction> optionalDiscoverAntidoteAction = getCurrentDiscoverAntidoteAction();

        if(optionalDiscoverAntidoteAction.isEmpty() ||
                !hasActionToDo() ||
                game.isGameWon() ||
                game.isGameLost()) {
            disableAntidoteMarkerButtons();
            return;
        }

        DiscoverAntidoteAction discoverAntidoteAction = optionalDiscoverAntidoteAction.get();

        for (Plague plague : plagues) {
            Button button = antidoteButtons.get(plague);

            if (checkIfAntidoteIsDiscovered(plague)) {
                button.setDisable(true);
            } else {
                configureAntidoteButton(button, plague, discoverAntidoteAction);
            }
        }
    }

    /**
     * Checks if an antidote has been discovered for the specified plague.
     *
     * @param plague The plague to check.
     * @return {@code true} if an antidote has been discovered for the given plague, otherwise {@code false}.
     * @author Marvin Tischer
     * @since 2025-02-17
     */
    private boolean checkIfAntidoteIsDiscovered(Plague plague) {
        List<AntidoteMarker> antidoteMarkers = game.getAntidoteMarkers();

        for (AntidoteMarker antidoteMarker : antidoteMarkers) {
            if (plague.equals(antidoteMarker.getPlague())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Disables all antidote marker buttons, preventing further interactions.
     *
     * @author Marvin Tischer
     * @since 2025-02-13
     */
    private void disableAntidoteMarkerButtons(){
        for (Map.Entry<Plague, Button> entry : antidoteButtons.entrySet()) {
            entry.getValue().setDisable(true);
        }
    }

    /**
     * Handles the action when an antidote marker button is clicked.
     * Opens a selection popup for choosing city cards required for antidote research.
     * After the selection, the action is sent, and the antidote button is disabled.
     *
     * @param plague The plague for which the antidote is being researched.
     * @param action The {@link DiscoverAntidoteAction} associated with the current turn.
     *
     * @author Marvin Tischer
     * @since 2025-02-05
     */
    private void handleAntidoteMarkerButtonAction(Plague plague, DiscoverAntidoteAction action) {
        SelectCityCardsForAntidoteResearchPresenter selectionPopupPresenter = AbstractPresenter.loadFXMLPresenter(SelectCityCardsForAntidoteResearchPresenter.class);
        selectionPopupPresenter.initialize(action, plague);

        Stage stage = createSelectionStage(selectionPopupPresenter);
        stage.showAndWait();

        selectionPopupPresenter.sendDiscoverAntidoteAction();
    }

    /**
     * Creates and configures a new selection stage for choosing city cards
     * during antidote research.
     *
     * @param presenter The presenter responsible for providing the scene for selection.
     * @return A configured {@code Stage} instance for the selection process.
     *
     * @author Marvin Tischer
     * @since 2025-02-13
     */
    private Stage createSelectionStage(SelectCityCardsForAntidoteResearchPresenter presenter) {
        Stage stage = new Stage();
        stage.setTitle("Spielerkarten auswählen");
        stage.setScene(presenter.getScene());
        stage.setResizable(false);
        return stage;
    }

    /**
     * Initializes the {@link #outbreakStackPane} using {@link OutbreakMarkerPresenter}
     */
    private void initializeOutbreakMarkerPane() {
        List<javafx.scene.paint.Color> colorList = List.of(
                javafx.scene.paint.Color.GREEN,
                javafx.scene.paint.Color.YELLOW,
                javafx.scene.paint.Color.RED
        );
        this.outbreakMarkerPresenter = new OutbreakMarkerPresenter(outbreakStackPane, game.getOutbreakMarker(), colorList);
    }

    /**
     * Initializes the {@link #infectionMarkerStackPane} using {@link LevelableMarkerPresenter}
     */
    private void initializeInfectionMarkerPane() {
        List<javafx.scene.paint.Color> colorList = List.of(
                javafx.scene.paint.Color.DARKOLIVEGREEN,
                javafx.scene.paint.Color.YELLOW,
                javafx.scene.paint.Color.ORANGE
        );
        this.infectionMarkerPresenter = new LevelableMarkerPresenter(infectionMarkerStackPane, game.getInfectionMarker(), colorList);
        infectionMarkerStackPane.setVisible(false);
        infectionMarkerStackPane.setMouseTransparent(true);
        infectionCardsOverviewPresenter.setCardIconOnMouseEntered(() -> infectionMarkerStackPane.setVisible(true));
        infectionCardsOverviewPresenter.setCardIconOnMouseExited(() -> infectionMarkerStackPane.setVisible(false));
    }
}