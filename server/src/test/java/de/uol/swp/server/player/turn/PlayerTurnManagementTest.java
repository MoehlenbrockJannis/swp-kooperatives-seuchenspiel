package de.uol.swp.server.player.turn;

import de.uol.swp.common.action.ActionFactory;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.map.GameMap;
import de.uol.swp.common.map.MapSlot;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.turn.PlayerTurn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlayerTurnManagementTest {
    private PlayerTurnManagement playerTurnManagement;
    private Game game;

    @BeforeEach
    void setUp() {
        playerTurnManagement = new PlayerTurnManagement();

        game = mock(Game.class);
    }

    @Test
    @DisplayName("Should create a PlayerTurn with all relevant parameters")
    void createPlayerTurn() {
        final Player player = new AIPlayer("bot");

        final GameMap map = mock(GameMap.class);
        final MapSlot mapSlot = mock(MapSlot.class);
        final Field field = new Field(map, mapSlot);
        player.setCurrentField(field);

        final int numberOfAction = 4;
        final int numberOfPlayerCardsToDraw = 5;
        final int numberOfInfectionCardsToDraw = 2;

        final PlayerTurn expected = new PlayerTurn(game, player, numberOfAction, numberOfPlayerCardsToDraw, numberOfInfectionCardsToDraw);

        when(game.getCurrentPlayer())
                .thenReturn(player);
        when(game.getNumberOfActionsPerTurn())
                .thenReturn(numberOfAction);
        when(game.getNumberOfPlayerCardsToDrawPerTurn())
                .thenReturn(numberOfPlayerCardsToDraw);
        when(game.getNumberOfInfectionCardsToDrawPerTurn())
                .thenReturn(numberOfInfectionCardsToDraw);

        final PlayerTurn actual = playerTurnManagement.createPlayerTurn(game);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(ActionFactory.class) // needs to be ignored, otherwise NoClassDefFoundError is thrown
                .isEqualTo(expected);
    }
}