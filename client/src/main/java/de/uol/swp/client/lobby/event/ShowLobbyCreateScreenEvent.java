package de.uol.swp.client.lobby.event;

import de.uol.swp.common.user.User;

public class ShowLobbyCreateScreenEvent {
    private final User user;

    public ShowLobbyCreateScreenEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}