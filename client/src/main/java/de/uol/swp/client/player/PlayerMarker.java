package de.uol.swp.client.player;

import de.uol.swp.client.game.GameMapPresenter;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.user.User;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import java.util.Objects;

/**
 * Represents the player marker on the game map
 *
 * @author Silas van Thiel
 * @see GameMapPresenter
 * @since 2024-09-28
 */
public class PlayerMarker extends Group {

    private static final double DEFAULT_HEAD_RADIUS = 10.0;
    private static final double DEFAULT_BODY_TOP_X = 10.0;
    private static final double DEFAULT_BODY_TOP_Y = -20.0;
    private static final double DEFAULT_BODY_BOTTOM_LEFT_X = 0.0;
    private static final double DEFAULT_BODY_BOTTOM_RIGHT_X = 20.0;
    private static final double DEFAULT_BODY_BOTTOM_Y = 20.0;

    private double headRadius;
    private double bodyTopX;
    private double bodyTopY;
    private double bodyBottomLeftX;
    private double bodyBottomRightX;
    private double bodyBottomY;

    private final Color playerColor;
    private final LoggedInUserProvider loggedInUserProvider;
    private final Player player;

    private Circle head;
    private Polygon body;

    /**
     * Constructor
     */
    public PlayerMarker(Color playerColor, double playerSize, LoggedInUserProvider loggedInUserProvider, Player player) {
        this.playerColor = playerColor;
        this.loggedInUserProvider = loggedInUserProvider;
        this.player = player;
        initializeSizes(playerSize);
        initializeShapes();
        initializeMouseEvents();
        alignBodyToHead();
        addToGroup(body, head);
    }

    /**
     * Initializes the mouse events of the player marker
     */
    private void initializeMouseEvents() {
        initializeHoverEvents();
        initializeClickEvent();
    }

    /**
     * Initializes the click event of the player marker
     */
    private void initializeHoverEvents() {
        this.setOnMouseEntered(event -> {
            if (isEventTriggeredByBoundPlayer()) {
                this.setOpacity(0.5);
            }
        });

        this.setOnMouseExited(event -> {
            if (isEventTriggeredByBoundPlayer()) {
                this.setOpacity(1.0);
            }
        });
    }

    /**
     * Initializes the click event of the player marker
     */
    private void initializeClickEvent() {
        this.setOnMouseClicked(event -> {
            if (isEventTriggeredByBoundPlayer()) {
                handleMarkerClick();
            }
        });
    }

    /**
     * Handles the click event of the player marker
     */
    private void handleMarkerClick() {
        // TODO implement marker click logic
    }

    /**
     * @return True if the event is triggered by the player bound to the player marker
     */
    private boolean isEventTriggeredByBoundPlayer() {
        User user = loggedInUserProvider.get();
        return Objects.equals(user.getUsername(), player.getName());
    }

    /**
     * Initializes the shapes of the player marker
     */
    private void initializeShapes() {
        createHead();
        createBody();
    }

    /**
     * Calculates the size of the player marker
     *
     * @param playerSize The size of the player marker
     */
    private void initializeSizes(double playerSize) {
        this.headRadius = DEFAULT_HEAD_RADIUS * playerSize;
        this.bodyTopX = DEFAULT_BODY_TOP_X * playerSize;
        this.bodyTopY = DEFAULT_BODY_TOP_Y * playerSize;
        this.bodyBottomLeftX = DEFAULT_BODY_BOTTOM_LEFT_X * playerSize;
        this.bodyBottomRightX = DEFAULT_BODY_BOTTOM_RIGHT_X * playerSize;
        this.bodyBottomY = DEFAULT_BODY_BOTTOM_Y * playerSize;
    }

    /**
     * Adds the given shapes to the group
     *
     * @param shapes The shapes to be added
     */
    private void addToGroup(Shape... shapes) {
        this.getChildren().addAll(shapes);
    }

    /**
     * Aligns the body to the head
     */
    private void alignBodyToHead() {
        this.head.setLayoutX(headRadius);
        this.head.setLayoutY(headRadius);

        this.body.setLayoutX(0);
        this.body.setLayoutY(2 * headRadius);
    }

    /**
     * Creates the body of the player
     */
    private void createBody() {
        this.body = new Polygon();
        body.getPoints().addAll(
                bodyTopX, bodyTopY,
                bodyBottomLeftX, bodyBottomY,
                bodyBottomRightX, bodyBottomY
        );
        colorShape(body, playerColor);
    }

    /**
     * Creates the head of the player
     */
    private void createHead() {
        this.head = new Circle(headRadius);
        colorShape(head, playerColor);
    }

    /**
     * Colors the given shape with the given color
     *
     * @param shape The shape to be colored
     * @param color The color to be used
     */
    private void colorShape(Shape shape, Color color) {
        shape.setFill(color);
        shape.setStroke(Color.BLACK);
    }

    /**
     * @return The height of the player marker (from the top of the head to the bottom of the body)
     */
    public double getHeight() {
        return Math.abs(bodyBottomY) + Math.abs(bodyBottomY);
    }

    /**
     * @return The width of the player marker
     */
    public double getWidth() {
        return Math.abs(bodyBottomLeftX) + Math.abs(bodyBottomRightX);
    }

}
