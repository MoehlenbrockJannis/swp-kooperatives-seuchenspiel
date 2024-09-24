package de.uol.swp.common.action.simple;

import de.uol.swp.common.action.simple.SimpleAction;
import de.uol.swp.common.map.Field;
import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * The {@code MoveAction} class serves as a base class for actions that involve moving to a target field.
 * It extends {@link SimpleAction} and provides methods to manage the target field and available movement options.
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
@NoArgsConstructor
@AllArgsConstructor
public abstract class MoveAction extends SimpleAction {

    @Setter
    private Field targetField;

    /**
     * Returns the target field for the move action.
     *
     * @return the {@link Field} that is the target for this action
     */
    public Field getTargetField() {
        return this.targetField;
    }

    /**
     * Returns the current field for the move action.
     *
     * @return the {@link Field} that represents the current position
     */
    public Field getCurrentField() {
        return null;
    }

    /**
     * Returns a list of available fields for movement.
     *
     * @return an empty list, as this method can be overridden in subclasses
     */
    public List<Field> getAvailableFields() {
        return Collections.emptyList();
    }
}
