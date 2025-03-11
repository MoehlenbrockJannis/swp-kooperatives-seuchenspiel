package de.uol.swp.client.game.instructions;

import de.uol.swp.client.AbstractPresenter;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

/**
 * Presents the introduction tab within the {@link GameInstructionsPresenter}.
 *
 */
public class IntroductionTabPresenter extends AbstractPresenter {

    @FXML
    private GridPane gridPane;

    private static final String SVG_PATH_PREFIX = "action/";
    private static final String FLASK_SVG = SVG_PATH_PREFIX + "flask.svg";

    /**
     * Initializes the SVG icon {@link javafx.scene.layout.StackPane} of the {@link IntroductionTabPresenter}.
     */
    @FXML
    public void initialize() {
        GameInstructionIconUtil.createSVGIconStackPane(FLASK_SVG, Color.BLACK, gridPane, 0, 0);
    }
}
