package de.uol.swp.client.game;

import de.uol.swp.client.EventBusBasedTest;
import de.uol.swp.common.game.request.CreateGameRequest;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.map.City;
import de.uol.swp.common.map.MapSlot;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.util.Color;
import org.greenrobot.eventbus.Subscribe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class GameServiceTest extends EventBusBasedTest {
    private GameService gameService;

    private Lobby lobby;

    private MapType mapType;

    private List<Plague> plagueList;

    @BeforeEach
    void setUp() {
        final String lobbyName = "test";
        final User user = new UserDTO("test", "", "");
        this.lobby = new LobbyDTO(lobbyName, user, 2, 4);
        this.gameService = new GameService(getBus());

        City city1 = new City("test", "");
        City city2 = new City("test", "");
        Plague plague = new Plague("test", new Color(0, 0, 0));
        List<MapSlot> mapSlotList = new ArrayList<>(List.of(new MapSlot(city1, new ArrayList<>(List.of(city2)), plague, 0, 0)));
        this.mapType = new MapType("name", mapSlotList, city1);
        this.plagueList = new ArrayList<>(List.of(plague));
    }

    @Test
    void createGame() throws InterruptedException {
        gameService.createGame(lobby, mapType, plagueList);

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
