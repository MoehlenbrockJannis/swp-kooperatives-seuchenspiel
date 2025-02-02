package de.uol.swp.client.game;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.game.exception.NoIntersectionWithCoordinatesException;
import de.uol.swp.client.util.NodeBindingUtils;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.web.WebView;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates all connections between fields of a game and presents them on the {@link de.uol.swp.client.game.GameMapPresenter}.
 */
public class CityConnectionPanePresenter extends AbstractPresenter {

    private final Pane cityConnectionPane;

    private final WebView webView;

    private final double leftEdgeX;
    private final double rightEdgeX;
    private final double topEdgeY;
    private final double bottomEdgeY;


    /**
     * Constructor
     *
     * @param game    The {@link Game} for which connections will be created.
     * @param webView The {@link WebView} to which all connections will be bound to.
     */
    public CityConnectionPanePresenter(Game game, WebView webView, Pane cityConnectionPane) {
        this.webView = webView;
        this.cityConnectionPane = cityConnectionPane;

        this.leftEdgeX = GameMapPresenter.getSVG_VIEW_BOX_MIN_X();
        this.topEdgeY = GameMapPresenter.getSVG_VIEW_BOX_MIN_Y();
        this.rightEdgeX = GameMapPresenter.getSVG_VIEW_BOX_MAX_X();
        this.bottomEdgeY = GameMapPresenter.getSVG_VIEW_BOX_MAX_Y();

        createAllConnections(game.getFields());
    }

    /**
     * Creates all connections between neighboring {@link Field}s from the given {@link Field} list.
     *
     * @param fields The {@link Field} list for which all connections will be created.
     */
    private void createAllConnections(List<Field> fields) {
        List<Field> doneFields = new ArrayList<>();

        for (Field field : fields) {
            for (Field neighborField : field.getNeighborFields()) {
                if (!doneFields.contains(neighborField)) {
                    createSingleConnection(field, neighborField);
                }
            }
            doneFields.add(field);
        }
    }

    /**
     * Creates a connection between two given {@link Field}s.
     *
     * @param field1 The first field of the connection.
     * @param field2 The second field of the connection.
     */
    private void createSingleConnection(Field field1, Field field2) {
        double startX = field1.getXCoordinate();
        double startY = field1.getYCoordinate();
        double endX = field2.getXCoordinate();
        double endY = field2.getYCoordinate();

        createShortestLine(startX, startY, endX, endY);
    }

    /**
     * Creates the shortest line between two points on a toroidal map and adds it to the {@link #cityConnectionPane}.
     *
     * <p>
     * This method calculates the shortest path between two points on a map
     * that wraps around both horizontally and vertically, similar to a torus.
     * It adjusts the points if necessary to ensure the shortest distance
     * is used, handling cases where the path wraps around one or both map edges.
     * </p>
     *
     * @param x1 The x-coordinate of the starting point.
     * @param y1 The y-coordinate of the starting point.
     * @param x2 The x-coordinate of the ending point.
     * @param y2 The y-coordinate of the ending point.
     */
    private void createShortestLine(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;

        if (Math.abs(dx) > rightEdgeX / 2) {
            dx = dx > leftEdgeX ? dx - rightEdgeX : dx + rightEdgeX;
        }

        if (Math.abs(dy) > bottomEdgeY / 2) {
            dy = dy > topEdgeY ? dy - bottomEdgeY : dy + bottomEdgeY;
        }

        double wrappedX2 = x1 + dx;
        double wrappedY2 = y1 + dy;

        boolean wrapXRequired = wrappedX2 < leftEdgeX || wrappedX2 > rightEdgeX;
        boolean wrapYRequired = wrappedY2 < topEdgeY || wrappedY2 > bottomEdgeY;

        if (!wrapXRequired && !wrapYRequired) {
            addLinesToPane(new Line(x1, y1, wrappedX2, wrappedY2));
        } else if (wrapXRequired && !wrapYRequired) {
            handleHorizontalWrap(x1, y1, wrappedX2, wrappedY2, dx, dy);
        } else if (!wrapXRequired) {
            handleVerticalWrap(x1, y1, wrappedX2, wrappedY2, dx, dy);
        } else {
            handleCornerWrap(x1, y1, wrappedX2, wrappedY2, dx, dy);
        }
    }

