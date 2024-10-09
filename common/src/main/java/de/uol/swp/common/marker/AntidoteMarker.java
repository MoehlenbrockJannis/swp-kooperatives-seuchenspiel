package de.uol.swp.common.marker;

import de.uol.swp.common.plague.Plague;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 * The AntidoteMarker class represents a marker specifically used for the antidote
 * of a particular plague in the game. It contains a reference to the associated plague.
 *
 * <p>
 * This class is used to track or indicate the progress of curing a specific plague.
 *
 * @see Marker
 * @since 2024-10-01
 */
@AllArgsConstructor
@Getter
public class AntidoteMarker extends Marker{
    private Plague plague;
}
