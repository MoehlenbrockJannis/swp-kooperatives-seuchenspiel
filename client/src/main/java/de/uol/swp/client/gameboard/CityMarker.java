package de.uol.swp.client.gameboard;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Getter
public class CityMarker extends Circle {

    private static final Logger LOG = LogManager.getLogger(CityMarker.class);

    private final TempMapSlot mapSlot;

    public CityMarker(TempMapSlot mapSlot) {
        this.mapSlot = mapSlot;

        this.setFill(mapSlot.getPlagueColor());
        this.setRadius(7);

        this.setOnMouseEntered(event -> this.setOpacity(0.5));

        this.setOnMouseExited(event -> this.setOpacity(1.0));

        this.setOnMouseClicked(event -> {
            if (getColor() == mapSlot.getPlagueColor()) {
                this.setFill(Color.WHITE);
                this.setOpacity(1.0);
            } else {
                this.setFill(mapSlot.getPlagueColor());
            }

            LOG.debug("User clicked on city {} at [{}, {}]", mapSlot.getCityName(), mapSlot.getXCoordinate(), mapSlot.getYCoordinate());
        });
    }

    private Color getColor() {
        return (Color) this.getFill();
    }
}