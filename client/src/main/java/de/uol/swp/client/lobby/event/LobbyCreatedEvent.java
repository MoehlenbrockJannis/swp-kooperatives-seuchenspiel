package de.uol.swp.client.lobby.event;

import de.uol.swp.common.user.User;

public class LobbyCreatedEvent {
    private final String lobbyName;
    private final User user;

    public LobbyCreatedEvent(String lobbyName, User user) {
        this.lobbyName = lobbyName;
        this.user = user;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public User getUser() {
        return user;
    }
}