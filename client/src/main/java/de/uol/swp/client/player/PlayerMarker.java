package de.uol.swp.client.player;

import de.uol.swp.client.marker.HighlightableMarker;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.player.Player;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import lombok.Getter;

/**
 * Represents the player marker
 *
 * @see PlayerMarkerPresenter
 */
@Getter
public class PlayerMarker extends HighlightableMarker {

    private static final double DEFAULT_HEAD_RADIUS = 10.0;
    private static final double DEFAULT_BODY_TOP_X = 10.0;
    private static final double DEFAULT_BODY_TOP_Y = -20.0;
    private static final double DEFAULT_BODY_BOTTOM_LEFT_X = 0.0;
    private static final double DEFAULT_BODY_BOTTOM_RIGHT_X = 20.0;
    private static final double DEFAULT_BODY_BOTTOM_Y = 20.0;

    private final double headRadius;
    private final double bodyTopX;
    private final double bodyTopY;
    private final double bodyBottomLeftX;
    private final double bodyBottomRightX;
    private final double bodyBottomY;

    private final Color playerColor;
    private final LoggedInUserProvider loggedInUserProvider;
    @Getter
    private final Player player;
    private final Game game;

    private Circle head;
    private Polygon body;

    public PlayerMarker(Color playerColor, double playerSize, LoggedInUserProvider loggedInUserProvider, Player player, Game game) {
        this.playerColor = playerColor;
        this.loggedInUserProvider = loggedInUserProvider;
        this.game = game;
        this.player = player;

        this.headRadius = DEFAULT_HEAD_RADIUS * playerSize;
        this.bodyTopX = DEFAULT_BODY_TOP_X * playerSize;
        this.bodyTopY = DEFAULT_BODY_TOP_Y * playerSize;
        this.bodyBottomLeftX = DEFAULT_BODY_BOTTOM_LEFT_X * playerSize;
        this.bodyBottomRightX = DEFAULT_BODY_BOTTOM_RIGHT_X * playerSize;
        this.bodyBottomY = DEFAULT_BODY_BOTTOM_Y * playerSize;

        initializeShapes();
        alignBodyToHead();
        addToGroup(body, head);
    }
    /**
     * Initializes the shapes of the player marker
     */
    private void initializeShapes() {
        createHead();
        createBody();
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
        this.head.setLayoutY(-headRadius);

        this.body.setLayoutX(-getWidth() / 2);
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