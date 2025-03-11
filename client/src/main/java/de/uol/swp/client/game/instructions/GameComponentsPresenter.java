package de.uol.swp.client.game.instructions;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.card.InfectionCardsOverviewPresenter;
import de.uol.swp.client.card.PlayerCardsOverviewPresenter;
import de.uol.swp.client.marker.LevelableMarkerPresenter;
import de.uol.swp.client.marker.OutbreakMarkerPresenter;
import de.uol.swp.client.util.ColorService;
import de.uol.swp.common.marker.InfectionMarker;
import de.uol.swp.common.marker.OutbreakMarker;
import de.uol.swp.common.role.RoleColors;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.List;

/**
 * Presents the game components tab within the {@link GameInstructionsPresenter}.
 *
 */
public class GameComponentsPresenter extends AbstractPresenter {

    @FXML
    private GridPane gridPane;
    @FXML
    private StackPane playerMarkerPane;
    @FXML
    private StackPane researchLaboratoryMarkerStackPane;
    @FXML
    private StackPane plagueCubeIconStackPane;
    @FXML
    private StackPane outbreakMarkerStackPane;
    @FXML
    private GridPane outbreakMarkerGridPane;
    @FXML
    private StackPane infectionMarkerStackPane;
    @FXML
    private GridPane infectionMarkerGridPane;
    @FXML
    private StackPane playerCardStackPane;
    @FXML
    private StackPane infectionCardStackPane;

    private static final String SVG_PATH_PREFIX = "action/";
    private static final String FLASK_SVG = SVG_PATH_PREFIX + "flask.svg";
    private static final Color PLAYER_MARKER_COLOR = ColorService.convertColorToJavaFXColor(RoleColors.ARZT_COLOR_ORANGE);

    /**
     * Initializes all icon {@link StackPane}s of the {@link GameComponentsPresenter}.
     */
    @FXML
    public void initialize() {
        GameInstructionIconUtil.initializePlayerMarkerStackPane(playerMarkerPane, PLAYER_MARKER_COLOR);
        GameInstructionIconUtil.initializeResearchLaboratoryIconStackPane(researchLaboratoryMarkerStackPane);
        GameInstructionIconUtil.initializePlagueCubeIconStackPane(plagueCubeIconStackPane);
        GameInstructionIconUtil.createSVGIconStackPane(FLASK_SVG, Color.BLACK, gridPane, 2, 1);
        initializeOutbreakMarkerStackPane();
        initializeInfectionMarkerStackPane();
        GameInstructionIconUtil.initializeCardIconStackPane(playerCardStackPane, PlayerCardsOverviewPresenter.ICON_COLOR);
        GameInstructionIconUtil.initializeCardIconStackPane(infectionCardStackPane, InfectionCardsOverviewPresenter.ICON_COLOR);
    }

    /**
     * Initializes the {@link OutbreakMarker} {@link StackPane} using {@link OutbreakMarkerPresenter}.
     */
    private void initializeOutbreakMarkerStackPane() {
        GameInstructionIconUtil.initializeSquareStackPaneSize(outbreakMarkerGridPane);

        List<Integer> outbreakLevels = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8);
        OutbreakMarker outbreakMarker = new OutbreakMarker(outbreakLevels);
        List<javafx.scene.paint.Color> colorList = List.of(
                javafx.scene.paint.Color.GREEN,
                javafx.scene.paint.Color.YELLOW,
                javafx.scene.paint.Color.RED
        );

        new OutbreakMarkerPresenter(outbreakMarkerStackPane, outbreakMarker, colorList);
    }

    /**
     * Initializes the {@link InfectionMarker} {@link StackPane} using {@link LevelableMarkerPresenter}.
     */
    private void initializeInfectionMarkerStackPane() {
        GameInstructionIconUtil.initializeSquareStackPaneSize(infectionMarkerGridPane);

        List<Integer> infectionLevels = Arrays.asList(2, 2, 2, 3, 3, 4, 4);
        InfectionMarker infectionMarker = new InfectionMarker(infectionLevels);
        List<javafx.scene.paint.Color> colorList = List.of(
                javafx.scene.paint.Color.DARKOLIVEGREEN,
                javafx.scene.paint.Color.YELLOW,
                javafx.scene.paint.Color.ORANGE
        );

        new LevelableMarkerPresenter(infectionMarkerStackPane, infectionMarker, colorList);
    }
}
