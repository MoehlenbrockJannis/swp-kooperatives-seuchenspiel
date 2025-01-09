package de.uol.swp.common.action.simple;

import de.uol.swp.common.action.ActionFactory;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.turn.PlayerTurn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
        try (MockedConstruction<ActionFactory> mockActionFactory = Mockito.mockConstruction(ActionFactory.class, (mock, context) -> {
            when(mock.createAllGeneralActionsExcludingSomeAndIncludingSomeRoleActions(any(), any()))
                    .thenReturn(List.of());
        })) {
            playerTurn = new PlayerTurn(game, new AIPlayer("ai"), numberOfActionsToDo, 0, 0);
        }

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