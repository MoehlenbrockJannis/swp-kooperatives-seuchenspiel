package de.uol.swp.common.map.exception;

import de.uol.swp.common.map.Field;
import de.uol.swp.common.map.City;

/**
 * Exception thrown when the {@link Field} for a given {@link City} has not been found
 *
 * @author David Scheffler
 * @see Field
 * @see City
 *
 * @since 2024-12-01
 */
public class FieldOfCityNotFoundException extends RuntimeException {
    public FieldOfCityNotFoundException(final String cityName) {
        super("There is no field for the city \"" + cityName + "\"");
    }
}

