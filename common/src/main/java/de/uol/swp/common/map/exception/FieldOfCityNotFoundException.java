package de.uol.swp.common.map.exception;

import de.uol.swp.common.map.City;
import de.uol.swp.common.map.Field;

/**
 * Exception thrown when the {@link Field} for a given {@link City} has not been found
 *

 * @see Field
 * @see City
 */
public class FieldOfCityNotFoundException extends RuntimeException {
    public FieldOfCityNotFoundException(final String cityName) {
        super("There is no field for the city \"" + cityName + "\"");
    }
}

