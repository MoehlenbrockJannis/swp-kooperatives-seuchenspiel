package de.uol.swp.client.plague;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.plague.Plague;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Cursor;
import javafx.scene.transform.Scale;
import javafx.util.Duration;
import lombok.Getter;

import java.util.List;
import java.util.function.Consumer;

/**
 * Manages plague cube markers in a game field, handling animations and interactions.
 */
public class PlagueCubeMarkerPresenter extends AbstractPresenter {

    @Getter
    private final PlagueCubeMarker plagueCubeMarker;
    @Getter
    private final Field field;
    private Timeline animationTimeline;

    /**
     * Constructor
     * <p>
     * Initializes the {@link PlagueCubeMarkerPresenter} and adds {@link PlagueCubeIcon}s to the associated {@link PlagueCubeMarker} by using {@link #updateMarker()}
     * </p>
     *
     * @param plagueCubeMarker the {@link PlagueCubeMarker} associated with the Presenter
     * @param field            the {@link Field} associated with the Presenter
     */
    public PlagueCubeMarkerPresenter(PlagueCubeMarker plagueCubeMarker, Field field) {
        this.plagueCubeMarker = plagueCubeMarker;
        this.field = field;
    }

    /**
     * Starts a pulsing animation and makes the marker clickable with hover effects
     */
    public void startPulsingAnimation() {
        stopAnimation();

        Scale animationScale = new Scale(1.0, 1.0);
        plagueCubeMarker.getTransforms().removeIf(Scale.class::isInstance);
        plagueCubeMarker.getTransforms().add(animationScale);

        animationTimeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(animationScale.xProperty(), 1.0),
                        new KeyValue(animationScale.yProperty(), 1.0)
                ),
                new KeyFrame(Duration.seconds(0.5),
                        new KeyValue(animationScale.xProperty(), 1.2),
                        new KeyValue(animationScale.yProperty(), 1.2)
                ),
                new KeyFrame(Duration.seconds(1.0),
                        new KeyValue(animationScale.xProperty(), 1.0),
                        new KeyValue(animationScale.yProperty(), 1.0)
                )
        );

        animationTimeline.setCycleCount(Timeline.INDEFINITE);
        animationTimeline.play();
    }

    /**
     * Stops the current animation and removes click/hover handlers
     */
    public void stopAnimation() {
        if (animationTimeline != null) {
            animationTimeline.stop();
            animationTimeline = null;
            plagueCubeMarker.getTransforms().removeIf(Scale.class::isInstance);
            plagueCubeMarker.getTransforms().add(new Scale(1.0, 1.0));
        }

        plagueCubeMarker.setOnMouseClicked(null);
        plagueCubeMarker.setOnMouseEntered(null);
        plagueCubeMarker.setOnMouseExited(null);
        plagueCubeMarker.setEffect(null);
        plagueCubeMarker.setCursor(Cursor.DEFAULT);
        plagueCubeMarker.setMouseTransparent(true);
    }

    /**
     * Sets up mouse click handlers for plague cube icons.
     * This method configures each plague cube icon to be clickable and applies the provided handler
     * to process the associated Plague object when clicked.
     *
     * @param onPlagueCubeIconClickHandler the Consumer that will be called with the Plague object when a plague cube icon is clicked
     */
    public void addOnMouseClickedForEachPlagueCubeIcon(Consumer<Plague> onPlagueCubeIconClickHandler) {
        plagueCubeMarker.setMouseTransparent(false);
        plagueCubeMarker.getPlagueCubeIcons().forEach(plagueCubeIcon -> {
            plagueCubeIcon.setCursor(Cursor.HAND);
            setMouseClickedForEachPlagueCubeIcon(plagueCubeIcon, onPlagueCubeIconClickHandler);
        });
    }

    public void setMouseClickedForEachPlagueCubeIcon(PlagueCubeIcon plagueCubeIcon, Consumer<Plague> onPlagueCubeIconClickHandler) {
        plagueCubeIcon.setOnMouseClicked(event -> {
            Plague plague = plagueCubeIcon.getPlague();
            onPlagueCubeIconClickHandler.accept(plague);
        });
    }

    /**
     * Removes all mouse click handlers from plague cube icons.
     * This method makes plague cube icons non-clickable by resetting their mouse event handlers
     * and restoring the default cursor appearance.
     */
    public void removeOnMouseClickedForEachPlagueCubeIcon() {
        plagueCubeMarker.setMouseTransparent(true);
        plagueCubeMarker.getPlagueCubeIcons().forEach(plagueCubeIcon -> {
            plagueCubeIcon.setCursor(Cursor.DEFAULT);
            plagueCubeIcon.setOnMouseClicked(null);
        });
    }

    /**
     * Updates the displayed {@link PlagueCubeIcon}s on the {@link PlagueCubeMarker}
     */
    public void updateMarker() {
        List<de.uol.swp.common.plague.PlagueCube> plagueCubesOfAssociatedPlague = field.getPlagueCubesOfPlague(field.getPlague());
        int numberOfAssociatedPlagueCubes = plagueCubesOfAssociatedPlague.size();
        plagueCubeMarker.setNumberOfAssociatedPlagueCubes(numberOfAssociatedPlagueCubes);

        updateAssociatedPlagueCubes();
        updateForeignPlagueCubes();

        stopAnimation();
    }

    /**
     * Updates the marker to show associated plague cubes based on their number.
     */
    private void updateAssociatedPlagueCubes() {
        plagueCubeMarker.deleteAllAssociatedPlagueCubes();

        int numberOfAssociatedPlagueCubes = plagueCubeMarker.getNumberOfAssociatedPlagueCubes();
        Plague plague = field.getPlague();

        if (numberOfAssociatedPlagueCubes == 1) {
            plagueCubeMarker.createSinglePlagueCube(plague);
        } else if (numberOfAssociatedPlagueCubes == 2) {
            plagueCubeMarker.createPairOfPlagueCubes(plague);
        } else if (numberOfAssociatedPlagueCubes >= 3) {
            plagueCubeMarker.createPyramidOfPlagueCubes(plague);
        }

        updatePlagueCubeCounter();
    }

    /**
     * Updates the visibility of the plague cube counter based on the number of cubes.
     */
    private void updatePlagueCubeCounter() {
        int numberOfAssociatedPlagueCubes = plagueCubeMarker.getNumberOfAssociatedPlagueCubes();

        if (numberOfAssociatedPlagueCubes > 3) {
            plagueCubeMarker.updatePlagueCubeCounter();
            plagueCubeMarker.setAssociatedPlagueCubeCounterVisibility(true);
        } else {
            plagueCubeMarker.setAssociatedPlagueCubeCounterVisibility(false);
        }
    }

    /**
     * Updates the marker to show foreign plague cubes for other diseases.
     */
    private void updateForeignPlagueCubes() {
        plagueCubeMarker.deleteAllForeignPlagueCubes();
        plagueCubeMarker.addForeignPlagueCubes(field);
    }
}