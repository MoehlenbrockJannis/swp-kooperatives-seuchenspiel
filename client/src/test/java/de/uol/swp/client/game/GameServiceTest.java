package de.uol.swp.client.game;

import de.uol.swp.client.EventBusBasedTest;
import de.uol.swp.common.game.request.CreateGameRequest;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.greenrobot.eventbus.Subscribe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest extends EventBusBasedTest {
    private GameService gameService;

    private Lobby lobby;

    @BeforeEach
    void setUp() {
        final String lobbyName = "test";
        final User user = new UserDTO("test", "", "");
        this.lobby = new LobbyDTO(lobbyName, user, 2, 4);
        this.gameService = new GameService(getBus());
    }

    @Test
    void createGame() throws InterruptedException {
        gameService.createGame(lobby);

        waitForLock();

        assertInstanceOf(CreateGameRequest.class, event);

        final CreateGameRequest createGameRequest = (CreateGameRequest) event;
        assertEquals(lobby, createGameRequest.getLobby());
    }

    @Subscribe
    public void onEvent(final CreateGameRequest createGameRequest) {
        handleEvent(createGameRequest);
    }
}
