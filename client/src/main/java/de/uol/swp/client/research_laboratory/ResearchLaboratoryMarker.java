package de.uol.swp.client.research_laboratory;

import de.uol.swp.client.game.GameMapPresenter;
import de.uol.swp.client.util.ColorService;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

/**
 * The ResearchLaboratoryMarker class is used to create a marker for the research laboratory on the game map.
 *
 * @author Silas van Thiel
 * @see GameMapPresenter
 * @since 04.11.2024
 */
public class ResearchLaboratoryMarker extends Group {

    private static final double DEFAULT_FRONT_BOTTOM_LEFT_X = -20.0;
    private static final double DEFAULT_FRONT_BOTTOM_LEFT_Y = 20.0;

    private static final double DEFAULT_FRONT_TOP_LEFT_X = -20.0;
    private static final double DEFAULT_FRONT_TOP_LEFT_Y = 0.0;

    private static final double DEFAULT_FRONT_BOTTOM_RIGHT_X = 0.0;
    private static final double DEFAULT_FRONT_BOTTOM_RIGHT_Y = 20.0;

    private static final double DEFAULT_FRONT_TOP_RIGHT_X = 0.0;
    private static final double DEFAULT_FRONT_TOP_RIGHT_Y = 0.0;

    private static final double DEFAULT_FRONT_TOP_CENTER_X = -10.0;
    private static final double DEFAULT_FRONT_TOP_CENTER_Y = -10.0;

    private static final double DEFAULT_BACK_TOP_CENTER_X = 10.0;
    private static final double DEFAULT_BACK_TOP_CENTER_Y = -20.0;

    private static final double DEFAULT_BACK_TOP_RIGHT_X = 20.0;
    private static final double DEFAULT_BACK_TOP_RIGHT_Y = -10.0;

    private static final double DEFAULT_BACK_BOTTOM_RIGHT_X = 20.0;
    private static final double DEFAULT_BACK_BOTTOM_RIGHT_Y = 10.0;

    private double frontBottomLeftX;
    private double frontBottomLeftY;

    private double frontTopLeftX;
    private double frontTopLeftY;

    private double frontBottomRightX;
    private double frontBottomRightY;

    private double frontTopRightX;
    private double frontTopRightY;

    private double frontTopCenterX;
    private double frontTopCenterY;

    private double backTopCenterX;
    private double backTopCenterY;

    private double backTopRightX;
    private double backTopRightY;

    private double backBottomRightX;
    private double backBottomRightY;

    private final Polygon frontBase = new Polygon();
    private final Polygon sideBase = new Polygon();
    private final Polygon roof = new Polygon();

    private static final Color COLOR = Color.rgb(192, 192, 192);
    private static final double COLOR_ADJUSTMENT_FACTOR = 0.3;

    /**
     * Constructor for the ResearchLaboratoryMarker
     *
     * @param laboratorySize the size of the laboratory
     */
    public ResearchLaboratoryMarker(double laboratorySize) {
        initializeSizes(laboratorySize);
        initializeShapes();
        addToGroup(frontBase, sideBase, roof);
    }

    /**
     * Initializes the sizes of the laboratory
     *
     * @param laboratorySize the size of the laboratory
     */
    private void initializeSizes(double laboratorySize) {
        this.frontBottomLeftX = DEFAULT_FRONT_BOTTOM_LEFT_X * laboratorySize;
        this.frontBottomLeftY = DEFAULT_FRONT_BOTTOM_LEFT_Y * laboratorySize;

        this.frontTopLeftX = DEFAULT_FRONT_TOP_LEFT_X * laboratorySize;
        this.frontTopLeftY = DEFAULT_FRONT_TOP_LEFT_Y * laboratorySize;

        this.frontBottomRightX = DEFAULT_FRONT_BOTTOM_RIGHT_X * laboratorySize;
        this.frontBottomRightY = DEFAULT_FRONT_BOTTOM_RIGHT_Y * laboratorySize;

        this.frontTopRightX = DEFAULT_FRONT_TOP_RIGHT_X * laboratorySize;
        this.frontTopRightY = DEFAULT_FRONT_TOP_RIGHT_Y * laboratorySize;

        this.frontTopCenterX = DEFAULT_FRONT_TOP_CENTER_X * laboratorySize;
        this.frontTopCenterY = DEFAULT_FRONT_TOP_CENTER_Y * laboratorySize;

        this.backTopCenterX = DEFAULT_BACK_TOP_CENTER_X * laboratorySize;
        this.backTopCenterY = DEFAULT_BACK_TOP_CENTER_Y * laboratorySize;

        this.backTopRightX = DEFAULT_BACK_TOP_RIGHT_X * laboratorySize;
        this.backTopRightY = DEFAULT_BACK_TOP_RIGHT_Y * laboratorySize;

        this.backBottomRightX = DEFAULT_BACK_BOTTOM_RIGHT_X * laboratorySize;
        this.backBottomRightY = DEFAULT_BACK_BOTTOM_RIGHT_Y * laboratorySize;
    }

    /**
     * Initializes the shapes of the laboratory
     */
    private void initializeShapes() {
        createFrontBase();
        createSideBase();
        createRoof();
    }

    /**
     * Creates the front base of the laboratory
     */
    private void createFrontBase() {
        frontBase.getPoints().addAll(
                frontBottomLeftX, frontBottomLeftY,
                frontBottomRightX, frontBottomRightY,
                frontTopRightX, frontTopRightY,
                frontTopCenterX, frontTopCenterY,
                frontTopLeftX, frontTopLeftY
        );
        frontBase.setFill(COLOR);
        frontBase.setStroke(Color.BLACK);
    }

    /**
     * Creates the side base of the laboratory
     */
    private void createSideBase() {
        sideBase.getPoints().addAll(
                frontBottomRightX, frontBottomRightY,
                backBottomRightX, backBottomRightY,
                backTopRightX, backTopRightY,
                frontTopRightX, frontTopRightY
        );
        sideBase.setFill(ColorService.adjustBrightness(COLOR, -COLOR_ADJUSTMENT_FACTOR));
        sideBase.setStroke(Color.BLACK);
    }

    /**
     * Creates the roof of the laboratory
     */
    private void createRoof() {
        roof.getPoints().addAll(
                frontTopRightX, frontTopRightY,
                backTopRightX, backTopRightY,
                backTopCenterX, backTopCenterY,
                frontTopCenterX, frontTopCenterY
        );
        roof.setFill(ColorService.adjustBrightness(COLOR, COLOR_ADJUSTMENT_FACTOR));
        roof.setStroke(Color.BLACK);
    }

    /**
     * Adds the given shapes to the group
     *
     * @param shapes The shapes to be added
     */
    private void addToGroup(Shape... shapes) {
        this.getChildren().addAll(shapes);
    }
}
