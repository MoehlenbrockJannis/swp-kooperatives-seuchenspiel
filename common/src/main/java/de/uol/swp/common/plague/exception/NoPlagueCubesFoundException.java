package de.uol.swp.common.plague.exception;

/**
 * Exception thrown when {@link de.uol.swp.common.plague.PlagueCube}s of a given {@link de.uol.swp.common.plague.Plague} could not be found
 *
 * @author David Scheffler
 * @see de.uol.swp.common.game.Game
 * @since 2025-01-08
 */
public class NoPlagueCubesFoundException extends RuntimeException {
    public NoPlagueCubesFoundException(final String plagueName) {
        super("Could not find plague cubes of the plague \"" + plagueName + "\"");
    }
}

