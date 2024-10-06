package de.uol.swp.common.action.advanced.cure_plague;

import de.uol.swp.common.action.RoleAction;

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
public class IncreasedEffectivenessCurePlagueAction extends CurePlagueAction implements RoleAction {

    /**
     * <p>
     *     Always true
     * </p>
     *
     * {@inheritDoc}
     *
     * @return true
     */
    @Override
    public boolean isRemoveAllPlagueCubesAvailable() {
        return true;
    }
}
