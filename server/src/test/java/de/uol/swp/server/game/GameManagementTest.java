package de.uol.swp.server.game;

import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameManagementTest {
    // TODO: Write Tests when configuration is included
    private GameManagement gameManagement;
    private Lobby mockLobby;
    private MapType mockMapType;
    private List<Plague> mockPlagues;
    private Game mockGame;
    private Player mockPlayer;
    private PlayerCard mockPlayerCard;

    @BeforeEach
    void setUp() {
        gameManagement = new GameManagement();
        mockLobby = mock(Lobby.class);
        mockMapType = mock(MapType.class);
        mockPlagues = new ArrayList<>();
        mockGame = mock(Game.class);
        mockPlayer = mock(Player.class);
        mockPlayerCard = mock(PlayerCard.class);
    }

    @Test
    void testCreateGame() {
        when(mockLobby.getPlayers()).thenReturn(Set.of(mockPlayer));
        Game game = gameManagement.createGame(mockLobby, mockMapType, mockPlagues);
        assertThat(game).isNotNull();
        assertThat(game.getLobby()).isEqualTo(mockLobby);
        assertThat(game.getMap()).isNotNull();
        assertThat(game.getPlagues()).isEqualTo(mockPlagues);
        assertThat(game.getPlayersInTurnOrder()).isEqualTo(List.of(mockPlayer));
    }

    @Test
    void testAddGame() {
        gameManagement.addGame(mockGame);
        assertThat(gameManagement.getGame(mockGame)).contains(mockGame);
    }

    @Test
    void generateUniqueGameId_doesNotCollideWithExistingIds() {
        Game existingGame = new Game( mockLobby, mockMapType, List.of(mockPlayer), mockPlagues);
        gameManagement.addGame(existingGame);

        for (int i = 0; i < 1000; i++) {
            int id = gameManagement.generateUniqueGameId();
            assertThat(id).isNotEqualTo(123456);
        }
    }

    @Test
    void getGame_returnsGameIfFound() {
        Game game = new Game( mockLobby, mockMapType, List.of(mockPlayer), mockPlagues);
        gameManagement.addGame(game);
        Optional<Game> retrievedGame = gameManagement.getGame(game);
        assertThat(retrievedGame).contains(game);
    }

    @Test
    void getGame_returnsEmptyOptionalIfGameNotFound() {
        Game game = new Game(mockLobby, mockMapType, List.of(mockPlayer), mockPlagues);
        gameManagement.addGame(game);
        Optional<Game> retrievedGame = gameManagement.getGame(mockGame);
        assertThat(retrievedGame).isEmpty();
    }

    @Test
    void updateGame_updatesExistingGame() {
        Game game = gameManagement.createGame(mockLobby, mockMapType, mockPlagues);
        gameManagement.addGame(game);
        PlayerCard playerCard = gameManagement.drawPlayerCard(game);

        gameManagement.updateGame(game);
        Game updatedGame = gameManagement.getGame(game).orElseThrow();
        assertThat(gameManagement.getGame(updatedGame)).contains(updatedGame);
    }

}
