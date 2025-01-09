package de.uol.swp.client.research_laboratory;

import de.uol.swp.client.game.GameMapPresenter;
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

    private static final double DEFAULT_FRONT_BOTTOM_LEFT_X = 0.0;
    private static final double DEFAULT_FRONT_BOTTOM_LEFT_Y = 40.0;

    private static final double DEFAULT_FRONT_TOP_LEFT_X = 0.0;
    private static final double DEFAULT_FRONT_TOP_LEFT_Y = 20.0;

    private static final double DEFAULT_FRONT_BOTTOM_RIGHT_X = 20.0;
    private static final double DEFAULT_FRONT_BOTTOM_RIGHT_Y = 40.0;

    private static final double DEFAULT_FRONT_TOP_RIGHT_X = 20.0;
    private static final double DEFAULT_FRONT_TOP_RIGHT_Y = 20.0;

    private static final double DEFAULT_FRONT_TOP_CENTER_X = 10.0;
    private static final double DEFAULT_FRONT_TOP_CENTER_Y = 10.0;

    private static final double DEFAULT_BACK_TOP_CENTER_X = 30.0;
    private static final double DEFAULT_BACK_TOP_CENTER_Y = 0.0;

    private static final double DEFAULT_BACK_TOP_RIGHT_X = 40.0;
    private static final double DEFAULT_BACK_TOP_RIGHT_Y = 10.0;

    private static final double DEFAULT_BACK_BOTTOM_RIGHT_X = 40.0;
    private static final double DEFAULT_BACK_BOTTOM_RIGHT_Y = 30.0;

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

    private final Polygon frontSquareBase = new Polygon();
    private final Polygon frontTriangleRoof = new Polygon();
    private final Polygon sideSquareBase = new Polygon();
    private final Polygon sideSquareRoof = new Polygon();

    private final Color frontColor = Color.rgb(125, 125, 125);
    private final Color sideSquareBaseColor = Color.rgb(62, 62, 62);
    private final Color sideSquareRoofColor = Color.rgb(200, 200, 200);

    /**
     * Constructor for the ResearchLaboratoryMarker
     *
     * @param laboratorySize the size of the laboratory
     */
    public ResearchLaboratoryMarker(double laboratorySize) {
        initializeSizes(laboratorySize);
        initializeShapes();
        addToGroup(frontSquareBase, frontTriangleRoof, sideSquareBase, sideSquareRoof);
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
        createFrontSquareBase();
        createFrontTriangleRoof();
        createSideSquareBase();
        createSideSquareRoof();
    }

    /**
     * Creates the front square base of the laboratory
     */
    private void createFrontSquareBase() {
        frontSquareBase.getPoints().addAll(
                frontBottomLeftX, frontBottomLeftY,
                frontBottomRightX, frontBottomRightY,
                frontTopRightX, frontTopRightY,
                frontTopLeftX, frontTopLeftY
        );
        frontSquareBase.setFill(frontColor);
    }

    /**
     * Creates the front triangle roof of the laboratory
     */
    private void createFrontTriangleRoof() {
        frontTriangleRoof.getPoints().addAll(
                frontTopLeftX, frontTopLeftY,
                frontTopCenterX, frontTopCenterY,
                frontTopRightX, frontTopRightY
        );
        frontTriangleRoof.setFill(frontColor);
    }

    /**
     * Creates the side square base of the laboratory
     */
    private void createSideSquareBase() {
        sideSquareBase.getPoints().addAll(
                frontBottomRightX, frontBottomRightY,
                backBottomRightX, backBottomRightY,
                backTopRightX, backTopRightY,
                frontTopRightX, frontTopRightY
        );
        sideSquareBase.setFill(sideSquareBaseColor);
    }

    /**
     * Creates the side square roof of the laboratory
     */
    private void createSideSquareRoof() {
        sideSquareRoof.getPoints().addAll(
                frontTopRightX, frontTopRightY,
                backTopRightX, backTopRightY,
                backTopCenterX, backTopCenterY,
                frontTopCenterX, frontTopCenterY
        );
        sideSquareRoof.setFill(sideSquareRoofColor);
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
