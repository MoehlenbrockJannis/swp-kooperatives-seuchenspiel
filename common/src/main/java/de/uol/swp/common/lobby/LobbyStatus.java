package de.uol.swp.common.lobby;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LobbyStatus {
    OPEN ("Offen"),
    FULL ("Voll"),
    RUNNING ("Im Spiel");

    private final String status;
}
