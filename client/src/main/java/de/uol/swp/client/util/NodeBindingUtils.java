package de.uol.swp.client.util;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
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
     * Binds the scaleX, scaleY, layoutX, and layoutY properties of the target {@link Node} to the
     * corresponding properties of the source {@link WebView} and the given coordinates.
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

    /**
     * Binds the preferred width and height of the target {@link StackPane} to the width and height of the source {@link Region}.
     *
     * @param sourceRegion    The {@link Region} whose size will be used to set the preferred width and height of the targetStackPane.
     * @param targetStackPane The {@link StackPane} whose preferred width and height will be bound to the sourceRegion.
     * @author David Scheffler
     * @since 2025-02-21
     */
    public static void bindRegionSizeToStackPanePrefSize(Region sourceRegion, StackPane targetStackPane) {
        targetStackPane.prefWidthProperty().bind(sourceRegion.widthProperty());
        targetStackPane.prefHeightProperty().bind(sourceRegion.heightProperty());
    }

    /**
     * Binds the font size of the target {@link Text} to the dimensions of the source {@link Region}. The font size
     * will scale based on the smallest of the {@link Region}'s width and height, and the given scaling factor will
     * be applied to determine the scale.
     *
     * @param sourceRegion The {@link Region} whose width and height will determine the font size of the targetText.
     * @param targetText   The {@link Text} whose font size will be bound to the size of the sourceRegion.
     * @param scaleFactor  A scaling factor to control the size of the font relative to the sourceRegion's dimensions.
     * @author David Scheffler
     * @since 2025-02-21
     */
    public static void bindRegionSizeToTextFont(Region sourceRegion, Text targetText, double scaleFactor) {
        targetText.styleProperty().bind(Bindings.concat(
                "-fx-font-size: ",
                Bindings.min(
                        sourceRegion.heightProperty(), sourceRegion.widthProperty()
                ).multiply(scaleFactor).asString(), ";"
        ));
    }

    /**
     * Binds the scaleX and scaleY properties of the target {@link Node} to the size of the source {@link Region}.
     * The {@link Node} will scale based on the smallest of the {@link Region}'s width and height, and the given
     * scaling factor will be applied to determine the scale.
     *
     * @param sourceRegion  The {@link Region} whose size will be bound to the scaling of the targetNode.
     * @param targetNode    The {@link Node} whose scaleX and scaleY properties will be bound to the size of the sourceRegion.
     * @param scalingFactor A factor to control the scaling of the targetNode based on the sourceRegion's size.
     * @author David Scheffler
     * @since 2025-02-21
     */
    public static void bindRegionSizeToNode(Region sourceRegion, Node targetNode, double scalingFactor) {
        targetNode.scaleXProperty().bind(Bindings.min(sourceRegion.widthProperty(), sourceRegion.heightProperty()).multiply(scalingFactor));
        targetNode.scaleYProperty().bind(Bindings.min(sourceRegion.widthProperty(), sourceRegion.heightProperty()).multiply(scalingFactor));
    }
}
