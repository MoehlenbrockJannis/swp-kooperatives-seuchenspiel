package de.uol.swp.common.action;

/**
 * The {@code Command} interface represents an executable action or task.
 * It provides a single method to execute the command.
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
public interface Command {

    /**
     * Executes the command.
     */
    void execute();
}

