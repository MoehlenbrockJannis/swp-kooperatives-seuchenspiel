package de.uol.swp.common.action.simple.car;

import de.uol.swp.common.action.simple.AbstractMoveActionTest;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.Player;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CarActionTest extends AbstractMoveActionTest {
    @Getter
    private CarAction action;
    private Player player;

    @BeforeEach
    void setUp() {
        player = new AIPlayer("testerbot");

        action = new CarAction();
        action.setExecutingPlayer(player);

        setUpFields();
    }

    @Test
    @DisplayName("Should return the executing player")
    void getMovedPlayer() {
        assertThat(action.getMovedPlayer())
                .usingRecursiveComparison()
                .isEqualTo(player);
    }

    @Test
    @DisplayName("Should return a list of all fields neighboring the current field")
    void getAvailableFields() {
        action.getExecutingPlayer().setCurrentField(currentField);

        assertThat(getAction().getAvailableFields())
                .usingRecursiveComparison()
                .isEqualTo(neighboringFields);
    }
}