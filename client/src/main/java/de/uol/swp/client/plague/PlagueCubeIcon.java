package de.uol.swp.client.plague;

import de.uol.swp.client.util.ColorService;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates a single PlagueCube for the {@link PlagueCubeMarker}
 */
public class PlagueCubeIcon extends Group {

    @Getter
    private final double width;
    @Getter
    private final double height;
    @Getter
    private final double depth;

    private final double xStartCoordinate;
    private final double yStartCoordinate;

    private final Color color;
    private static final double COLOR_ADJUSTMENT_FACTOR = 0.3;

    @Getter
    private final boolean isForeignPlagueCube;

    /**
     * Constructor
     *
     * @param size  the size of the {@link PlagueCubeIcon}
     * @param color the {@link Color} used for the {@link PlagueCubeIcon}
     * @param isForeignPlagueCube whether this {@link PlagueCubeIcon} represents a foreign plague cube or not
     */
    public PlagueCubeIcon(double size, Color color, boolean isForeignPlagueCube) {
        this(size, color, isForeignPlagueCube, -1);
    }

    /**
     * Constructor
     *
     * @param size  the size of the {@link PlagueCubeIcon}
     * @param color the {@link Color} used for the {@link PlagueCubeIcon}
     * @param isForeignPlagueCube whether this {@link PlagueCubeIcon} represents a foreign plague cube or not
     * @param numberOfPlagueCubes the amount of {@link de.uol.swp.common.plague.PlagueCube} displayed on the counter
     */
    public PlagueCubeIcon(double size, Color color, boolean isForeignPlagueCube, int numberOfPlagueCubes) {
        this.width = size;
        this.height = size * 0.6;
        this.depth = size * 0.5;

        xStartCoordinate = 0.0;
        yStartCoordinate = (-height + depth) / 2;

        this.color = color;

        this.isForeignPlagueCube = isForeignPlagueCube;

        createPlagueCube();

        if (numberOfPlagueCubes > -1) {
           addPlagueCubeCounter(numberOfPlagueCubes);
        }
    }

    /**
     * Returns the total height of the {@link PlagueCubeIcon} ({@link #height} + {@link #depth})
     *
     * @return the total height of the {@link PlagueCubeIcon}
     */
    public double getTotalHeight() {
        return height + depth;
    }

    /**
     * Creates and adds all sides of the {@link PlagueCubeIcon}
     */
    private void createPlagueCube() {
        addPolygon(getLeftSideCoordinates(), 0);
        addPolygon(getRightSideCoordinates(), -COLOR_ADJUSTMENT_FACTOR);
        addPolygon(getTopSideCoordinates(), COLOR_ADJUSTMENT_FACTOR);
    }

    /**
     * Returns a {@link List} of {@link Point2D} with all coordinates required to create the
     * left side {@link Polygon} of the {@link PlagueCubeIcon}
     *
     * @return {@link List} of {@link Point2D} with all coordinates for the left side {@link Polygon}
     */
    private List<Point2D> getLeftSideCoordinates() {
        List<Point2D> coordinates = new ArrayList<>();

        coordinates.add(new Point2D(xStartCoordinate, yStartCoordinate));
        coordinates.add(new Point2D(xStartCoordinate, yStartCoordinate + height));
        coordinates.add(new Point2D(xStartCoordinate - width / 2, yStartCoordinate + height - depth / 2));
        coordinates.add(new Point2D(xStartCoordinate - width / 2, yStartCoordinate - depth / 2));

        return coordinates;
    }

    /**
     * Returns a {@link List} of {@link Point2D} with all coordinates required to create the
     * right side {@link Polygon} of the {@link PlagueCubeIcon}
     *
     * @return {@link List} of {@link Point2D} with all coordinates for the right side {@link Polygon}
     */
    private List<Point2D> getRightSideCoordinates() {
        List<Point2D> coordinates = new ArrayList<>();

        coordinates.add(new Point2D(xStartCoordinate, yStartCoordinate));
        coordinates.add(new Point2D(xStartCoordinate, yStartCoordinate + height));
        coordinates.add(new Point2D(xStartCoordinate + width / 2, yStartCoordinate + height - depth / 2));
        coordinates.add(new Point2D(xStartCoordinate + width / 2, yStartCoordinate - depth / 2));

        return coordinates;
    }

    /**
     * Returns a {@link List} of {@link Point2D} with all coordinates required to create the
     * top side {@link Polygon} of the {@link PlagueCubeIcon}
     *
     * @return {@link List} of {@link Point2D} with all coordinates for the top side {@link Polygon}
     */
    private List<Point2D> getTopSideCoordinates() {
        List<Point2D> coordinates = new ArrayList<>();

        coordinates.add(new Point2D(xStartCoordinate, yStartCoordinate));
        coordinates.add(new Point2D(xStartCoordinate - width / 2, yStartCoordinate - depth / 2));
        coordinates.add(new Point2D(xStartCoordinate, yStartCoordinate - depth));
        coordinates.add(new Point2D(xStartCoordinate + width / 2, yStartCoordinate - depth / 2));

        return coordinates;
    }

    /**
     * Creates a {@link Polygon} with the given coordinates and adds it to the {@link PlagueCubeIcon}
     *
     * @param coordinates           {@link List} of {@link Point2D} with all coordinates for the {@link Polygon}
     * @param colorAdjustmentFactor factor used to adjust the brightness of the {@link #color} of the {@link Polygon}
     * @see ColorService#adjustBrightness(Color, double)
     */
    private void addPolygon(List<Point2D> coordinates, double colorAdjustmentFactor) {
        Polygon polygon = new Polygon();

        for (Point2D point : coordinates) {
            polygon.getPoints().addAll(point.getX(), point.getY());
        }

        polygon.setFill(ColorService.adjustBrightness(color, colorAdjustmentFactor));
        polygon.setStroke(Color.BLACK);
        this.getChildren().add(polygon);
    }

    /**
     * Creates and adds a {@link javafx.scene.text.Text} to the {@link PlagueCubeIcon} to display the amount of cubes
     */
    private void addPlagueCubeCounter(int numberOfPlagueCubes) {
        Text counter = new Text(String.valueOf(numberOfPlagueCubes));

        counter.setFont(Font.font(null, FontWeight.BOLD, (height + depth) * 0.75));
        counter.setFill(Color.WHITE);
        counter.setStroke(Color.BLACK);
        counter.setStrokeType(StrokeType.OUTSIDE);

        double textWidth = counter.getLayoutBounds().getWidth();
        double textHeight = counter.getLayoutBounds().getHeight();

        double xPosition = -textWidth / 2;
        double yPosition = textHeight / 4;

        counter.setX(xPosition);
        counter.setY(yPosition);

        this.getChildren().add(counter);
    }
}
