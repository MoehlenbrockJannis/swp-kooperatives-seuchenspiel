package de.uol.swp.common.map;

import de.uol.swp.common.plague.Plague;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

/**
 * A wrapper for city that contains information on how it is displayed on a map.
 * It contains information about the edges to other cities, the associated plague and the city's coordinates.
 *
 * <p>
 *     Objects of this class are used in {@link MapType} to define the map.
 * </p>
 *
 * @see City
 * @see MapType
 * @see Plague
 * @author Tom Weelborg
 * @since 2024-09-02
 */
@AllArgsConstructor
@Getter
public class MapSlot implements Serializable {
    private City city;
    private List<City> connectedCities;
    private Plague plague;
    private int xCoordinate;
    private int yCoordinate;
}
