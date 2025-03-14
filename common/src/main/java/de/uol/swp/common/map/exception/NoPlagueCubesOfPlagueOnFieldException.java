package de.uol.swp.common.map.exception;

import de.uol.swp.common.map.Field;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.plague.PlagueCube;

/**
 * Exception thrown when there are no plague cubes of the given plague on a field
 *
 * @see Field
 * @see Plague
 * @see PlagueCube
 */
public class NoPlagueCubesOfPlagueOnFieldException extends RuntimeException {
    public NoPlagueCubesOfPlagueOnFieldException(final String plagueName, final String fieldName) {
        super("There are no plague cubes of \"" + plagueName + "\" on Field \"" + fieldName + "\"");
    }
}
