package de.uol.swp.client.game;

import de.uol.swp.client.util.ColorService;
import de.uol.swp.common.map.Field;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.*;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Scale;
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
    private Timeline scaleTimeline;

    @Getter
    private final Field field;

    @Getter
    private static final int RADIUS = 10;

    private final Scale animationScale = new Scale(1.0, 1.0);

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

        this.getTransforms().add(animationScale);

        initHighlightAnimation();

        this.getChildren().addAll(circle, circleBorder);
    }

    /**
     * Initializes the highlight animation.
     *
     * @since 2024-10-13
     */
    private void initHighlightAnimation() {
        int duration = 800;

        fadeTransition = new FadeTransition(Duration.millis(duration), this);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.3);
        fadeTransition.setCycleCount(Animation.INDEFINITE);
        fadeTransition.setAutoReverse(true);

        scaleTimeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(animationScale.xProperty(), 1.0),
                        new KeyValue(animationScale.yProperty(), 1.0)
                ),
                new KeyFrame(Duration.millis(duration),
                        new KeyValue(animationScale.xProperty(), 1.2),
                        new KeyValue(animationScale.yProperty(), 1.2)
                )
        );
        scaleTimeline.setCycleCount(Animation.INDEFINITE);
        scaleTimeline.setAutoReverse(true);
    }

    /**
     * Starts the highlight animation.
     *
     * @since 2024-10-12
     */
    public void highlight() {
        fadeTransition.play();
        scaleTimeline.play();
    }

    /**
     * Stops the highlight animation
     *
     * @since 2024-10-12
     */
    public void unhighlight() {
        fadeTransition.stop();
        scaleTimeline.stop();

        this.setOpacity(1.0);
        animationScale.setX(1.0);
        animationScale.setY(1.0);
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