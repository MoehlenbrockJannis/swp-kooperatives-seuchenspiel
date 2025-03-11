package de.uol.swp.common.player;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserPlayerTest {

    private User defaultUser;

    private UserPlayer defaultPlayer;

    @BeforeEach
    void setUp() {
        this.defaultUser = new UserDTO("Heinz", "12345", "Heinz@mail.com");
        this.defaultPlayer = new UserPlayer(this.defaultUser);
    }

    @Test
    void shouldReturnUsernameAsPlayerName() {
        assertEquals("Heinz", this.defaultPlayer.getName());
    }

    @Test
    void shouldReturnTrueWhenPlayerContainsUser() {
        assertTrue(this.defaultPlayer.containsUser(this.defaultUser));
    }
}
