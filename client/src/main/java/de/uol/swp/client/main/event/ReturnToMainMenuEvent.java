package de.uol.swp.client.main.event;

import de.uol.swp.common.user.User;

public class ReturnToMainMenuEvent {
    private final User user;

    public ReturnToMainMenuEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}