package de.uol.swp.common.action.simple;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.game.turn.PlayerTurn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static de.uol.swp.common.util.TestUtils.createPlayerTurn;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WaiveActionTest {
    private WaiveAction action;
    private Game game;
    private PlayerTurn playerTurn;
    private int numberOfActionsToDo;

    @BeforeEach
    void setUp() {
        numberOfActionsToDo = 10;

        game = mock();
        playerTurn = createPlayerTurn(
                game,
                new AIPlayer("ai"),
                numberOfActionsToDo,
                0,
                0
        );

        when(game.getCurrentTurn())
                .thenReturn(playerTurn);

        action = new WaiveAction();
        action.setGame(game);
    }

    @Test
    @DisplayName("Should always be available")
    void isAvailable() {
        assertThat(action.isAvailable())
                .isTrue();
    }

    @Test
    @DisplayName("Should always be executable")
    void isExecutable() {
        assertThat(action.isExecutable())
                .isTrue();
    }

    @Test
    @DisplayName("Should set the number of actions to do to 0")
    void execute() {
        assertThat(playerTurn.getNumberOfActionsToDo())
                .isEqualTo(numberOfActionsToDo);

        action.execute();

        assertThat(playerTurn.getNumberOfActionsToDo())
                .isZero();
    }
}