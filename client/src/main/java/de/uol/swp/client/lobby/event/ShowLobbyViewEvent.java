package de.uol.swp.client.lobby.event;

public class ShowLobbyViewEvent {
    private final String lobbyName;

    public ShowLobbyViewEvent(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public String getLobbyName() {
        return lobbyName;
    }
}