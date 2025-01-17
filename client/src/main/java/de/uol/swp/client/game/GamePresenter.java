package de.uol.swp.client.game;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.action.ActionService;
import de.uol.swp.client.approvable.ApprovableService;
import de.uol.swp.client.card.InfectionCardsOverviewPresenter;
import de.uol.swp.client.card.PlayerCardsOverviewPresenter;
import de.uol.swp.client.chat.ChatPresenter;
import de.uol.swp.client.lobby.LobbyService;
import de.uol.swp.client.research_laboratory.ResearchLaboratoryMarker;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.common.action.Action;
import de.uol.swp.common.approvable.Approvable;
import de.uol.swp.common.approvable.server_message.ApprovableServerMessage;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.server_message.RetrieveUpdatedGameServerMessage;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.player.Player;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import lombok.Getter;
import org.greenrobot.eventbus.Subscribe;

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

    private boolean isChatVisible = true;


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
        gameMapController.initialize(game);
        initializeMenuItems();
        chatComponentController.setLobby(game.getLobby());
        initializeChat();
    }

    @Subscribe
    public void onReceiveUpdatedGameMessage(RetrieveUpdatedGameServerMessage message) {
        Runnable executable = () -> this.game = message.getGame();
        executeIfTheUpdatedGameMessageRetrieves(message, executable);
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
        }

    }

    /**
     * Adds a research laboratory to the current field.
     */
    @FXML
    private void addResearchLaboratoryButtonPressed() {
        // TODO: replace with BuildResearchLaboratoryAction
        final ResearchLaboratoryMarker researchLaboratoryMarker = new ResearchLaboratoryMarker(0.7);
        final Player currentPlayer = this.game.getLobby().getPlayerForUser(loggedInUserProvider.get());
        final Field field = currentPlayer.getCurrentField();
        gameMapController.addResearchLaboratoryMarkerToField(researchLaboratoryMarker, field);
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