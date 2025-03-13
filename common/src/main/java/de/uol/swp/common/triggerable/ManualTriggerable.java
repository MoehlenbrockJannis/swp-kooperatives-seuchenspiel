package de.uol.swp.common.triggerable;

import de.uol.swp.common.answerable.Answerable;

/**
 * Represents a triggerable action or event that requires manual interaction or input to be activated.
 * <p>
 * The {@code ManualTriggerable} interface combines the functionalities of {@link Triggerable} and {@link Answerable},
 * allowing for actions or events that depend on explicit user decisions or other forms of manual input.
 * </p>
 */
public interface ManualTriggerable extends Triggerable, Answerable {

}