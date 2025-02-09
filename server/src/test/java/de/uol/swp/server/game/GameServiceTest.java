package de.uol.swp.server.game;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.GameDifficulty;
import de.uol.swp.common.game.request.CreateGameRequest;
import de.uol.swp.common.game.server_message.CreateGameServerMessage;
import de.uol.swp.common.game.server_message.RetrieveUpdatedGameServerMessage;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.EventBusBasedTest;
import de.uol.swp.server.lobby.LobbyService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static de.uol.swp.server.util.TestUtils.createMapType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class GameServiceTest extends EventBusBasedTest {
    private GameService gameService;
    private GameManagement gameManagement;
    private LobbyService lobbyService;

    private Game game;
    private Lobby lobby;
    private MapType mapType;
    private List<Plague> plagues;
    private AIPlayer aiPlayer;
    private GameDifficulty difficulty;

    @BeforeEach
    void setUp() {
        gameManagement = mock();
        lobbyService = mock();
        final EventBus eventBus = getBus();
        difficulty = GameDifficulty.getDefault();

        gameService = new GameService(eventBus, gameManagement, lobbyService);

        final User user = new UserDTO("user", "pass", "");
        lobby = new LobbyDTO("lobby", user, 1, 2);

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

        assertThat(gameService.getSession(aiPlayer))
                .isPresent();
    }

    @Test
    @DisplayName("Should return an empty Optional if there is no session for an AIPlayer")
    void getSession_empty() {
        assertThat(gameService.getSession(aiPlayer))
                .isEmpty();
    }

    @Subscribe
    public void onEvent(final CreateGameServerMessage createGameServerMessage) {
        handleEvent(createGameServerMessage);
    }

    @Subscribe
    public void onEvent(final RetrieveUpdatedGameServerMessage retrieveUpdatedGameServerMessage) {
        handleEvent(retrieveUpdatedGameServerMessage);
    }
}