package de.uol.swp.client.gameboard;

import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;

// TODO replace client.gameboard.TempMapSlot with common.map.MapSlot
/**
 * Temporary class to test functionality of {@link WorldMapPresenter} and {@link CityMarker}.
 * This class imitates the functionality of {@link de.uol.swp.common.map.MapSlot} (and its attributes)
 * and should be replaced with it.
 *
 * @see de.uol.swp.common.map.MapSlot
 * @author David Scheffler
 * @since 2024-09-10
 */
@AllArgsConstructor
@Getter
public class TempMapSlot {
    private String cityName;
    private Color plagueColor;
    private int xCoordinate;
    private int yCoordinate;
}
