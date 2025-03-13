package de.uol.swp.common.map.exception;

import de.uol.swp.common.map.Field;
import de.uol.swp.common.map.research_laboratory.ResearchLaboratory;

/**
 * Exception thrown when there is already a {@link ResearchLaboratory} on a {@link Field}
 *
 * @see Field
 * @see ResearchLaboratory
 */
public class ResearchLaboratoryAlreadyBuiltOnFieldException extends RuntimeException {
    public ResearchLaboratoryAlreadyBuiltOnFieldException(final String fieldName) {
        super("There is already a ResearchLaboratory on Field \"" + fieldName + "\"");
    }
}
