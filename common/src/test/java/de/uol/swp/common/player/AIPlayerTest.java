package de.uol.swp.common.player;

import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test class for the AIPlayer class.
 * This class contains test methods to verify the behavior and functionality
 * of the AIPlayer class, including its initialization and user containment features.
 *
 * @see AIPlayer
 * @since 2024-09-18
 */
class AIPlayerTest {

    private AIPlayer defaultAIPlayer;

    /**
     * This method is executed before each test case.
     * It initializes the test environment by setting up a default AI player
     * with the name "Hubert".
     * Ensures that each test starts with a fresh instance of AIPlayer.
     *
     * @since 2024-09-18
     */
    @BeforeEach
    void setUp() {
      this.defaultAIPlayer = new AIPlayer("Hubert");
    }

    /**
     * This test verifies that the AIPlayer is correctly initialized with the given name.
     * It asserts that the name of the default AI player is "Hubert" as expected.
     *
     * @since 2024-09-18
     */
    @Test
    void shouldInitializeAIPlayerWithName() {
        assertEquals("Hubert", this.defaultAIPlayer.getName());
    }


    /**
     * This test checks that the AIPlayer correctly returns false when queried
     * about whether it contains a specific user.
     * It creates two UserDTO instances (user1 and user2) and asserts that
     * the AIPlayer does not contain either of them.
     *
     * @since 2024-09-18
     */
    @Test
    void shouldReturnFalseWhenAIPlayerDoesNotContainUser() {
        final UserDTO user1 = new UserDTO("test1", "", "");
        assertThat(defaultAIPlayer.containsUser(user1)).isFalse();

        final UserDTO user2 = new UserDTO("test2", "", "");
        assertThat(defaultAIPlayer.containsUser(user2)).isFalse();
    }
}
