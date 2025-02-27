package de.uol.swp.server.player.turn;

import de.uol.swp.common.action.simple.MoveAllyToAllyAction;
import de.uol.swp.common.action.simple.car.CarActionForAlly;
import de.uol.swp.common.action.simple.charter_flight.CharterFlightActionForAlly;
import de.uol.swp.common.action.simple.direct_flight.DirectFlightActionForAlly;
import de.uol.swp.common.action.simple.shuttle_flight.ShuttleFlightActionForAlly;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.GameDifficulty;
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
import de.uol.swp.common.role.RoleAbility;
import de.uol.swp.common.role.RoleCard;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.util.Color;
import de.uol.swp.server.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

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
        final Lobby lobby = new LobbyDTO("lobby", user);

        final Player player = new AIPlayer("ai");
        RoleCard roleCard = new RoleCard("Test", null, new RoleAbility(
                Map.of(),
                List.of(
                        CarActionForAlly.class,
                        CharterFlightActionForAlly.class,
                        DirectFlightActionForAlly.class,
                        ShuttleFlightActionForAlly.class,
                        MoveAllyToAllyAction.class
                ),
                List.of()
        ));
        player.setRole(roleCard);
        lobby.addPlayer(player);

        final MapType mapType = createMapType();
        final GameDifficulty difficulty = GameDifficulty.getDefault();
        game = new Game(lobby, mapType, new ArrayList<>(lobby.getPlayers()), List.of(), difficulty);

        for (Player p : game.getPlayersInTurnOrder()) {
            if (p.getRole() == null) {
                p.setRole(new RoleCard("TestRole", null, new RoleAbility(
                        Map.of(),
                        List.of(
                                CarActionForAlly.class,
                                CharterFlightActionForAlly.class,
                                DirectFlightActionForAlly.class,
                                ShuttleFlightActionForAlly.class,
                                MoveAllyToAllyAction.class
                        ),
                        List.of()
                )));
            }
        }
    }

    @Test
    @DisplayName("Should create a PlayerTurn with all relevant parameters")
    void createPlayerTurn() {
        final Player player = new AIPlayer("bot");
        RoleCard roleCard = new RoleCard("TestRole", null, new RoleAbility(
                Map.of(),
                List.of(
                        CarActionForAlly.class,
                        CharterFlightActionForAlly.class,
                        DirectFlightActionForAlly.class,
                        ShuttleFlightActionForAlly.class,
                        MoveAllyToAllyAction.class
                ),
                List.of()
        ));
        player.setRole(roleCard);

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

        final PlayerTurn expected = TestUtils.createPlayerTurn(
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
            final PlayerTurn expected = TestUtils.createPlayerTurn(
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