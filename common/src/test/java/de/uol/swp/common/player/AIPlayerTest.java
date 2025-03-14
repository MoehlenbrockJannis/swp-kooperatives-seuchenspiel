package de.uol.swp.common.player;

import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AIPlayerTest {

    private AIPlayer defaultAIPlayer;

    @BeforeEach
    void setUp() {
      this.defaultAIPlayer = new AIPlayer("Hubert");
    }

    @Test
    void shouldInitializeAIPlayerWithName() {
        assertEquals("Hubert", this.defaultAIPlayer.getName());
    }


    @Test
    void shouldReturnFalseWhenAIPlayerDoesNotContainUser() {
        final UserDTO user1 = new UserDTO("test1", "", "");
        assertThat(defaultAIPlayer.containsUser(user1)).isFalse();

        final UserDTO user2 = new UserDTO("test2", "", "");
        assertThat(defaultAIPlayer.containsUser(user2)).isFalse();
    }
}
