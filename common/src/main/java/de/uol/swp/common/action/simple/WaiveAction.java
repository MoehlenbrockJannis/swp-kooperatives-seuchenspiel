package de.uol.swp.common.action.simple;

/**
 * The {@code WaiveAction} class represents an action that a player chooses to waive or forgo.
 * It extends {@link SimpleAction}.
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
public class WaiveAction extends SimpleAction {

    /**
     * <p>
     * Always {@code true}
     * </p>
     *
     * {@inheritDoc}
     *
     * @return {@code true}
     */
    @Override
    public boolean isAvailable() {
        return true;
    }

    /**
     * <p>
     * Always {@code true}
     * </p>
     *
     * {@inheritDoc}
     *
     * @return {@code true}
     */
    @Override
    public boolean isExecutable() {
        return true;
    }

    /**
     * <p>
     * Does nothing.
     * </p>
     *
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        if (isExecutable()) {
            getGame().getCurrentTurn().setNumberOfActionsToDo(0);
        }
    }
}

