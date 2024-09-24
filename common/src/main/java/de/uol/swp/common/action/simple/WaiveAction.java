package de.uol.swp.common.action.simple;

/**
 * The {@code WaiveAction} class represents an action that a player chooses to waive or forgo.
 * It extends {@link SimpleAction} and overrides methods to indicate that the action is not available.
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
public class WaiveAction extends SimpleAction {

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public void execute() {

    }
}

