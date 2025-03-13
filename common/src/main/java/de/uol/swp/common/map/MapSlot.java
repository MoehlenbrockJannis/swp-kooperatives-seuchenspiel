package de.uol.swp.common.map;

import de.uol.swp.common.plague.Plague;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
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
 */
@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class MapSlot implements Serializable {
    private City city;
    private List<City> connectedCities;
    private Plague plague;
    private int xCoordinate;
    private int yCoordinate;

    @Override
    public String toString() {
        return city.getName();
    }

    /**
     * <p>
     *     Returns {@code true} if given {@link Plague} is equal to {@link #plague}, {@code false} otherwise.
     * </p>
     *
     * @param plague {@link Plague} to check equality with {@link #plague} for
     * @return {@code true} if given {@link Plague} is equal to {@link #plague}, {@code false} otherwise
     */
    public boolean hasPlague(final Plague plague) {
        return this.plague.equals(plague);
    }
}
