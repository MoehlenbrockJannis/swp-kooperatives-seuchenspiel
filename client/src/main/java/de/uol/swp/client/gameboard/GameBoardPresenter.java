package de.uol.swp.client.gameboard;

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
 */
public class GameBoardPresenter extends AbstractPresenter {

    private Stage stage;
    private Game game;

    @FXML
    private Pane settingsPane;

    @FXML
    private ImageView settingsIcon;

    @FXML
    private ContextMenu settingsContextMenu;

    /**
     * Initializes the game board window
     *
     * @param stage The stage to display the game board window
     * @param game  The game to be displayed
     */
    @FXML
    public void initialize(final Stage stage, final Game game) {
        this.stage = stage;
        this.game = game;
        stage.show();

        settingsIcon.fitWidthProperty().bind(settingsPane.widthProperty());
        settingsIcon.fitHeightProperty().bind(settingsPane.heightProperty());

        settingsPane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                settingsContextMenu.show(settingsPane, event.getScreenX(), event.getScreenY());
            }
        });
    }
}
