package de.uol.swp.server.game;

import de.uol.swp.common.card.InfectionCard;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.GameDifficulty;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.UserPlayer;
import de.uol.swp.common.player.turn.PlayerTurn;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.game.store.GameStore;
import de.uol.swp.server.game.store.MainMemoryBasedGameStore;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.player.turn.PlayerTurnManagement;
import de.uol.swp.server.role.RoleManagement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static de.uol.swp.server.util.TestUtils.createMapType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameManagementTest {
    private GameManagement gameManagement;
    private LobbyManagement lobbyManagement;
    private PlayerTurnManagement playerTurnManagement;
    private RoleManagement roleManagement;
    private Lobby mockLobby;
    private MapType mapType;
    private List<Plague> mockPlagues;
    private Game mockGame;
    private Player mockPlayer;
    private Player mockPlayer2;
    private PlayerCard mockPlayerCard;
    private InfectionCard mockInfectionCard;
    private GameDifficulty difficulty;

    @BeforeEach
    void setUp() {
        lobbyManagement = mock(LobbyManagement.class);
        playerTurnManagement = mock(PlayerTurnManagement.class);
        roleManagement = mock(RoleManagement.class);
        GameStore gameStore = mock(MainMemoryBasedGameStore.class);

        gameManagement = new GameManagement(gameStore);
        gameManagement.setLobbyManagement(lobbyManagement);
        gameManagement.setPlayerTurnManagement(playerTurnManagement);
        gameManagement.setRoleManagement(roleManagement);

        mockLobby = mock(Lobby.class);
        mapType = createMapType();
        mockPlagues = new ArrayList<>();
        mockGame = mock(Game.class);
        mockPlayer = mock(Player.class);
        mockPlayer2 = mock(Player.class);
        mockPlayerCard = mock(PlayerCard.class);
        mockInfectionCard = mock(InfectionCard.class);
        mockLobby.addPlayer(mockPlayer);
        mockLobby.addPlayer(mockPlayer2);
        difficulty = GameDifficulty.getDefault();
    }

    @Test
    @DisplayName("Test creating a game")
    void testCreateGame() {
        final PlayerTurn playerTurn = mock(PlayerTurn.class);
        User user1 = new UserDTO("user", "pass", "");
        User user2 = new UserDTO("user2", "pass2", "user2");
        Player player1 = new UserPlayer(user1);
        Player player2 = new UserPlayer(user2);

        Lobby lobby = new LobbyDTO("lobby", user1);
        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        when(playerTurnManagement.createPlayerTurn(any()))
                .thenReturn(playerTurn);

        Game game = gameManagement.createGame(lobby, mapType, mockPlagues, difficulty);

        assertThat(game).isNotNull();
        assertThat(game.getLobby()).isEqualTo(lobby);
        assertThat(game.getMap()).isNotNull();
        assertThat(game.getPlagues()).isEqualTo(mockPlagues);
        assertThat(game.getPlayersInTurnOrder()).contains(player1, player2);
        assertThat(game.getCurrentTurn())
                .isEqualTo(playerTurn);
    }

    @Test
    @DisplayName("Test adding a game")
    void testAddGame() {
        gameManagement.addGame(mockGame);

        when(gameManagement.getGame(mockGame)).thenReturn(Optional.of(mockGame));

        assertThat(gameManagement.getGame(mockGame)).contains(mockGame);
    }

    @Test
    @DisplayName("Test getting a game returns the game if found")
    void getGame_returnsGameIfFound() {
        Game game = new Game( mockLobby, mapType, List.of(mockPlayer, mockPlayer2), mockPlagues, difficulty);
        gameManagement.addGame(game);

        when(gameManagement.getGame(game)).thenReturn(Optional.of(game));

        Optional<Game> retrievedGame = gameManagement.getGame(game);
        assertThat(retrievedGame).contains(game);
    }

    @Test
    @DisplayName("Test getting a game returns empty optional if game not found")
    void getGame_returnsEmptyOptionalIfGameNotFound() {
        Game game = new Game(mockLobby, mapType, List.of(mockPlayer, mockPlayer2), mockPlagues, difficulty);
        game.setId(123456);
        gameManagement.addGame(game);
        Optional<Game> retrievedGame = gameManagement.getGame(mockGame);
        assertThat(retrievedGame).isEmpty();
    }

    @Test
    @DisplayName("Test updating an existing game")
    void updateGame_updatesExistingGame() {
        User user1 = new UserDTO("user", "pass", "");
        User user2 = new UserDTO("user2", "pass2", "user2");
        Player player1 = new UserPlayer(user1);
        Player player2 = new UserPlayer(user2);

        Lobby lobby = new LobbyDTO("lobby", user1);
        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game game = gameManagement.createGame(lobby, mapType, mockPlagues, difficulty);
        gameManagement.addGame(game);

        when(gameManagement.getGame(game)).thenReturn(Optional.of(game));

        gameManagement.updateGame(game);
        Game updatedGame = gameManagement.getGame(game).orElseThrow();
        assertThat(gameManagement.getGame(updatedGame)).contains(updatedGame);
    }
}
