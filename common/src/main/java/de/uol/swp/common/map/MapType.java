package de.uol.swp.common.map;

import de.uol.swp.common.plague.Plague;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
public class MapType implements Serializable {
    @EqualsAndHashCode.Include
    private String name;
    private byte[] backgroundImage;
    private List<MapSlot> map;
    private City startingCity;

    public MapType(final String name, final List<MapSlot> map, final City startingCity) {
        this.name = name;
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
}
