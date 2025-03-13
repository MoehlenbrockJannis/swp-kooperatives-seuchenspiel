package de.uol.swp.common.triggerable;

/**
 * Represents a triggerable action or event that is automatically triggered based on certain conditions.
 * <p>
 * The {@code AutoTriggerable} interface extends {@link Triggerable} and adds the functionality to check
 * whether the action or event is currently triggered using the {@link #isTriggered()} method.
 * </p>
 */
public interface AutoTriggerable extends Triggerable {

    /**
     * Checks whether the triggerable action or event is currently triggered.
     *
     * @return {@code true} if the action or event is triggered, {@code false} otherwise.
     */
    boolean isTriggered();
}