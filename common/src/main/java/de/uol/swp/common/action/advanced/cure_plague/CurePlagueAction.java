package de.uol.swp.common.action.advanced.cure_plague;

import de.uol.swp.common.action.advanced.AdvancedAction;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.player.Player;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an action that allows a player to cure the plague on a given field.
 * This action can either remove one plague cube or all plague cubes from the field,
 * depending on the availability of the option to remove all cubes at once.
 * It extends the {@link AdvancedAction} class and includes methods to execute the curing process.
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
@Getter
@Setter
public class CurePlagueAction extends AdvancedAction {
    /**
     * The specific type of plague to be cured on the field.
     */
    private Plague plague;

    /**
     * Removes one plague cube from the field if any plague is present.
     * It checks if there is a plague on the field and, if so, cures one cube of the specified plague type.
     */
    public void removeOnePlagueCube() {
        final Field currentField = getExecutingPlayer().getCurrentField();
        final Game game = getGame();
        if (currentField.isCurable(plague)) {
            game.addPlagueCube(currentField.cure(plague));
        }
    }

    /**
     * Removes all plague cubes from the field, provided that the option to remove all cubes is available.
     * This method iterates through all the plague cubes on the field and cures them one by one.
     */
    public void removeAllPlagueCubes() {
        final Field currentField = getExecutingPlayer().getCurrentField();
        final Game game = getGame();
        while (currentField.isCurable(plague)) {
            game.addPlagueCube(currentField.cure(plague));
        }
    }

    /**
     * Checks if the option to remove all plague cubes is available.
     * This method returns a boolean indicating if the player can remove all plague cubes at once.
     *
     * @return true if removing all plague cubes is available, false otherwise.
     */
    public boolean isRemoveAllPlagueCubesAvailable() {
        return getGame().hasAntidoteMarkerForPlague(plague);
    }

    /**
     * <p>
     *     Returns {@code true} if the current {@link Field} is infected with any {@link Plague}.
     * </p>
     *
     * {@inheritDoc}
     *
     * @return {@code true} if the current {@link Field} is infected with any {@link Plague}, {@code false} otherwise
     * @see #getExecutingPlayer()
     * @see Player#getCurrentField()
     * @see Game#getPlagues()
     * @see Field#isCurable(Plague)
     */
    @Override
    public boolean isAvailable() {
        final Field currentField = getExecutingPlayer().getCurrentField();
        final Game game = getGame();
        for (final Plague plagueOnGame : game.getPlagues()) {
            if (currentField.isCurable(plagueOnGame)) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>
     *     A {@link CurePlagueAction} is executable if it is available.
     * </p>
     *
     * @return {@code true} if it is available, {@code false} otherwise
     * @see #isAvailable()
     */
    @Override
    public boolean isExecutable() {
        return isAvailable();
    }

    /**
     * <p>
     *     If this {@link CurePlagueAction} is executable,
     *     removes either one or all plague cubes from the current {@link Field}.
     * </p>
     *
     * @throws IllegalStateException if the {@link CurePlagueAction} is not executable
     * @see #isExecutable()
     * @see #isRemoveAllPlagueCubesAvailable()
     * @see #removeAllPlagueCubes()
     * @see #removeOnePlagueCube()
     */
    @Override
    public void execute() {
        if (!isExecutable()) {
            throw new IllegalStateException("This Action may not be executed.");
        }

        if (isRemoveAllPlagueCubesAvailable()) {
            removeAllPlagueCubes();
        } else {
            removeOnePlagueCube();
        }
    }
}