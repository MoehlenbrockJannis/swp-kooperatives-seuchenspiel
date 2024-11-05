package de.uol.swp.server.game;

import de.uol.swp.common.card.InfectionCard;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.map.City;
import de.uol.swp.common.map.MapSlot;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.turn.PlayerTurn;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.player.turn.PlayerTurnManagement;
import de.uol.swp.server.role.RoleManagement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameManagementTest {
    private GameManagement gameManagement;
    private LobbyManagement lobbyManagement;
    private PlayerTurnManagement playerTurnManagement;
    private RoleManagement roleManagement;
    private Lobby mockLobby;
    private MapType mockMapType;
    private List<Plague> mockPlagues;
    private Game mockGame;
    private Player mockPlayer;
    private PlayerCard mockPlayerCard;
    private InfectionCard mockInfectionCard;

    @BeforeEach
    void setUp() {
        lobbyManagement = mock(LobbyManagement.class);
        playerTurnManagement = mock(PlayerTurnManagement.class);
        roleManagement = mock(RoleManagement.class);

        gameManagement = new GameManagement();
        gameManagement.setLobbyManagement(lobbyManagement);
        gameManagement.setPlayerTurnManagement(playerTurnManagement);
        gameManagement.setRoleManagement(roleManagement);

        final City city = new City("city", "info");
        final MapSlot mapSlot = new MapSlot(city, List.of(), null, 0, 0);

        mockLobby = mock(Lobby.class);
        mockMapType = mock(MapType.class);
        when(mockMapType.getMap())
                .thenReturn(List.of(mapSlot));
        when(mockMapType.getStartingCity())
                .thenReturn(city);
        mockPlagues = new ArrayList<>();
        mockGame = mock(Game.class);
        mockPlayer = mock(Player.class);
        mockPlayerCard = mock(PlayerCard.class);
        mockInfectionCard = mock(InfectionCard.class);
    }

    @Test
    @DisplayName("Test creating a game")
    void testCreateGame() {
        final PlayerTurn playerTurn = mock(PlayerTurn.class);

        when(mockLobby.getPlayers()).thenReturn(Set.of(mockPlayer));
        when(playerTurnManagement.createPlayerTurn(any()))
                .thenReturn(playerTurn);

        Game game = gameManagement.createGame(mockLobby, mockMapType, mockPlagues);

        assertThat(game).isNotNull();
        assertThat(game.getLobby()).isEqualTo(mockLobby);
        assertThat(game.getMap()).isNotNull();
        assertThat(game.getPlagues()).isEqualTo(mockPlagues);
        assertThat(game.getPlayersInTurnOrder()).isEqualTo(List.of(mockPlayer));
        assertThat(game.getCurrentTurn())
                .isEqualTo(playerTurn);
    }

    @Test
    @DisplayName("Test adding a game")
    void testAddGame() {
        gameManagement.addGame(mockGame);
        assertThat(gameManagement.getGame(mockGame)).contains(mockGame);
    }

    @Test
    @DisplayName("Test generating unique game ID does not collide with existing IDs")
    void generateUniqueGameId_doesNotCollideWithExistingIds() {
        Game existingGame = new Game( mockLobby, mockMapType, List.of(mockPlayer), mockPlagues);
        gameManagement.addGame(existingGame);

        for (int i = 0; i < 1000; i++) {
            int id = gameManagement.generateUniqueGameId();
            assertThat(id).isNotEqualTo(123456);
        }
    }

    @Test
    @DisplayName("Test getting a game returns the game if found")
    void getGame_returnsGameIfFound() {
        Game game = new Game( mockLobby, mockMapType, List.of(mockPlayer), mockPlagues);
        gameManagement.addGame(game);
        Optional<Game> retrievedGame = gameManagement.getGame(game);
        assertThat(retrievedGame).contains(game);
    }

    @Test
    @DisplayName("Test getting a game returns empty optional if game not found")
    void getGame_returnsEmptyOptionalIfGameNotFound() {
        Game game = new Game(mockLobby, mockMapType, List.of(mockPlayer), mockPlagues);
        gameManagement.addGame(game);
        Optional<Game> retrievedGame = gameManagement.getGame(mockGame);
        assertThat(retrievedGame).isEmpty();
    }

    @Test
    @DisplayName("Test updating an existing game")
    void updateGame_updatesExistingGame() {
        Game game = gameManagement.createGame(mockLobby, mockMapType, mockPlagues);
        gameManagement.addGame(game);
        PlayerCard playerCard = gameManagement.drawPlayerCard(game);

        gameManagement.updateGame(game);
        Game updatedGame = gameManagement.getGame(game).orElseThrow();
        assertThat(gameManagement.getGame(updatedGame)).contains(updatedGame);
    }

    @Test
    @DisplayName("Test drawing a player card")
    void drawPlayerCard() {
        Game game = gameManagement.createGame(mockLobby, mockMapType, mockPlagues);
        gameManagement.addGame(game);
        PlayerCard playerCard = gameManagement.drawPlayerCard(game);
        assertThat(playerCard).isNotNull();
    }

    @Test
    @DisplayName("Test discarding a player card")
    void discardPlayerCard() {
        Game game = gameManagement.createGame(mockLobby, mockMapType, mockPlagues);
        gameManagement.addGame(game);
        gameManagement.discardPlayerCard(game, mockPlayerCard);
        assertThat(game.getPlayerDiscardStack()).contains(mockPlayerCard);
    }

    @Test
    @DisplayName("Test drawing an infection card from the top")
    void drawInfectionCard_fromTheTop() {
        Game game = gameManagement.createGame(mockLobby, mockMapType, mockPlagues);
        gameManagement.addGame(game);
        Optional<Game> optionalGame = gameManagement.getGame(game);
        optionalGame.get().getInfectionDrawStack().push(mockInfectionCard);
        InfectionCard infectionCard = gameManagement.drawInfectionCardFromTheTop(optionalGame.get());

        assertThat(infectionCard).isNotNull();
    }

    @Test
    @DisplayName("Test drawing an infection card from the bottom")
    void drawInfectionCard_fromTheBottom() {
        Game game = gameManagement.createGame(mockLobby, mockMapType, mockPlagues);
        game.getInfectionDrawStack().removeFirstCard();
        game.getInfectionDrawStack().push(mockInfectionCard);
        game.getInfectionDrawStack().push(mock(InfectionCard.class));
        InfectionCard infectionCard = gameManagement.drawInfectionCardFromTheBottom(game);

        assertThat(infectionCard).isNotNull();
        assertThat(infectionCard).isEqualTo(mockInfectionCard);
    }

    @Test
    @DisplayName("Test discarding an infection card")
    void discardInfectionCard() {
        Game game = gameManagement.createGame(mockLobby, mockMapType, mockPlagues);
        gameManagement.addGame(game);
        gameManagement.discardInfectionCard(game, mockInfectionCard);
        assertThat(game.getInfectionDiscardStack()).contains(mockInfectionCard);
    }

}
