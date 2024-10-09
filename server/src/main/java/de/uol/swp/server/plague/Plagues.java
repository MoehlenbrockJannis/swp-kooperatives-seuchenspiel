package de.uol.swp.server.plague;

import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.util.Color;
import lombok.Getter;

import java.util.Set;

/**
 * A utility class that defines all plague types.
 *
 * @see Plague
 * @author David Scheffler
 * @since 2024-09-22
 */
@Getter
public final class Plagues {
    public static final Plague BLUE_PLAGUE = new Plague("Blau", new Color(0, 0, 255));
    public static final Plague BLACK_PLAGUE = new Plague("Schwarz", new Color(0, 0, 0));
    public static final Plague RED_PLAGUE = new Plague("Rot", new Color(255, 0, 0));
    public static final Plague YELLOW_PLAGUE = new Plague("Gelb", new Color(255, 255, 0));

    /**
     * Private constructor to prevent instantiation
     *
     * @author David Scheffler
     * @since 2024-09-22
     */
    private Plagues() {
    }

    /**
     * Returns a set of all plagues.
     *
     * @return a set containing all {@link Plague} objects
     * @see Plague
     * @author David Scheffler
     * @since 2024-09-22
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