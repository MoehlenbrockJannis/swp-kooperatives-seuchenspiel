package de.uol.swp.client.util;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

public abstract class HighlightableMarker extends Group {
    protected Timeline colorTimeline;
    protected Timeline scaleTimeline;

    protected final ColorAdjust colorAdjust = new ColorAdjust();
    protected final Scale animationScale = new Scale(1.0, 1.0);

    protected HighlightableMarker() {
        initHighlightAnimation();
    }

    /**
     * Initializes the highlight animation.
     *
     * @since 2024-10-13
     */
    private void initHighlightAnimation() {
        int duration = 800;

        this.setEffect(colorAdjust);
        this.getTransforms().add(animationScale);

        colorTimeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(colorAdjust.brightnessProperty(), 0.0)
                ),
                new KeyFrame(Duration.millis(duration),
                        new KeyValue(colorAdjust.brightnessProperty(), 0.7)
                )
        );
        colorTimeline.setCycleCount(Animation.INDEFINITE);
        colorTimeline.setAutoReverse(true);

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
        colorTimeline.play();
        scaleTimeline.play();
    }

    /**
     * Stops the highlight animation
     *
     * @since 2024-10-12
     */
    public void unhighlight() {
        colorTimeline.stop();
        scaleTimeline.stop();

        colorAdjust.setBrightness(0.0);
        animationScale.setX(1.0);
        animationScale.setY(1.0);
    }
}
