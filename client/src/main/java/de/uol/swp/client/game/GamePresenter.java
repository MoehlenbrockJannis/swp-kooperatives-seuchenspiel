package de.uol.swp.client.game;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.card.CardOverviewPresenter;
import de.uol.swp.client.lobby.LobbyService;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.server_message.RetrieveUpdatedGameMessage;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
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
    private LoggedInUserProvider loggedInUserProvider;

    @FXML
    private Pane playerCardStackPane;

    @FXML
    private CardOverviewPresenter playerCardsOverviewController;

    @FXML
    private GameMapPresenter gameMapController;


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
    public void initialize(final Stage stage, final Game game) {
        setStage(stage, false);
        this.game = game;
        final Supplier<Game> gameSupplier = () -> getGame();

        stage.setTitle("Game: " + game.getLobby().getName());
        stage.show();

        playerCardsOverviewController.initialize(
                gameSupplier,
                g -> g.getPlayerDrawStack(),
                g -> g.getPlayerDiscardStack(),
                this.playerCardStackPane,
                (player, cardService) -> cardService.sendDrawPlayerCardRequest(this.game, player),
                (player, cardService, playerCard) -> cardService.sendDiscardPlayerCardRequest(this.game, player, (PlayerCard) playerCard)
        );
        playerCardsOverviewController.setWindow(stage);
        this.associatedPresenters.add(playerCardsOverviewController);

        settingsIcon.fitWidthProperty().bind(settingsPane.widthProperty());
        settingsIcon.fitHeightProperty().bind(settingsPane.heightProperty());

        settingsPane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                settingsContextMenu.show(settingsPane, event.getScreenX(), event.getScreenY());
            }
        });
        gameMapController.initialize(game);
        initializeMenuItems();
    }

    @Subscribe
    public void onReceiveUpdatedGameMessage(RetrieveUpdatedGameMessage message) {
        this.game = message.getGame();
        playerCardsOverviewController.updateLabels();
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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Spiel verlassen");
        alert.setHeaderText("Möchten Sie das Spiel wirklich verlassen?");
        alert.setContentText("Wenn Sie das Spiel verlassen, wird das Spiel-Fenster geschlossen.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            closeStage();
        }
    }
}