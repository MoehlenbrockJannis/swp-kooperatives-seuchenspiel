package de.uol.swp.client.map.exception;


import de.uol.swp.client.map.CityConnectionPanePresenter;

/**
 * Exception thrown when a line does not intersect both target x- and y-coordinates.
 *
 * @author David Scheffler
 * @see CityConnectionPanePresenter
 * @since 2025-01-30
 */
public class NoIntersectionWithCoordinatesException extends RuntimeException {
    public NoIntersectionWithCoordinatesException(double x1, double y1, double x2, double y2, double targetX, double targetY) {
        super("The line between (" + x1 + ", " + y1 + ") and (" + x2 + ", " + y2 + ") does not reach x = " + targetX + " and y = " + targetY);
    }
}
