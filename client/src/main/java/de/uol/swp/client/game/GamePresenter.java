package de.uol.swp.client.game;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.common.game.Game;
import javafx.stage.Stage;

/**
 * Manages the game window
 */
public class GamePresenter extends AbstractPresenter {

    public static final String FXML = "/fxml/GameBoardView.fxml";

    private Stage stage;
    private Game game;

    /**
     * Initializes the game window
     *
     * @param stage The stage to display the game window
     * @param game  The game to be displayed
     */
    public void initialize(final Stage stage, final Game game) {
        this.stage = stage;
        this.game = game;
        stage.show();
    }

}
