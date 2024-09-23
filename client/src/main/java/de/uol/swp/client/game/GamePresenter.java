package de.uol.swp.client.game;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.common.game.Game;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Manages the game board window
 *
 * @author David Scheffler
 * @since 2024-09-09
 */
public class GamePresenter extends AbstractPresenter {
    public static final String GAME_FXML_FOLDER_PATH = "game/";

    private Game game;

    @FXML
    private Pane settingsPane;

    @FXML
    private ImageView settingsIcon;

    @FXML
    private ContextMenu settingsContextMenu;

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
     * @author David Scheffler
     * @since 2024-09-09
     */
    @FXML
    public void initialize(final Stage stage, final Game game) {
        setStage(stage);
        this.game = game;

        stage.setTitle("Game: " + game.getLobby().getName());
        stage.show();

        settingsIcon.fitWidthProperty().bind(settingsPane.widthProperty());
        settingsIcon.fitHeightProperty().bind(settingsPane.heightProperty());

        settingsPane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                settingsContextMenu.show(settingsPane, event.getScreenX(), event.getScreenY());
            }
        });

        gameMapController.initialize(game);
    }

}
