package de.uol.swp.common.player;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test class for the UserPlayer class.
 * This class contains test methods to verify the functionality and behavior
 * of the UserPlayer class, including name retrieval and user containment.
 *
 * It initializes the test environment by creating a default UserDTO and UserPlayer
 * before each test.
 *
 * The tests cover the following scenarios:
 * - Correct retrieval of the username as the player's name
 * - Proper behavior of the containsUser method to check if the player contains a specific user
 *
 * @see UserPlayer
 * @since 2024-09-18
 */
class UserPlayerTest {

    private User defaultUser;

    private UserPlayer defaultPlayer;

    /**
     * This method is executed before each test case.
     * It initializes the test environment by creating a default UserDTO and UserPlayer.
     * Specifically, it sets up:
     * - defaultUser with the name "Heinz", ID "12345", and email "Heinz@mail.com"
     * - defaultPlayer as a UserPlayer instance based on the defaultUser
     *
     * This setup ensures that each test starts with a consistent and predefined state.
     *
     * @since 2024-09-18
     */
    @BeforeEach
    void setUp() {
        this.defaultUser = new UserDTO("Heinz", "12345", "Heinz@mail.com");
        this.defaultPlayer = new UserPlayer(this.defaultUser);
    }

    /**
     * This test verifies that the getName method of UserPlayer correctly returns
     * the username as the player's name.
     * It asserts that the name of defaultPlayer, initialized with the username "Heinz",
     * is returned as "Heinz".
     *
     * @since 2024-09-18
     */
    @Test
    void shouldReturnUsernameAsPlayerName() {
        assertEquals("Heinz", this.defaultPlayer.getName());
    }

    /**
     * This test verifies that the containsUser method correctly returns true
     * when the UserPlayer contains the specified UserDTO.
     * It asserts that defaultPlayer, initialized with defaultUser, correctly
     * identifies that it contains defaultUser.
     *
     * @since 2024-09-18
     */
    @Test
    void shouldReturnTrueWhenPlayerContainsUser() {
        assertTrue(this.defaultPlayer.containsUser(this.defaultUser));
    }
}
