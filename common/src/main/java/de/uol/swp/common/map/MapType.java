package de.uol.swp.common.map;

import de.uol.swp.common.plague.Plague;
import lombok.Getter;

import java.io.Serializable;
import java.util.*;

/**
 * Definition of a type of map
 *
 * <p>
 *     Used in {@link GameMap} to specify the general map layout and information about where the players start.
 * </p>
 *
 * @see City
 * @see GameMap
 * @see MapSlot
 * @author Tom Weelborg
 * @since 2024-09-02
 */
@Getter
public class MapType implements Serializable {
    private byte[] backgroundImage;
    private List<MapSlot> map;
    private City startingCity;

    public MapType(final List<MapSlot> map, final City startingCity) {
        this.map = map;
        this.startingCity = startingCity;
    }

    /**
     * <p>
     *     Returns a list of all unique {@link Plague} found in the MapType.
     * </p>
     *
     * @return {@link Set} of {@link Plague}
     */
    public Set<Plague> getUniquePlagues() {
        Set<Plague> plaguesSet = new HashSet<>();

        for (MapSlot slot : map) {
            Plague plague = slot.getPlague();
            plaguesSet.add(plague);
        }
        return plaguesSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapType mapType = (MapType) o;
        return Arrays.equals(backgroundImage, mapType.backgroundImage) &&
                Objects.equals(map, mapType.map) &&
                Objects.equals(startingCity, mapType.startingCity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(backgroundImage), map, startingCity);
    }
}