    /**
     * Handles horizontal wrapping by creating the appropriate lines.
     *
     * @param x1        The x-coordinate of the starting point.
     * @param y1        The y-coordinate of the starting point.
     * @param wrappedX2 The wrapped x-coordinate of the ending point.
     * @param wrappedY2 The wrapped y-coordinate of the ending point.
     * @param dx        The differential of both x-coordinates.
     * @param dy        The differential of both y-coordinates.
     */
    private void handleHorizontalWrap(double x1, double y1, double wrappedX2, double wrappedY2, double dx, double dy) {
        double leftEdgeY = getLeftEdgeIntersection(x1, y1, dx, dy);
        double rightEdgeY = getRightEdgeIntersection(x1, y1, dx, dy);

        if (wrappedX2 < leftEdgeX) {
            addLinesToPane(
                    new Line(x1, y1, leftEdgeX, leftEdgeY),
                    new Line(rightEdgeX, leftEdgeY, wrappedX2 + rightEdgeX, wrappedY2)
            );
        } else {
            addLinesToPane(
                    new Line(x1, y1, rightEdgeX, rightEdgeY),
                    new Line(leftEdgeX, rightEdgeY, wrappedX2 - rightEdgeX, wrappedY2)
            );
        }
    }

    /**
     * Handles vertical wrapping by creating the appropriate lines.
     *
     * @param x1        The x-coordinate of the starting point.
     * @param y1        The y-coordinate of the starting point.
     * @param wrappedX2 The wrapped x-coordinate of the ending point.
     * @param wrappedY2 The wrapped y-coordinate of the ending point.
     * @param dx        The differential of both x-coordinates.
     * @param dy        The differential of both y-coordinates.
     */
    private void handleVerticalWrap(double x1, double y1, double wrappedX2, double wrappedY2, double dx, double dy) {
        double topEdgeX = getTopEdgeIntersection(x1, y1, dx, dy);
        double bottomEdgeX = getBottomEdgeIntersection(x1, y1, dx, dy);

        if (wrappedY2 < topEdgeY) {
            addLinesToPane(
                    new Line(x1, y1, topEdgeX, topEdgeY),
                    new Line(topEdgeX, bottomEdgeY, wrappedX2, wrappedY2 + bottomEdgeY)
            );
        } else {
            addLinesToPane(
                    new Line(x1, y1, bottomEdgeX, bottomEdgeY),
                    new Line(bottomEdgeX, topEdgeY, wrappedX2, wrappedY2 - bottomEdgeY)
            );
        }

    }

    /**
     * Handles corner wrapping by creating the appropriate lines for horizontal and vertical map wrapping.
     *
     * @param x1        The x-coordinate of the starting point.
     * @param y1        The y-coordinate of the starting point.
     * @param wrappedX2 The wrapped x-coordinate of the ending point.
     * @param wrappedY2 The wrapped y-coordinate of the ending point.
     * @param dx        The differential of both x-coordinates.
     * @param dy        The differential of both y-coordinates.
     */
    private void handleCornerWrap(double x1, double y1, double wrappedX2, double wrappedY2, double dx, double dy) {
        double leftEdgeY = getLeftEdgeIntersection(x1, y1, dx, dy);
        double rightEdgeY = getRightEdgeIntersection(x1, y1, dx, dy);
        double topEdgeX = getTopEdgeIntersection(x1, y1, dx, dy);
        double bottomEdgeX = getBottomEdgeIntersection(x1, y1, dx, dy);

        if (dx < leftEdgeX && dy < topEdgeY) {
            handleTopLeftWrap(x1, y1, wrappedX2, wrappedY2, leftEdgeY, topEdgeX);
        } else if (dx > leftEdgeX && dy < topEdgeY) {
            handleTopRightWrap(x1, y1, wrappedX2, wrappedY2, rightEdgeY, topEdgeX);
        } else if (dx < leftEdgeX && dy > topEdgeY) {
            handleBottomLeftWrap(x1, y1, wrappedX2, wrappedY2, leftEdgeY, bottomEdgeX);
        } else {
            handleBottomRightWrap(x1, y1, wrappedX2, wrappedY2, rightEdgeY, bottomEdgeX);
        }
    }

