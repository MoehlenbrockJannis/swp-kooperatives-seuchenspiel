package de.uol.swp.server.plague;

import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.util.Color;
import lombok.Getter;

import java.util.Set;

/**
 * A utility class that defines all plague types.
 *
 * @see Plague
 */
@Getter
public final class Plagues {
    public static final Plague BLUE_PLAGUE = new Plague("Blau", new Color(40, 138, 204));
    public static final Plague BLACK_PLAGUE = new Plague("Schwarz", new Color(89, 91, 97));
    public static final Plague RED_PLAGUE = new Plague("Rot", new Color(255, 46, 23));
    public static final Plague YELLOW_PLAGUE = new Plague("Gelb", new Color(255, 235, 61));

    /**
     * Private constructor to prevent instantiation
     */
    private Plagues() {
    }

    /**
     * Returns a set of all plagues.
     *
     * @return a set containing all {@link Plague} objects
     * @see Plague
     */
    public static Set<Plague> getAllPlagues() {
        return Set.of(
                BLUE_PLAGUE,
                BLACK_PLAGUE,
                RED_PLAGUE,
                YELLOW_PLAGUE
        );
    }
}