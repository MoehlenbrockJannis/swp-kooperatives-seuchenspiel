package de.uol.swp.common.map.exception;

import de.uol.swp.common.map.City;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.map.GameMap;
import de.uol.swp.common.map.MapType;

/**
 * Exception thrown when the starting {@link Field} of a {@link GameMap} cannot be found
 *
 * @see City
 * @see Field
 * @see GameMap
 * @see MapType
 * @author Tom Weelborg
 * @since 2024-09-02
 */
public class StartingFieldNotFoundException extends RuntimeException {
    public StartingFieldNotFoundException(final String startingCityName) {
        super("The starting field with the city named \"" + startingCityName + "\" cannot be found on this GameMap");
    }
}