    /**
     * Handles top left corner wrapping by creating the appropriate lines for horizontal and vertical map wrapping.
     *
     * @param x1        The x-coordinate of the starting point.
     * @param y1        The y-coordinate of the starting point.
     * @param wrappedX2 The wrapped x-coordinate of the ending point.
     * @param wrappedY2 The wrapped y-coordinate of the ending point.
     * @param leftEdgeY The y-coordinate of the intersection with the left edge.
     * @param topEdgeX  The x-coordinate of the intersection with the top edge.
     */
    private void handleTopLeftWrap(double x1, double y1, double wrappedX2, double wrappedY2, double leftEdgeY, double topEdgeX) {
        boolean intersectsVerticalEdgeFirst = checkIntersection(x1, y1, wrappedX2, wrappedY2, leftEdgeX, topEdgeY);
        if (intersectsVerticalEdgeFirst) {
            addLinesToPane(
                    new Line(x1, y1, leftEdgeX, leftEdgeY),
                    new Line(rightEdgeX, leftEdgeY, topEdgeX + rightEdgeX, topEdgeY),
                    new Line(topEdgeX + rightEdgeX, bottomEdgeY, wrappedX2 + rightEdgeX, wrappedY2 + bottomEdgeY)
            );
        } else {
            addLinesToPane(
                    new Line(x1, y1, topEdgeX, topEdgeY),
                    new Line(topEdgeX, bottomEdgeY, leftEdgeX, leftEdgeY + bottomEdgeY),
                    new Line(rightEdgeX, leftEdgeY + bottomEdgeY, wrappedX2 + rightEdgeX, wrappedY2 + bottomEdgeY)
            );
        }
    }

    /**
     * Handles top right corner wrapping by creating the appropriate lines for horizontal and vertical map wrapping.
     *
     * @param x1         The x-coordinate of the starting point.
     * @param y1         The y-coordinate of the starting point.
     * @param wrappedX2  The wrapped x-coordinate of the ending point.
     * @param wrappedY2  The wrapped y-coordinate of the ending point.
     * @param rightEdgeY The y-coordinate of the intersection with the right edge.
     * @param topEdgeX   The x-coordinate of the intersection with the top edge.
     */
    private void handleTopRightWrap(double x1, double y1, double wrappedX2, double wrappedY2, double rightEdgeY, double topEdgeX) {
        boolean intersectsVerticalEdgeFirst = checkIntersection(x1, y1, wrappedX2, wrappedY2, rightEdgeX, topEdgeY);
        if (intersectsVerticalEdgeFirst) {
            addLinesToPane(
                    new Line(x1, y1, rightEdgeX, rightEdgeY),
                    new Line(leftEdgeX, rightEdgeY, topEdgeX - rightEdgeX, topEdgeY),
                    new Line(topEdgeX - rightEdgeX, bottomEdgeY, wrappedX2 - rightEdgeX, wrappedY2 + bottomEdgeY)
            );
        } else {
            addLinesToPane(
                    new Line(x1, y1, topEdgeX, topEdgeY),
                    new Line(topEdgeX, bottomEdgeY, rightEdgeX, rightEdgeY + bottomEdgeY),
                    new Line(leftEdgeX, rightEdgeY + bottomEdgeY, wrappedX2 - rightEdgeX, wrappedY2 + bottomEdgeY)
            );
        }
    }

    /**
     * Handles bottom left corner wrapping by creating the appropriate lines for horizontal and vertical map wrapping.
     *
     * @param x1          The x-coordinate of the starting point.
     * @param y1          The y-coordinate of the starting point.
     * @param wrappedX2   The wrapped x-coordinate of the ending point.
     * @param wrappedY2   The wrapped y-coordinate of the ending point.
     * @param leftEdgeY   The y-coordinate of the intersection with the left edge.
     * @param bottomEdgeX The x-coordinate of the intersection with the bottom edge.
     */
    private void handleBottomLeftWrap(double x1, double y1, double wrappedX2, double wrappedY2, double leftEdgeY, double bottomEdgeX) {
        boolean intersectsVerticalEdgeFirst = checkIntersection(x1, y1, wrappedX2, wrappedY2, leftEdgeX, bottomEdgeY);
        if (intersectsVerticalEdgeFirst) {
            addLinesToPane(
                    new Line(x1, y1, leftEdgeX, leftEdgeY),
                    new Line(rightEdgeX, leftEdgeY, bottomEdgeX + rightEdgeX, bottomEdgeY),
                    new Line(bottomEdgeX + rightEdgeX, topEdgeY, wrappedX2 + rightEdgeX, wrappedY2 - bottomEdgeY)
            );
        } else {
            addLinesToPane(
                    new Line(x1, y1, bottomEdgeX, bottomEdgeY),
                    new Line(bottomEdgeX, topEdgeY, leftEdgeX, leftEdgeY - bottomEdgeY),
                    new Line(rightEdgeX, leftEdgeY - bottomEdgeY, wrappedX2 + rightEdgeX, wrappedY2 - bottomEdgeY)
            );
        }
    }

