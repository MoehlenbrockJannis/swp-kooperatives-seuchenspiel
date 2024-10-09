package de.uol.swp.common.map.exception;

import de.uol.swp.common.map.Field;
import de.uol.swp.common.map.research_laboratory.ResearchLaboratory;

/**
 * Exception thrown when there is already a {@link ResearchLaboratory} on a {@link Field}
 *
 * @see Field
 * @see ResearchLaboratory
 * @author Tom Weelborg
 * @since 2024-09-02
 */
public class ResearchLaboratoryAlreadyBuiltOnFieldException extends RuntimeException {
    public ResearchLaboratoryAlreadyBuiltOnFieldException(final String fieldName) {
        super("There is already a ResearchLaboratory on Field \"" + fieldName + "\"");
    }
}
