package de.uol.swp.common.lobby;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LobbyStatus {
    OPEN ("offen"),
    FULL ("voll"),
    RUNNING ("im Spiel"),
    OVER ("beendet");

    private final String status;
}