    /**
     * Handles bottom right corner wrapping by creating the appropriate lines for horizontal and vertical map wrapping.
     *
     * @param x1          The x-coordinate of the starting point.
     * @param y1          The y-coordinate of the starting point.
     * @param wrappedX2   The wrapped x-coordinate of the ending point.
     * @param wrappedY2   The wrapped y-coordinate of the ending point.
     * @param rightEdgeY  The y-coordinate of the intersection with the right edge.
     * @param bottomEdgeX The x-coordinate of the intersection with the bottom edge.
     */
    private void handleBottomRightWrap(double x1, double y1, double wrappedX2, double wrappedY2, double rightEdgeY, double bottomEdgeX) {
        boolean intersectsVerticalEdgeFirst = checkIntersection(x1, y1, wrappedX2, wrappedY2, rightEdgeX, bottomEdgeY);
        if (intersectsVerticalEdgeFirst) {
            addLinesToPane(
                    new Line(x1, y1, rightEdgeX, rightEdgeY),
                    new Line(leftEdgeX, rightEdgeY, bottomEdgeX - rightEdgeX, bottomEdgeY),
                    new Line(bottomEdgeX - rightEdgeX, topEdgeY, wrappedX2 - rightEdgeX, wrappedY2 - bottomEdgeY)
            );
        } else {
            addLinesToPane(
                    new Line(x1, y1, bottomEdgeX, bottomEdgeY),
                    new Line(bottomEdgeX, topEdgeY, rightEdgeX, rightEdgeY - bottomEdgeY),
                    new Line(leftEdgeX, rightEdgeY - bottomEdgeY, wrappedX2 - rightEdgeX, wrappedY2 - bottomEdgeY)
            );
        }
    }

    /**
     * Returns the y-coordinate of the left edge intersection.
     *
     * @param x1 The x-coordinate of the starting point.
     * @param y1 The y-coordinate of the starting point.
     * @param dx The differential of both x-coordinates.
     * @param dy The differential of both y-coordinates.
     * @return The y-coordinate of the left edge intersection.
     */
    private double getLeftEdgeIntersection(double x1, double y1, double dx, double dy) {
        return y1 + dy * (leftEdgeX - x1) / dx;
    }

    /**
     * Returns the y-coordinate of the right edge intersection.
     *
     * @param x1 The x-coordinate of the starting point.
     * @param y1 The y-coordinate of the starting point.
     * @param dx The differential of both x-coordinates.
     * @param dy The differential of both y-coordinates.
     * @return The y-coordinate of the right edge intersection.
     */
    private double getRightEdgeIntersection(double x1, double y1, double dx, double dy) {
        return y1 + dy * (rightEdgeX - x1) / dx;
    }

    /**
     * Returns the x-coordinate of the top edge intersection.
     *
     * @param x1 The x-coordinate of the starting point.
     * @param y1 The y-coordinate of the starting point.
     * @param dx The differential of both x-coordinates.
     * @param dy The differential of both y-coordinates.
     * @return The x-coordinate of the top edge intersection.
     */
    private double getTopEdgeIntersection(double x1, double y1, double dx, double dy) {
        return x1 + dx * (topEdgeY - y1) / dy;
    }

    /**
     * Returns the x-coordinate of the bottom edge intersection.
     *
     * @param x1 The x-coordinate of the starting point.
     * @param y1 The y-coordinate of the starting point.
     * @param dx The differential of both x-coordinates.
     * @param dy The differential of both y-coordinates.
     * @return The x-coordinate of the bottom edge intersection.
     */
    private double getBottomEdgeIntersection(double x1, double y1, double dx, double dy) {
        return x1 + dx * (bottomEdgeY - y1) / dy;
    }

