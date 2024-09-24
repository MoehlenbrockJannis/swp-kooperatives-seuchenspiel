package de.uol.swp.common.action.advanced.cure_plague;

import de.uol.swp.common.action.RoleAction;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.plague.Plague;
import lombok.NoArgsConstructor;

/**
 * The {@code IncreasedEffectivenessCurePlagueAction} class represents an enhanced action for curing a plague,
 * with increased effectiveness compared to the standard cure action. It extends {@link CurePlagueAction}
 * and implements {@link RoleAction}, indicating that it is tied to a specific role in the game.
 * <p>
 * This action allows for more efficient plague removal, potentially removing multiple plague cubes.
 * </p>
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
@NoArgsConstructor
public class IncreasedEffectivenessCurePlagueAction extends CurePlagueAction implements RoleAction {

    /**
     * Constructs a new {@code IncreasedEffectivenessCurePlagueAction} with the specified parameters for plague removal.
     *
     * @param removeAllAvailable whether all available plague cubes should be removed
     * @param field the {@link Field} where the plague is located
     * @param plague the {@link Plague} being cured
     */
    public IncreasedEffectivenessCurePlagueAction(boolean removeAllAvailable, Field field, Plague plague) {
        super(removeAllAvailable, field, plague);
    }

    @Override
    public void removeOnePlagueCube() {
        super.removeOnePlagueCube();
    }
}
