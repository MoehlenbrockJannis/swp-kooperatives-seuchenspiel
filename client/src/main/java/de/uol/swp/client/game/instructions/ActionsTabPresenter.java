package de.uol.swp.client.game.instructions;

import de.uol.swp.client.AbstractPresenter;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * Presents the roles tab within the {@link GameInstructionsPresenter}.
 *
 * @author David Scheffler
 * @since 2025-03-10
 */
public class ActionsTabPresenter extends AbstractPresenter {

    @FXML
    private GridPane simpleActionGridPane;
    @FXML
    private GridPane advancedActionGridPane;

    private static final String SVG_PATH_PREFIX = "action/";
    private static final String CAR_ACTION_SVG = SVG_PATH_PREFIX + "carAction.svg";
    private static final String DIRECT_FLIGHT_ACTION_SVG = SVG_PATH_PREFIX + "directFlightAction.svg";
    private static final String CHARTER_FLIGHT_ACTION_SVG = SVG_PATH_PREFIX + "charterFlightAction.svg";
    private static final String SHUTTLE_FLIGHT_ACTION_SVG = SVG_PATH_PREFIX + "shuttleFlightAction.svg";
    private static final String RESEARCH_LAB_SVG = SVG_PATH_PREFIX + "addResearchLaboratory.svg";
    private static final String CURE_PLAGUE_SVG = SVG_PATH_PREFIX + "curePlague.svg";
    private static final String SHARE_KNOWLEDGE_SVG = SVG_PATH_PREFIX + "cards.svg";
    private static final String WAIVE_ACTION_SVG = SVG_PATH_PREFIX + "close.svg";
    private static final String DISCOVER_ANTIDOTE_SVG = SVG_PATH_PREFIX + "flask.svg";

    private static final Color DEFAULT_ACTION_COLOR = Color.BLACK;
    private static final Color WAIVE_ACTION_COLOR = Color.DARKRED;


    /**
     * Initializes all SVG icon {@link StackPane}s of the {@link ActionsTabPresenter}.
     */
    @FXML
    public void initialize() {
        GameInstructionIconUtil.createSVGIconStackPane(CAR_ACTION_SVG, DEFAULT_ACTION_COLOR, simpleActionGridPane, 0, 0);
        GameInstructionIconUtil.createSVGIconStackPane(DIRECT_FLIGHT_ACTION_SVG, DEFAULT_ACTION_COLOR, simpleActionGridPane, 0, 1);
        GameInstructionIconUtil.createSVGIconStackPane(CHARTER_FLIGHT_ACTION_SVG, DEFAULT_ACTION_COLOR, simpleActionGridPane, 0, 2);
        GameInstructionIconUtil.createSVGIconStackPane(SHUTTLE_FLIGHT_ACTION_SVG, DEFAULT_ACTION_COLOR, simpleActionGridPane, 0, 3);
        GameInstructionIconUtil.createSVGIconStackPane(WAIVE_ACTION_SVG, WAIVE_ACTION_COLOR, simpleActionGridPane, 0, 4);
        GameInstructionIconUtil.createSVGIconStackPane(RESEARCH_LAB_SVG, DEFAULT_ACTION_COLOR, advancedActionGridPane, 0, 0);
        GameInstructionIconUtil.createSVGIconStackPane(DISCOVER_ANTIDOTE_SVG, DEFAULT_ACTION_COLOR, advancedActionGridPane, 0, 1);
        GameInstructionIconUtil.createSVGIconStackPane(CURE_PLAGUE_SVG, DEFAULT_ACTION_COLOR, advancedActionGridPane, 0, 2);
        GameInstructionIconUtil.createSVGIconStackPane(SHARE_KNOWLEDGE_SVG, DEFAULT_ACTION_COLOR, advancedActionGridPane, 0, 3);
    }
}