    /**
     * Checks if the line between the given coordinates reaches the targetX value before reaching the targetY value.
     *
     * @param x1      The x-coordinate of the starting point.
     * @param y1      The y-coordinate of the starting point.
     * @param x2      The x-coordinate of the ending point.
     * @param y2      The y-coordinate of the ending point.
     * @param targetX The target x-value to check.
     * @param targetY The target y-value to check.
     * @return True if the line reaches targetX before targetY, false otherwise.
     * @throws NoIntersectionWithCoordinatesException if the line does not intersect both target values.
     */
    private boolean checkIntersection(double x1, double y1, double x2, double y2, double targetX, double targetY) throws NoIntersectionWithCoordinatesException {
        double dx = x2 - x1;
        double dy = y2 - y1;

        if (dx == 0 || dy == 0) {
            throw new NoIntersectionWithCoordinatesException(x1, y1, x2, y2, targetX, targetY);
        }

        double m = dy / dx;

        double intersectionX = x1 + (targetY - y1) / m;

        double intersectionY = y1 + m * (targetX - x1);

        boolean reachesTargetX = (targetX >= Math.min(x1, x2) && targetX <= Math.max(x1, x2));
        boolean reachesTargetY = (targetY >= Math.min(y1, y2) && targetY <= Math.max(y1, y2));

        if (!reachesTargetX || !reachesTargetY) {
            throw new NoIntersectionWithCoordinatesException(x1, y1, x2, y2, targetX, targetY);
        }

        double distanceToTargetX = calculateDistance(x1, y1, targetX, intersectionY);
        double distanceToTargetY = calculateDistance(x1, y1, intersectionX, targetY);

        return distanceToTargetX < distanceToTargetY;
    }

    /**
     * Calculates the distance between two given points.
     *
     * @param x1 The x coordinate of the first point.
     * @param y1 The y coordinate of the first point.
     * @param x2 The x coordinate of the second point.
     * @param y2 The y coordinate of the second point.
     * @return The distance between the two given points.
     */
    private double calculateDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * Adds the given lines to the {@link #cityConnectionPane} using {@link #addCenteredLineToPane(Line)}.
     *
     * @param lines The lines to be added to the {@link #cityConnectionPane}.
     */
    private void addLinesToPane(Line... lines) {
        for (Line line : lines) {
            addCenteredLineToPane(line);
        }
    }

    /**
     * Centers the given {@link Line}, adds it to the {@link #cityConnectionPane} and binds it to the {@link #webView}.
     *
     * <p>
     * The {@link Line} is centered to ensure correct positioning and scaling.
     * </p>
     *
     * @param line the {@link Line} that is centered and added to the {@link #cityConnectionPane}.
     */
    private void addCenteredLineToPane(Line line) {
        line.setStroke(Color.DIMGREY);
        line.setStrokeWidth(4);

        double x1 = line.getStartX();
        double x2 = line.getEndX();
        double y1 = line.getStartY();
        double y2 = line.getEndY();

        double centerX = getCenterCoordinate(line.getStartX(), line.getEndX());
        double centerY = getCenterCoordinate(line.getStartY(), line.getEndY());

        double dx = -centerX;
        double dy = -centerY;

        line.setStartX(line.getStartX() + dx);
        line.setStartY(line.getStartY() + dy);
        line.setEndX(line.getEndX() + dx);
        line.setEndY(line.getEndY() + dy);

        cityConnectionPane.getChildren().add(line);
        bindLineToWebView(line, x1, y1, x2, y2);
    }

    /**
     * Calculates the coordinate that is centered between two given coordinates.
     *
     * @param coordinate1 The first coordinate value.
     * @param coordinate2 The second coordinate value.
     * @return The coordinate value that is exactly halfway between the two given coordinates.
     */
    private double getCenterCoordinate(double coordinate1, double coordinate2) {
        return (coordinate1 + coordinate2) * 0.5;
    }

    /**
     * Binds the given {@link Line} to the {@link #webView}.
     *
     * @param line   the {@link Line} to be bound to the {@link #webView}.
     * @param startX the start x coordinate of the given {@link Line}.
     * @param startY the start y coordinate of the given {@link Line}.
     * @param endX   the end x coordinate of the given {@link Line}.
     * @param endY   the end y coordinate of the given {@link Line}.
     * @see NodeBindingUtils
     */
    private void bindLineToWebView(Line line, double startX, double startY, double endX, double endY) {
        double centerX = getCenterCoordinate(startX, endX);
        double centerY = getCenterCoordinate(startY, endY);
        double xCoordinate = centerX / rightEdgeX;
        double yCoordinate = centerY / bottomEdgeY;
        double xScaleFactor = 1.0 / rightEdgeX;
        double yScaleFactor = 1.0 / bottomEdgeY;
        NodeBindingUtils.bindWebViewSizeAndPositionToNode(webView, line, xCoordinate, yCoordinate, xScaleFactor, yScaleFactor);
    }
}
