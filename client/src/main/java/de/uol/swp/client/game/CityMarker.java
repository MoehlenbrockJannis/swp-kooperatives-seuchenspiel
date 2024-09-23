package de.uol.swp.client.game;

import de.uol.swp.client.util.ColorService;
import de.uol.swp.common.map.MapSlot;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * JavaFX circle element used to render in GameMapPresenter. This element represents a city on the game map.
 *
 * @see GameMapPresenter
 * @author David Scheffler
 * @since 2024-09-10
 */
@Getter
public class CityMarker extends Circle {

    private static final Logger LOG = LogManager.getLogger(CityMarker.class);

    private final MapSlot mapSlot;

    /**
     * Constructor
     *
     * @param mapSlot A mapSlot of the mapType from the current game
     * @author David Scheffler
     * @since 2024-09-10
     */
    public CityMarker(MapSlot mapSlot) {
        this.mapSlot = mapSlot;
        Color mapSlotColor = ColorService.convertColorToJavaFXColor(mapSlot.getPlague().getColor());

        this.setFill(mapSlotColor);
        this.setRadius(7);

        this.setOnMouseEntered(event -> this.setOpacity(0.5));

        this.setOnMouseExited(event -> this.setOpacity(1.0));

        this.setOnMouseClicked(event -> {
            if (getColor() == mapSlotColor) {
                this.setFill(Color.WHITE);
                this.setOpacity(1.0);
            } else {
                this.setFill(mapSlotColor);
            }

            LOG.debug("User clicked on city {} at [{}, {}]", mapSlot.getCity().getName(), mapSlot.getXCoordinate(), mapSlot.getYCoordinate());
        });
    }

    /**
     * Returns current fill color of the CityMarker
     *
     * @return Color of the CityMarker
     */
    private Color getColor() {
        return (Color) this.getFill();
    }
}