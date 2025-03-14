package de.uol.swp.common.plague;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * Represents a plague cube in the game.
 * <p>
 * A plague cube is linked to a specific {@link Plague} and is used to represent the spread of the plague on the game board.
 * Plague cubes are placed on fields and are a critical mechanic in managing outbreaks during the game.
 * </p>
 */
@AllArgsConstructor
@Getter
public class PlagueCube implements Serializable {
    private Plague plague;
}
