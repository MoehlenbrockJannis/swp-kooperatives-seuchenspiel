package de.uol.swp.common.action.simple;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.player.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The {@code MoveAction} class serves as a base class for actions that involve moving to a target field.
 * It extends {@link SimpleAction} and provides methods to manage the target field and available movement options.
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
@Getter
@Setter
public abstract class MoveAction extends SimpleAction {
    private Field targetField;

    /**
     * <p>
     *     Returns {@code true} if {@link List} of available fields is not empty and {@code false} if it is empty.
     * </p>
     *
     * {@inheritDoc}
     *
     * @return {@code true} if {@link #getAvailableFields()} is not empty, {@code false} otherwise
     * @see #getAvailableFields()
     */
    @Override
    public boolean isAvailable() {
        return !getAvailableFields().isEmpty();
    }

    @Override
    public boolean isExecutable() {
        return isAvailable() && getAvailableFields().contains(targetField) &&
                !(this instanceof MoveAllyAction moveAllyAction && !moveAllyAction.isApproved());
    }

    /**
     * <p>
     *     If this {@link MoveAction} is executable,
     *     sets the current {@link Field} of the moved {@link Player} to the {@link #targetField}.
     * </p>
     *
     * @throws IllegalStateException if the {@link MoveAction} is not executable
     * @see #isExecutable()
     * @see #getMovedPlayer()
     * @see Player#setCurrentField(Field)
     */
    @Override
    public void execute() {
        if (!isExecutable()) {
            throw new IllegalStateException("This Action may not be executed.");
        }

        final Player player = getMovedPlayer();
        player.setCurrentField(targetField);
    }

    @Override
    public void initWithGame(final Game game) {
        super.initWithGame(game);
        this.targetField = game.findField(this.targetField).orElseThrow();
    }

    /**
     * <p>
     * Returns the current field for the move action.
     * </p>
     *
     * @return the {@link Field} that represents the current position
     */
    public Field getCurrentField() {
        return getMovedPlayer().getCurrentField();
    }

    /**
     * <p>
     * Returns the player moved by this action.
     * </p>
     *
     * @return the moved {@link Player}
     */
    public abstract Player getMovedPlayer();

    /**
     * <p>
     * Returns a list of available fields for movement.
     * </p>
     *
     * @return a list of available fields
     */
    public abstract List<Field> getAvailableFields();
}
