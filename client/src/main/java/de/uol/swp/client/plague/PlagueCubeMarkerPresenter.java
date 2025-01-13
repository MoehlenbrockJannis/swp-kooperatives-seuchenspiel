package de.uol.swp.client.plague;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.util.ColorService;
import de.uol.swp.common.map.Field;
import javafx.scene.paint.Color;
import lombok.Getter;

import java.util.List;


/**
 * Presents the {@link PlagueCubeMarker} on the {@link de.uol.swp.client.game.GameMapPresenter}.
 * Contains all functions for the {@link PlagueCubeMarker}.
 */
public class PlagueCubeMarkerPresenter extends AbstractPresenter {

    private final PlagueCubeMarker plagueCubeMarker;
    @Getter
    private final Field field;

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
     * Updates the displayed {@link PlagueCubeIcon}s on the {@link PlagueCubeMarker}
     */
    public void updateMarker() {
        List<de.uol.swp.common.plague.PlagueCube> plagueCubesOfAssociatedPlague = field.getPlagueCubesOfPlague(field.getPlague());
        int numberOfAssociatedPlagueCubes = plagueCubesOfAssociatedPlague.size();
        plagueCubeMarker.setNumberOfAssociatedPlagueCubes(numberOfAssociatedPlagueCubes);

        updateAssociatedPlagueCubes();

        updateForeignPlagueCubes();
    }

    private void updateAssociatedPlagueCubes() {
        Color associatedPlagueColor = ColorService.convertColorToJavaFXColor(field.getPlague().getColor());

        plagueCubeMarker.deleteAllAssociatedPlagueCubes();

        int numberOfAssociatedPlagueCubes = plagueCubeMarker.getNumberOfAssociatedPlagueCubes();

        if (numberOfAssociatedPlagueCubes == 1) {
            plagueCubeMarker.createSinglePlagueCube(associatedPlagueColor);
        } else if (numberOfAssociatedPlagueCubes == 2) {
            plagueCubeMarker.createPairOfPlagueCubes(associatedPlagueColor);
        } else if (numberOfAssociatedPlagueCubes >= 3) {
            plagueCubeMarker.createPyramidOfPlagueCubes(associatedPlagueColor);
        }

        updatePlagueCubeCounter();
    }

    private void updatePlagueCubeCounter() {
        int numberOfAssociatedPlagueCubes = plagueCubeMarker.getNumberOfAssociatedPlagueCubes();

        if (numberOfAssociatedPlagueCubes > 3) {
            plagueCubeMarker.updatePlagueCubeCounter();
            plagueCubeMarker.setAssociatedPlagueCubeCounterVisibility(true);
        } else {
            plagueCubeMarker.setAssociatedPlagueCubeCounterVisibility(false);
        }

    }

    private void updateForeignPlagueCubes() {
        plagueCubeMarker.deleteAllForeignPlagueCubes();

        plagueCubeMarker.addForeignPlagueCubes(field);
    }
}
