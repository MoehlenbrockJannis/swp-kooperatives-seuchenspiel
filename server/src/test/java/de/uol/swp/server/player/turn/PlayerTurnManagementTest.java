package de.uol.swp.server.player.turn;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.map.GameMap;
import de.uol.swp.common.map.MapSlot;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.turn.PlayerTurn;
import de.uol.swp.common.util.Color;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import java.util.ArrayList;
import java.util.List;

import static de.uol.swp.server.util.TestUtils.createMapType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlayerTurnManagementTest {
    private PlayerTurnManagement playerTurnManagement;
    private Game game;

    @BeforeEach
    void setUp() {
        playerTurnManagement = new PlayerTurnManagement();

        final User user = new UserDTO("user", "pass", "");
        final Lobby lobby = new LobbyDTO("lobby", user, 1, 2);

        final Player player = new AIPlayer("ai");
        lobby.addPlayer(player);

        final MapType mapType = createMapType();

        game = new Game(lobby, mapType, new ArrayList<>(lobby.getPlayers()), List.of());
    }

    @Test
    @DisplayName("Should create a PlayerTurn with all relevant parameters")
    void createPlayerTurn() {
        final Player player = new AIPlayer("bot");

        final Set<Plague> plagueSet = new HashSet<>();
        plagueSet.add(new Plague("testPlague", new Color(1, 2, 3)));

        final MapType mapType = mock(MapType.class);
        when(mapType.getUniquePlagues())
                .thenReturn(plagueSet);
        final GameMap map = mock(GameMap.class);
        when(map.getType())
                .thenReturn(mapType);
        final MapSlot mapSlot = mock(MapSlot.class);
        final Field field = new Field(map, mapSlot);
        player.setCurrentField(field);

        final int numberOfAction = 4;
        final int numberOfPlayerCardsToDraw = 5;
        final int numberOfInfectionCardsToDraw = 2;

        final PlayerTurn expected = new PlayerTurn(
                game,
                game.getPlayersInTurnOrder().get(0),
                game.getNumberOfActionsPerTurn(),
                game.getNumberOfPlayerCardsToDrawPerTurn(),
                game.getNumberOfInfectionCardsToDrawPerTurn()
        );

        final PlayerTurn actual = playerTurnManagement.createPlayerTurn(game);

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Should start a new PlayerTurn with all relevant parameters")
    void startNewPlayerTurn() {
        final int numberOfPlayers = game.getPlayersInTurnOrder().size();
        for (int i = 0; i < numberOfPlayers * 2; i++) {
            final PlayerTurn expected = new PlayerTurn(
                    game,
                    game.getPlayersInTurnOrder().get((i + 1) % numberOfPlayers),
                    game.getNumberOfActionsPerTurn(),
                    game.getNumberOfPlayerCardsToDrawPerTurn(),
                    game.getNumberOfInfectionCardsToDrawPerTurn()
            );

            playerTurnManagement.startNewPlayerTurn(game);

            final PlayerTurn actual = game.getCurrentTurn();

            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);
        }
    }
}