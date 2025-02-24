package de.uol.swp.client.marker;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.util.ColorService;
import de.uol.swp.client.util.NodeBindingUtils;
import de.uol.swp.client.util.exception.NodeNotFoundException;
import de.uol.swp.common.marker.LevelableMarker;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.List;

/**
 * Displays all levels of a given {@link LevelableMarker} in a given {@link StackPane} and shows the current level.
 */
public class LevelableMarkerPresenter extends AbstractPresenter {

    protected final LevelableMarker levelableMarker;
    private final StackPane stackPane;
    protected final GridPane gridPane;
    private final Pane levelIndicatorPane;
    private final double mainColumnWidthPercentage;
    protected final int numberOfRows;
    private final List<Color> colorList;

    private static final double DEFAULT_MAIN_COLUMN_WIDTH_PERCENTAGE = 50;

    /**
     * Constructor
     * <p>
     * Uses the {@link #DEFAULT_MAIN_COLUMN_WIDTH_PERCENTAGE} as {@link #mainColumnWidthPercentage}.
     *
     * @param stackPane       The {@link StackPane} that will display {@link #gridPane} containing the levelable marker.
     * @param levelableMarker The {@link LevelableMarker} that will be represented by this presenter.
     * @param colorList       A {@link List} of {@link Color}s used to create a gradient background for the displayed
     *                        levels. If the list is empty, {@link Color#TRANSPARENT} will be used as background.
     */
    public LevelableMarkerPresenter(StackPane stackPane, LevelableMarker levelableMarker, List<Color> colorList) {
        this(stackPane, levelableMarker, DEFAULT_MAIN_COLUMN_WIDTH_PERCENTAGE, colorList);
    }

    /**
     * Constructor
     *
     * @param stackPane                 The {@link StackPane} that will display {@link #gridPane} containing
     *                                  the levelable marker.
     * @param levelableMarker           The {@link LevelableMarker} that will be represented by this presenter.
     * @param mainColumnWidthPercentage The width percentage of {@link #gridPane}'s main column.
     *                                  If the given value is less than or equal to 0, or greater than or equal to 100,
     *                                  the {@link #DEFAULT_MAIN_COLUMN_WIDTH_PERCENTAGE} will be used instead.
     * @param colorList                 A {@link List} of {@link Color}s used to create a gradient background for the
     *                                  displayed levels. If the list is empty, {@link Color#TRANSPARENT} will be used
     *                                  as background.
     */
    public LevelableMarkerPresenter(StackPane stackPane, LevelableMarker levelableMarker, double mainColumnWidthPercentage, List<Color> colorList) {
        this.levelableMarker = levelableMarker;
        this.stackPane = stackPane;
        this.gridPane = new GridPane();
        this.levelIndicatorPane = new Pane();

        this.mainColumnWidthPercentage = (mainColumnWidthPercentage <= 0 || mainColumnWidthPercentage >= 100)
                ? DEFAULT_MAIN_COLUMN_WIDTH_PERCENTAGE : mainColumnWidthPercentage;

        this.numberOfRows = levelableMarker.getNumberOfLevels();
        this.colorList = colorList.isEmpty() ? List.of(Color.TRANSPARENT) : colorList;

        initializeGridPane();
    }

    /**
     * Initializes the {@link #gridPane}.
     */
    private void initializeGridPane() {
        stackPane.getChildren().add(gridPane);

        addGridPaneConstraints();
        fillGridPaneCells();
        initializeLevelIndicatorPane();
    }

