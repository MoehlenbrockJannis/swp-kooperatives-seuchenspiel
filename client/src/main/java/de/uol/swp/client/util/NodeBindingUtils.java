package de.uol.swp.client.util;

import javafx.scene.Node;
import javafx.scene.web.WebView;

/**
 * Utility class that provides methods to bind the properties of one Node to another Node.
 *
 * @author David Scheffler
 * @since 2025-01-15
 */
public class NodeBindingUtils {

    /**
     * Private constructor to prevent instantiation
     *
     * @author David Scheffler
     * @since 2025-01-16
     */
    private NodeBindingUtils() {
    }

    /**
     * Binds the scaleX, scaleY, layoutX, and layoutY properties of the targetNode to the
     * corresponding properties of the sourceWebView and the given coordinates.
     *
     * @param sourceWebView The WebView whose size and position will be bound to the targetNode.
     * @param targetNode    The node whose size and position will be adjusted based on the sourceWebView.
     * @param xCoordinate   The factor by which the targetNode's layoutX position is scaled relative to the sourceWebView's width.
     * @param yCoordinate   The factor by which the targetNode's layoutY position is scaled relative to the sourceWebView's height.
     * @param xScaleFactor  The factor by which the targetNode's horizontal scaling (scaleX) is determined relative to the sourceWebView's width.
     * @param yScaleFactor  The factor by which the targetNode's vertical scaling (scaleY) is determined relative to the sourceWebView's height.
     * @author David Scheffler
     * @since 2025-01-15
     */
    public static void bindWebViewSizeAndPositionToNode(WebView sourceWebView, Node targetNode, double xCoordinate, double yCoordinate, double xScaleFactor, double yScaleFactor) {
        targetNode.layoutXProperty().bind(sourceWebView.widthProperty().multiply(xCoordinate));
        targetNode.layoutYProperty().bind(sourceWebView.heightProperty().multiply(yCoordinate));

        targetNode.scaleXProperty().bind(sourceWebView.widthProperty().multiply(xScaleFactor));
        targetNode.scaleYProperty().bind(sourceWebView.heightProperty().multiply(yScaleFactor));
    }
}
