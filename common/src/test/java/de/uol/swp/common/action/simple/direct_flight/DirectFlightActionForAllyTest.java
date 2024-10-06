package de.uol.swp.common.action.simple.direct_flight;

import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.UserPlayer;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DirectFlightActionForAllyTest {
    private DirectFlightActionForAlly action;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        player1 = new AIPlayer("boeing pilot");
        player2 = new UserPlayer(new UserDTO("super user", "secret", ""));

        action = new DirectFlightActionForAlly();
        action.setExecutingPlayer(player2);
        action.setMovedAlly(player1);
    }

    @Test
    @DisplayName("Should return moved ally")
    void getApprovingPlayer() {
        assertThat(action.getApprovingPlayer())
                .usingRecursiveComparison()
                .isEqualTo(player1);
    }

    @Test
    @DisplayName("Should set the approval status to true")
    void approve() {
        assertThat(action.isApproved())
                .isFalse();

        action.approve();

        assertThat(action.isApproved())
                .isTrue();
    }

    @Test
    @DisplayName("Should return moved ally")
    void getMovedPlayer() {
        assertThat(action.getMovedPlayer())
                .usingRecursiveComparison()
                .isEqualTo(player1);
    }

    @Test
    @DisplayName("Should return specified moved ally")
    void getMovedAlly() {
        assertThat(action.getMovedAlly())
                .usingRecursiveComparison()
                .isEqualTo(player1);
    }

    @Test
    @DisplayName("Should return approval status")
    void isApproved() {
        assertThat(action.isApproved())
                .isFalse();
    }

    @Test
    @DisplayName("Should set given player as specified moved ally")
    void setMovedAlly() {
        assertThat(action.getMovedAlly())
                .usingRecursiveComparison()
                .isEqualTo(player1);

        action.setMovedAlly(player2);

        assertThat(action.getMovedAlly())
                .usingRecursiveComparison()
                .isEqualTo(player2);
    }
}