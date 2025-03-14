package de.uol.swp.common.util;

import java.io.Serializable;

/**
 * The {@code Command} interface represents an executable action or task.
 * It provides a single method to execute the command.
 */
public interface Command extends Serializable {

    /**
     * <p>
     * Executes the command.
     * </p>
     */
    void execute();
}

