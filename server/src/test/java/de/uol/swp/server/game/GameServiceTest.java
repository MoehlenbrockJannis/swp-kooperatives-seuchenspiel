package de.uol.swp.server.game;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.GameDifficulty;
import de.uol.swp.common.game.GameEndReason;
import de.uol.swp.common.game.request.CreateGameRequest;
import de.uol.swp.common.game.request.LeaveGameRequest;
import de.uol.swp.common.game.server_message.CreateGameServerMessage;
import de.uol.swp.common.game.server_message.RetrieveUpdatedGameServerMessage;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.request.LogoutRequest;
import de.uol.swp.server.EventBusBasedTest;
import de.uol.swp.server.chat.message.SystemLobbyMessageServerInternalMessage;
import de.uol.swp.server.communication.AISession;
import de.uol.swp.server.game.message.GameStateChangedInternalMessage;
import de.uol.swp.server.lobby.LobbyService;
import de.uol.swp.server.player.PlayerManagement;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.uol.swp.server.util.TestUtils.createMapType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class GameServiceTest extends EventBusBasedTest {
    private GameService gameService;
    private GameManagement gameManagement;
    private LobbyService lobbyService;
    private PlayerManagement playerManagement;

    private Game game;
    private Lobby lobby;
    private MapType mapType;
    private List<Plague> plagues;
    private User user;
    private AIPlayer aiPlayer;
    private GameDifficulty difficulty;

    @BeforeEach
    void setUp() {
        gameManagement = mock();
        lobbyService = mock();
        playerManagement = mock();
        final EventBus eventBus = getBus();
        difficulty = GameDifficulty.getDefault();

        gameService = new GameService(eventBus, gameManagement, lobbyService, playerManagement);

        user = new UserDTO("user", "pass", "");
        lobby = new LobbyDTO("lobby", user);

        final User user2 = new UserDTO("user2", "pass2", "user2");
        lobby.joinUser(user2);

        aiPlayer = new AIPlayer("ai");
        lobby.addPlayer(aiPlayer);

        mapType = createMapType();
        plagues = List.of();
        final MapType mapType = createMapType();
        final List<Plague> plagues = List.of();
        final GameDifficulty difficulty = GameDifficulty.getDefault();

        game = new Game(lobby, mapType, new ArrayList<>(lobby.getPlayers()), plagues, difficulty);

        when(gameManagement.createGame(lobby, mapType, plagues, difficulty))
                .thenReturn(game);

        doAnswer(invocationOnMock -> {
            getBus().post(invocationOnMock.getArguments()[1]);
            return null;
        }).when(lobbyService).sendToAllInLobby(eq(lobby), any());
    }

    @Test
    @DisplayName("Should create a game from the CreateGameRequest and return the game with a CreateGameServerMessage")
    void onCreateGameRequest() throws InterruptedException {
        final CreateGameRequest createGameRequest = new CreateGameRequest(lobby, mapType, plagues, difficulty);
        post(createGameRequest);

        waitForLock();

        verify(gameManagement, times(1))
                .addGame(game);
        assertThat(event)
                .isInstanceOf(CreateGameServerMessage.class);
        assertThat(((CreateGameServerMessage) event).getGame())
                .usingRecursiveComparison()
                .isEqualTo(game);
    }

    @Test
    @DisplayName("Should send an update of given game and update it in management")
    void sendGameUpdate() {
        gameService.sendGameUpdate(game);

        verify(gameManagement, times(1))
                .updateGame(game);

        assertThat(event)
                .isInstanceOf(RetrieveUpdatedGameServerMessage.class);
        final RetrieveUpdatedGameServerMessage retrieveUpdatedGameServerMessage = (RetrieveUpdatedGameServerMessage) event;
        assertThat(retrieveUpdatedGameServerMessage.getGame())
                .usingRecursiveComparison()
                .isEqualTo(game);
    }

    @Test
    @DisplayName("Should return an Optional with session for an AIPlayer")
    void getSession_present() {
        final CreateGameRequest createGameRequest = new CreateGameRequest(lobby, mapType, plagues, difficulty);
        post(createGameRequest);

        when(playerManagement.findSession(aiPlayer))
                .thenReturn(Optional.of(AISession.createAISession(aiPlayer)));

        assertThat(gameService.getSession(aiPlayer))
                .isPresent();
    }

    @Test
    @DisplayName("Should return an empty Optional if there is no session for an AIPlayer")
    void getSession_empty() {
        assertThat(gameService.getSession(aiPlayer))
                .isEmpty();
    }

    @Test
    @DisplayName("Should react to a leave game request and post a GameStateChangedInternalMessage")
    void onLeaveGameRequestTest() throws InterruptedException {
        assertFalse(game.isGameLost());

        LeaveGameRequest request = new LeaveGameRequest(game, aiPlayer);
        postAndWait(request);

        assertThat(this.event)
                .isInstanceOf(SystemLobbyMessageServerInternalMessage.class);

        assertTrue(game.isGameLost());
    }

    @Test
    @DisplayName("Should react to a game state change and post a GameStateChangedInternalMessage")
    void onGameStateChangedTest() throws InterruptedException {
        GameStateChangedInternalMessage gameStateChangedInternalMessage = new GameStateChangedInternalMessage(game, GameEndReason.PLAYER_LEFT_GAME);
        postAndWait(gameStateChangedInternalMessage);

        assertThat(this.event)
                .isInstanceOf(GameStateChangedInternalMessage.class);
    }

    @Subscribe
    public void onEvent(final CreateGameServerMessage createGameServerMessage) {
        handleEvent(createGameServerMessage);
    }

    @Subscribe
    public void onEvent(final RetrieveUpdatedGameServerMessage retrieveUpdatedGameServerMessage) {
        handleEvent(retrieveUpdatedGameServerMessage);
    }

    @Subscribe
    public void onEvent(final GameStateChangedInternalMessage gameStateChangedInternalMessage) {
        handleEvent(gameStateChangedInternalMessage);
    }

    @Subscribe
    public void onEvent(final SystemLobbyMessageServerInternalMessage systemLobbyMessageServerInternalMessage) {
        handleEvent(systemLobbyMessageServerInternalMessage);
    }

    @Test
    @DisplayName("Should make user leave game and lose it on logout")
    void onLogoutRequest() {
        assertThat(game.isGameLost())
                .isFalse();

        when(gameManagement.findAllGames())
                .thenReturn(List.of(game));

        final LogoutRequest logoutRequest = new LogoutRequest(user);
        post(logoutRequest);

        assertThat(game.isGameLost())
                .isTrue();
    }
}