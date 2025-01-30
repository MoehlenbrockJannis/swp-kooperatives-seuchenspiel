package de.uol.swp.server.game;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.request.CreateGameRequest;
import de.uol.swp.common.game.server_message.CreateGameServerMessage;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.UserPlayer;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.EventBusBasedTest;
import de.uol.swp.server.usermanagement.AuthenticationService;
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
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        gameManagement = mock();
        authenticationService = mock();
        final EventBus eventBus = getBus();

        gameService = new GameService(eventBus, authenticationService, gameManagement);
    }

    @Test
    @DisplayName("Should create a game from the CreateGameRequest and return the game with a CreateGameServerMessage")
    void onCreateGameRequest() throws InterruptedException {
        final User user = new UserDTO("user", "pass", "");
        final User user2 = new UserDTO("user2", "pass2", "user2");
        final Lobby lobby = new LobbyDTO("lobby", user, 1, 2);

        Player player = new UserPlayer(user);
        Player player2 = new UserPlayer(user2);

        lobby.addPlayer(player);
        lobby.addPlayer(player2);

        final MapType mapType = createMapType();
        final List<Plague> plagues = List.of();

        final Game game = new Game(lobby, mapType, new ArrayList<>(lobby.getPlayers()), plagues);

        when(gameManagement.createGame(lobby, mapType, plagues))
                .thenReturn(game);

        final CreateGameRequest createGameRequest = new CreateGameRequest(lobby, mapType, plagues);
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

    @Subscribe
    public void onEvent(final CreateGameServerMessage createGameServerMessage) {
        handleEvent(createGameServerMessage);
    }
}