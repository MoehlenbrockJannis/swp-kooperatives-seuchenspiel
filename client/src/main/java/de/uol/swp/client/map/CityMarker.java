package de.uol.swp.client.map;

import de.uol.swp.client.util.ColorService;
import de.uol.swp.client.marker.HighlightableMarker;
import de.uol.swp.common.map.Field;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import lombok.Getter;

/**
 * JavaFX circle element used to render in GameMapPresenter. This element represents a city on the game map.
 *
 * @see GameMapPresenter
 * @author David Scheffler
 * @since 2024-09-10
 */
public class CityMarker extends HighlightableMarker {

    @Getter
    private final Field field;

    @Getter
    private static final int RADIUS = 10;

    /**
     * Constructor
     *
     * @param field A field of the gameMap from the current game
     * @author David Scheffler
     * @since 2024-09-10
     */
    public CityMarker(Field field) {
        this.field = field;

        Circle circle = new Circle();
        circle.setFill(getColor());
        circle.setRadius(RADIUS);
        circle.setStroke(Color.BLACK);
        circle.setStrokeType(StrokeType.INSIDE);

        Circle circleBorder = new Circle();
        circleBorder.setFill(Color.TRANSPARENT);
        circleBorder.setRadius(RADIUS);
        circleBorder.setStroke(Color.WHITE);
        circleBorder.setStrokeType(StrokeType.OUTSIDE);
        circleBorder.setMouseTransparent(true);

        this.getChildren().addAll(circle, circleBorder);
    }

    /**
     * Returns color of field's plague
     *
     * @return Color of field's plague
     * @see Field
     * @see de.uol.swp.common.plague.Plague
     * @since 2024-10-12
     */
    public Color getColor() {
        return ColorService.convertColorToJavaFXColor(field.getPlague().getColor());
    }
}