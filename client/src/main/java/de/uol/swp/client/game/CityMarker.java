package de.uol.swp.client.game;

import de.uol.swp.client.util.ColorService;
import de.uol.swp.common.map.Field;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.*;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import lombok.Getter;

/**
 * JavaFX circle element used to render in GameMapPresenter. This element represents a city on the game map.
 *
 * @see GameMapPresenter
 * @author David Scheffler
 * @since 2024-09-10
 */
public class CityMarker extends Group {

    private FadeTransition fadeTransition;
    private ScaleTransition scaleTransition;

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

        initHighlightAnimation();

        circle.setOnMouseEntered(event -> this.highlight());
        circle.setOnMouseExited(event -> this.unhighlight());

        this.getChildren().addAll(circle, circleBorder);
    }

    /**
     * Initializes the highlight animation.
     *
     * @since 2024-10-13
     */
    private void initHighlightAnimation() {
        fadeTransition = new FadeTransition(Duration.millis(800), this);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.3);
        fadeTransition.setCycleCount(Animation.INDEFINITE);
        fadeTransition.setAutoReverse(true);

        scaleTransition = new ScaleTransition(Duration.millis(800), this);
        scaleTransition.setFromX(1.0);
        scaleTransition.setToX(1.2);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToY(1.2);
        scaleTransition.setCycleCount(Animation.INDEFINITE);
        scaleTransition.setAutoReverse(true);
    }

    /**
     * Starts the highlight animation.
     *
     * @since 2024-10-12
     */
    public void highlight() {
        fadeTransition.play();
        scaleTransition.play();
    }

    /**
     * Stops the highlight animation
     *
     * @since 2024-10-12
     */
    public void unhighlight() {
        if (fadeTransition != null) {
            fadeTransition.stop();
        }
        if (scaleTransition != null) {
            scaleTransition.stop();
        }

        this.setOpacity(1.0);
        this.setScaleX(1.0);
        this.setScaleY(1.0);
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