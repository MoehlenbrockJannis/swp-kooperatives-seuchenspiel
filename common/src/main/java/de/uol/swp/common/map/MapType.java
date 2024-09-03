package de.uol.swp.common.map;

import lombok.Getter;

import java.util.List;

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
public class MapType {
    private byte[] backgroundImage;
    private List<MapSlot> map;
    private City startingCity;

    public MapType(final List<MapSlot> map, final City startingCity) {
        this.map = map;
        this.startingCity = startingCity;
    }
}