    /**
     * Adds {@link RowConstraints} and {@link ColumnConstraints} to the {@link #gridPane}.
     * <p>
     * Creates 3 columns and the amount of rows specified by {@link #numberOfRows}. The middle column has the width
     * specified by {@link #mainColumnWidthPercentage}. The remaining width is split equally between both side columns.
     */
    private void addGridPaneConstraints() {
        double sideColumnWidthPercentage = (100 - mainColumnWidthPercentage) / 2;

        ColumnConstraints sideColumnConstraint = new ColumnConstraints();
        sideColumnConstraint.setPercentWidth(sideColumnWidthPercentage);
        ColumnConstraints mainColumnConstraints = new ColumnConstraints();
        mainColumnConstraints.setPercentWidth(mainColumnWidthPercentage);
        gridPane.getColumnConstraints().addAll(sideColumnConstraint, mainColumnConstraints, sideColumnConstraint);

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / numberOfRows);
            gridPane.getRowConstraints().add(row);
        }
    }

    /**
     * Fills all cells of {@link #gridPane} with {@link Pane}s and initializes the main column.
     *
     * @see #initializeMainColumnCell(Pane, int)
     */
    private void fillGridPaneCells() {
        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            for (int columnIndex = 0; columnIndex < 3; columnIndex++) {
                if (columnIndex == 1) {
                    Pane cell = new Pane();
                    gridPane.add(cell, columnIndex, rowIndex);
                    initializeMainColumnCell(cell, rowIndex);
                }
            }
        }
    }

    /**
     * Initiates the given {@link Pane} from {@link #gridPane}'s main column by adding the background color and
     * level number.
     *
     * @param cell     The {@link Pane} from {@link #gridPane}'s main column, that will be initiated.
     * @param rowIndex The row index of the given {@link Pane} within {@link #gridPane}.
     */
    private void initializeMainColumnCell(Pane cell, int rowIndex) {
        addMainColumnCellBackgroundColor(cell, rowIndex);
        addMainColumnCellLevelNumber(cell, rowIndex);
    }

    /**
     * Adds a background color and border to the given {@link Pane} using a {@link Rectangle}.
     * The color is determined by the row index, where the colors are interpolated from {@link #colorList}.
     *
     * @param cell     The {@link Pane} where the background color and border will be applied.
     * @param rowIndex The row index of the given {@link Pane} within {@link #gridPane} used to determine the
     *                 interpolation progress of the color gradient.
     * @see ColorService#interpolateColor(Color, Color, double)
     */
    private void addMainColumnCellBackgroundColor(Pane cell, int rowIndex) {
        Rectangle backgroundRectangle = new Rectangle();
        double progress = 1 - (double) rowIndex / (numberOfRows - 1);
        Color backgroundColor;

        int numberOfColors = colorList.size();
        int startColorIndex = (int) Math.floor(progress * (numberOfColors - 1));
        int endColorIndex = Math.min(startColorIndex + 1, numberOfColors - 1);

        double segmentProgress = (progress * (numberOfColors - 1)) - startColorIndex;

        backgroundColor = ColorService.interpolateColor(colorList.get(startColorIndex), colorList.get(endColorIndex), segmentProgress);

        backgroundRectangle.setFill(backgroundColor);
        backgroundRectangle.setStroke(Color.BLACK);
        backgroundRectangle.setStrokeWidth(2);
        backgroundRectangle.widthProperty().bind(cell.widthProperty());
        backgroundRectangle.heightProperty().bind(cell.heightProperty());
        cell.getChildren().add(backgroundRectangle);
    }

    /**
     * Adds the level number to the given {@link Pane}.
     *
     * @param cell     The {@link Pane} to which the level number will be added to.
     * @param rowIndex The row index of the given {@link Pane} used to get the associated level number.
     */
    private void addMainColumnCellLevelNumber(Pane cell, int rowIndex) {
        StackPane levelNumberStackPane = new StackPane();

        cell.getChildren().add(levelNumberStackPane);
        NodeBindingUtils.bindRegionSizeToStackPanePrefSize(cell, levelNumberStackPane);

        Text text = new Text(String.valueOf(levelableMarker.getLevelValue(numberOfRows - rowIndex - 1)));
        text.setFont(Font.font(null, FontWeight.BOLD, 10));

        NodeBindingUtils.bindRegionSizeToTextFont(levelNumberStackPane, text, 0.7);

        levelNumberStackPane.getChildren().add(text);
    }

    /**
     * Initializes the {@link #levelIndicatorPane}.
     */
    private void initializeLevelIndicatorPane() {
        StackPane levelIndicatorStackPane = new StackPane();
        Polygon levelIndicator = createLevelIndicatorPolygon();

        levelIndicatorStackPane.getChildren().add(levelIndicator);
        levelIndicatorPane.getChildren().add(levelIndicatorStackPane);
        gridPane.add(levelIndicatorPane, 0, 0);

        double scalingFactor = 0.6;
        NodeBindingUtils.bindRegionSizeToNode(levelIndicatorPane, levelIndicator, scalingFactor);
        NodeBindingUtils.bindRegionSizeToStackPanePrefSize(levelIndicatorPane, levelIndicatorStackPane);

        updateLevelIndicator(levelableMarker.getLevel());
    }

    /**
     * Returns the first {@link Node} found at the given row and column of the {@link #gridPane}.
     *
     * @param rowIndex    The row index within {@link #gridPane}.
     * @param columnIndex The column index within {@link #gridPane}.
     * @return The first {@link Node} found in the given row and column.
     * @throws NodeNotFoundException when no {@link Node} has been found in the given row and column of {@link #gridPane}.
     */
    protected Node getNodeFromGridPane(int rowIndex, int columnIndex) throws NodeNotFoundException {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getRowIndex(node) == rowIndex && GridPane.getColumnIndex(node) == columnIndex) {
                return node;
            }
        }
        throw new NodeNotFoundException(
                String.format("No node found in row %d and column %d of gridPane", rowIndex, columnIndex)
        );
    }

    /**
     * Creates a {@link Polygon} that is used to indicate the current level.
     *
     * @return A {@link Polygon} that shows a triangle pointing to the right.
     */
    private Polygon createLevelIndicatorPolygon() {
        Polygon levelIndicator = new Polygon();
        levelIndicator.getPoints().addAll(
                0.0, 0.0,
                1.0, 0.5,
                0.0, 1.0
        );
        levelIndicator.setFill(Color.BLACK);
        return levelIndicator;
    }

    /**
     * Updates the level indicator position.
     */
    public void updateLevelIndicator(int currentLevel) {
        int rowIndex = numberOfRows - 1 - currentLevel;
        GridPane.setRowIndex(levelIndicatorPane, rowIndex);
    }
}
