package de.uol.swp.common.action.advanced.cure_plague;

import de.uol.swp.common.action.advanced.AdvancedAction;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.plague.PlagueCube;
import de.uol.swp.common.player.Player;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an action that allows a player to cure the plague on a given field.
 * This action can either remove one plague cube or all plague cubes from the field,
 * depending on the availability of the option to remove all cubes at once.
 * It extends the {@link AdvancedAction} class and includes methods to execute the curing process.
 */
@Getter
@Setter
public class CurePlagueAction extends AdvancedAction {
    /**
     * The specific type of plague to be cured on the field.
     */
    private Plague plague;

    @Override
    public String toString() {
        return "Seuche behandeln";
    }

    /**
     * Removes one plague cube from the field if any plague is present.
     * It checks if there is a plague on the field and, if so, cures one cube of the specified plague type.
     */
    public void removeOnePlagueCube() {
        final Game game = getGame();
        final Field currentField = getExecutingPlayer().getCurrentField();

        if (currentField.isCurable(plague)) {
            PlagueCube curedCube = currentField.cure(plague);
            game.addPlagueCube(curedCube);
        }
    }

    /**
     * Checks if the option to remove all plague cubes is available.
     * This method returns a boolean indicating if the player can remove all plague cubes at once.
     *
     * @return true if removing all plague cubes is available, false otherwise.
     */
    public boolean isRemoveAllPlagueCubesAvailable() {
        boolean hasAntidoteMarkerForPlague = getGame().hasAntidoteMarkerForPlague(plague);
        boolean isDoctor = getExecutingPlayer().getRole().getName().equals("Arzt");
        return hasAntidoteMarkerForPlague || isDoctor;
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
     * @see #removeOnePlagueCube()
     */
    @Override
    public void execute() {
        if (!isExecutable()) {
            throw new IllegalStateException("This Action may not be executed.");
        }

        Game game = getGame();
        Field field = getExecutingPlayer().getCurrentField();

        if (isRemoveAllPlagueCubesAvailable()) {
            field.removeAllPlagueCubes(plague, field, game);
        } else {
            removeOnePlagueCube();
        }
    }
}