package de.uol.swp.common.marker;

import de.uol.swp.common.plague.Plague;

/**
 * The AntidoteMarker class represents a marker specifically used for the antidote
 * of a particular plague in the game. It contains a reference to the associated plague.
 *
 * This class is used to track or indicate the progress of curing a specific plague.
 *
 * @see Marker
 * @since 2024-10-01
 */
public class AntidoteMarker extends Marker{
    private Plague plague;

    /**
     * Constructor for the AntidoteMarker.
     *
     * Initializes the AntidoteMarker with the specified plague. This links the marker
     * to a particular plague instance, which can be used to track or manage the antidote
     * process for that plague.
     *
     * @param plague The plague associated with this AntidoteMarker.
     * @since 2024-10-01
     */
    public AntidoteMarker(Plague plague) {
        this.plague = plague;
    }
}
