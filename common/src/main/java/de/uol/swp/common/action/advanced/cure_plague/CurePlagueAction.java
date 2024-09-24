package de.uol.swp.common.action.advanced.cure_plague;

import de.uol.swp.common.action.advanced.AdvancedAction;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.plague.Plague;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Represents an action that allows a player to cure the plague on a given field.
 * This action can either remove one plague cube or all plague cubes from the field,
 * depending on the availability of the option to remove all cubes at once.
 * It extends the {@link AdvancedAction} class and includes methods to execute the curing process.
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
@AllArgsConstructor
@NoArgsConstructor
public class CurePlagueAction extends AdvancedAction {

    /**
     * Determines whether the option to remove all plague cubes from the field is available.
     */
    private boolean removeAllAvailable;

    /**
     * The field where the plague cubes are being cured.
     */
    private Field field;

    /**
     * The specific type of plague to be cured on the field.
     */
    private Plague plague;

    /**
     * Removes one plague cube from the field if any plague is present.
     * It checks if there is a plague on the field and, if so, cures one cube of the specified plague type.
     */
    public void removeOnePlagueCube() {
        if (field.getPlague() != null) {
            field.cure(plague);
        }
    }

    /**
     * Removes all plague cubes from the field, provided that the option to remove all cubes is available.
     * This method iterates through all the plague cubes on the field and cures them one by one.
     */
    public void removeAllPlagueCubes() {
        if (isRemoveAllPlagueCubesAvailable()) {
            while (field.getPlague() != null) {
                field.cure(field.getPlague());
            }
        }
    }

    /**
     * Checks if the option to remove all plague cubes is available.
     * This method returns a boolean indicating if the player can remove all plague cubes at once.
     *
     * @return true if removing all plague cubes is available, false otherwise.
     */
    public boolean isRemoveAllPlagueCubesAvailable() {
        return removeAllAvailable;
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public void execute() {
    }
}