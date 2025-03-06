package de.uol.swp.common.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the different reasons why a game can end.
 * Each reason is associated with a display message.
 */
@RequiredArgsConstructor
@Getter
public enum GameEndReason {
    NO_PLAGUE_CUBES_LEFT("Spiel Verloren! Keine Seuchenwürfel mehr verfügbar!"),
    MAX_OUTBREAKS_REACHED("Spiel Verloren! Maximale Anzahl an Ausbrüchen erreicht!"),
    NO_PLAYER_CARDS_LEFT("Spiel Verloren! Keine Spielerkarten mehr verfügbar!"),
    ALL_ANTIDOTES_DISCOVERED("Spiel Gewonnen! Alle Heilmittel erforscht!"),
    PLAYER_LEFT_GAME("Spiel Verloren! Ein Spieler hat das Spiel verlassen!");

    private final String displayMessage;

    @Override
    public String toString() {
        return displayMessage;
    }
}
