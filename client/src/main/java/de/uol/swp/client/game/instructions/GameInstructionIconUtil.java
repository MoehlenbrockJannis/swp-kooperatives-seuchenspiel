package de.uol.swp.client.game.instructions;

import de.uol.swp.client.card.CardIcon;
import de.uol.swp.client.map.research_laboratory.ResearchLaboratoryMarker;
import de.uol.swp.client.plague.PlagueCubeIcon;
import de.uol.swp.client.player.PlayerMarker;
import de.uol.swp.client.util.FileLoader;
import de.uol.swp.client.util.ScalableSVGStackPane;
import de.uol.swp.common.plague.Plague;
import javafx.scene.Group;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Utility class that provides methods to create icons used within the {@link GameInstructionsPresenter}.
 *
 */
public class GameInstructionIconUtil {

    private static final double PLAYER_SIZE = 0.75;
    private static final double SCALE_FACTOR = 0.025;

    /**
     * Private constructor to prevent instantiation
     */
    private GameInstructionIconUtil() {
    }

    /**
     * Initializes a given {@link StackPane} with a {@link PlayerMarker} icon in the given {@link Color}.
     *
     * @param stackPane The {@link StackPane} that will display a {@link PlayerMarker}.
     * @param color     The {@link Color} used for the {@link PlayerMarker}.
     */
    public static void initializePlayerMarkerStackPane(StackPane stackPane, Color color) {
        initializeSquareStackPaneSize(stackPane);

        PlayerMarker playerMarker = new PlayerMarker(color, PLAYER_SIZE, null, null, null);
        stackPane.getChildren().add(playerMarker);
        bindGroupSizeToSquarePane(stackPane, playerMarker, SCALE_FACTOR);
    }

    /**
     * Initializes a given {@link StackPane} with a {@link ResearchLaboratoryMarker} icon.
     *
     * @param stackPane The {@link StackPane} that will display a {@link ResearchLaboratoryMarker}.
     */
    public static void initializeResearchLaboratoryIconStackPane(StackPane stackPane) {
        GameInstructionIconUtil.initializeSquareStackPaneSize(stackPane);

        ResearchLaboratoryMarker researchLaboratoryMarker = new ResearchLaboratoryMarker(0.75);
        stackPane.getChildren().add(researchLaboratoryMarker);
        GameInstructionIconUtil.bindGroupSizeToSquarePane(stackPane, researchLaboratoryMarker, 0.025);
    }

    /**
     * Initializes a given {@link StackPane} with a {@link PlagueCubeIcon}.
     *
     * @param stackPane The {@link StackPane} that will display a {@link PlagueCubeIcon}.
     */
    public static void initializePlagueCubeIconStackPane(StackPane stackPane) {
        GameInstructionIconUtil.initializeSquareStackPaneSize(stackPane);

        Plague plague = new Plague("", new de.uol.swp.common.util.Color(40, 138, 204));
        PlagueCubeIcon plagueCubeIcon = new PlagueCubeIcon(40, false, plague);
        stackPane.getChildren().add(plagueCubeIcon);
        GameInstructionIconUtil.bindGroupSizeToSquarePane(stackPane, plagueCubeIcon, 0.018);
    }

    /**
     * Initializes the size of a square {@link Pane}.
     * <p>
     * Binds the height properties of the given {@link Pane} to its width properties to convert it into a square shape.
     *
     * @param pane The {@link Pane} whose height properties will be bound to its width properties.
     */
    public static void initializeSquareStackPaneSize(Pane pane) {
        pane.minHeightProperty().bind(pane.widthProperty());
        pane.prefHeightProperty().bind(pane.widthProperty());
        pane.maxHeightProperty().bind(pane.widthProperty());
    }

    /**
     * Binds the given {@link Group} size to the given square {@link StackPane}.
     *
     * @param stackPane   The square {@link StackPane} whose size will be bound to the scaling of the {@link Group}.
     * @param group       The {@link Group} whose scaling and positioning will be bound to the {@link StackPane}.
     * @param scaleFactor A factor to control the scaling of the {@link Group} based on the {@link StackPane}'s size.
     */
    public static void bindGroupSizeToSquarePane(StackPane stackPane, Group group, double scaleFactor) {
        group.scaleXProperty().bind(stackPane.widthProperty().multiply(scaleFactor));
        group.scaleYProperty().bind(stackPane.widthProperty().multiply(scaleFactor));
    }

    /**
     * Creates the SVG icon {@link StackPane} using {@link ScalableSVGStackPane} and add it to the given {@link GridPane}.
     *
     * @param svgPath        The {@link String} path to the SVG.
     * @param color          The {@link Color} used as the SVG's fill color.
     * @param gridPane       The {@link GridPane} to which the created {@link ScalableSVGStackPane} will be added to.
     * @param gridPaneColumn The {@link GridPane}'s column index in which the created {@link ScalableSVGStackPane} will be added to.
     * @param gridPaneRow    The {@link GridPane}'s row index in which the created {@link ScalableSVGStackPane} will be added to.
     */
    public static void createSVGIconStackPane(String svgPath, Color color, GridPane gridPane, int gridPaneColumn, int gridPaneRow) {
        ScalableSVGStackPane svgStackPane = new ScalableSVGStackPane(FileLoader.readImageFile(svgPath), 0.2, color);
        initializeSquareStackPaneSize(svgStackPane);
        gridPane.add(svgStackPane, gridPaneColumn, gridPaneRow);
    }

    /**
     * Initializes the {@link CardIcon} {@link StackPane} with the given {@link Color}.
     *
     * @param stackPane The {@link StackPane} that will receive a {@link CardIcon}.
     * @param color     The {@link Color} used for the created {@link CardIcon}.
     */
    public static void initializeCardIconStackPane(StackPane stackPane, Color color) {
        initializeSquareStackPaneSize(stackPane);

        Rectangle cardIcon = new CardIcon(color, stackPane);
        stackPane.getChildren().add(cardIcon);
        cardIcon.widthProperty().bind(stackPane.widthProperty().multiply(CardIcon.getWIDTH_MULTIPLIER() * 1.3));
        cardIcon.heightProperty().bind(stackPane.heightProperty().multiply(CardIcon.getHEIGHT_MULTIPLIER() * 1.3));
    }
}
