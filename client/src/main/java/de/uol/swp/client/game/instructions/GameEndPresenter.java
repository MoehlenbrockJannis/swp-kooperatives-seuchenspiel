package de.uol.swp.client.game.instructions;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.util.FileLoader;
import de.uol.swp.client.util.ScalableSVGStackPane;
import de.uol.swp.client.util.exception.NodeNotFoundException;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Presents the game end tab within the {@link GameInstructionsPresenter}.
 *
 */
public class GameEndPresenter extends AbstractPresenter {

    private static final Logger LOG = LogManager.getLogger(GameEndPresenter.class);

    @FXML
    private GridPane gameLossGridPane;
    @FXML
    private GridPane gameWinGridPane;

    private static final String ACTION_SVG_PATH_PREFIX = "action/";
    private static final String SKULL_SVG = "skull.svg";
    private static final String DISCOVER_ANTIDOTE_SVG = ACTION_SVG_PATH_PREFIX + "flask.svg";
    private static final String CHECK_MARK_SVG = ACTION_SVG_PATH_PREFIX + "check.svg";

    private static final Color DEFAULT_COLOR = Color.BLACK;
    private static final Color CHECK_MARK_COLOR = Color.GREEN;

    /**
     * Initializes the SVG icon {@link javafx.scene.layout.StackPane}s of the {@link GameEndPresenter}.
     */
    @FXML
    public void initialize() {
        GameInstructionIconUtil.createSVGIconStackPane(SKULL_SVG, DEFAULT_COLOR, gameLossGridPane, 0, 0);
        GameInstructionIconUtil.createSVGIconStackPane(DISCOVER_ANTIDOTE_SVG, DEFAULT_COLOR, gameWinGridPane, 0, 0);
        addCheckMarkIcon();
    }

    /**
     * Adds a green check mark to the {@link ScalableSVGStackPane} containing the {@link #CHECK_MARK_SVG} SVG.
     */
    private void addCheckMarkIcon() {
        try {
            ScalableSVGStackPane svgStackPane = getGameWinScalableSVGStackPane();
            ScalableSVGStackPane checkMarkSvgStackPane = new ScalableSVGStackPane(FileLoader.readImageFile(CHECK_MARK_SVG), 0.2, CHECK_MARK_COLOR);
            svgStackPane.getChildren().add(checkMarkSvgStackPane);
        } catch (NodeNotFoundException e) {
            LOG.error(e);
        }
    }

    /**
     * Returns the {@link ScalableSVGStackPane} within {@link #gameWinGridPane}.
     *
     * @return The {@link ScalableSVGStackPane} within {@link #gameWinGridPane}.F
     * @throws NodeNotFoundException if the {@link ScalableSVGStackPane} could not be found within {@link #gameWinGridPane}.
     */
    private ScalableSVGStackPane getGameWinScalableSVGStackPane() throws NodeNotFoundException {
        int rowIndex = 0;
        int columnIndex = 0;
        for (Node node : gameWinGridPane.getChildren()) {
            if (GridPane.getRowIndex(node) == rowIndex && GridPane.getColumnIndex(node) == columnIndex && node instanceof ScalableSVGStackPane svgStackPane) {
                return svgStackPane;
            }
        }

        throw new NodeNotFoundException(
                String.format("No scalableSVGStackPane found in row %d and column %d of gameWinGridPane", rowIndex, columnIndex)
        );
    }
}
