package de.uol.swp.client.card;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import lombok.Getter;

/**
 * The CardIcon class represents a rectangular icon with rounded corners,
 * which can be used to display a card-like shape in a JavaFX application.
 * The size of the CardIcon is dynamically bound to the size of its parent Pane.
 */
public class CardIcon extends Rectangle {

    private static final int ARC_SIZE = 10;
    @Getter
    private static final double WIDTH_MULTIPLIER = 0.33;
    @Getter
    private static final double HEIGHT_MULTIPLIER = 0.55;
    private static final Color STOKED_COLOR = Color.BLACK;

    /**
     * Constructs a CardIcon with the specified color and binds its size to the parent Pane.
     *
     * @param color  the fill color of the CardIcon
     * @param parent the parent Pane to which the CardIcon's size is bound
     */
    public CardIcon(Color color, Pane parent) {
        heightProperty().bind(parent.prefHeightProperty().multiply(HEIGHT_MULTIPLIER));
        widthProperty().bind(parent.prefWidthProperty().multiply(WIDTH_MULTIPLIER));
        setFill(color);
        setStroke(STOKED_COLOR);
        setArcHeight(ARC_SIZE);
        setArcWidth(ARC_SIZE);
    }
}
