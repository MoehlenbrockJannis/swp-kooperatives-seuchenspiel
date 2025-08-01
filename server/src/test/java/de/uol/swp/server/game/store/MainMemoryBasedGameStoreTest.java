package de.uol.swp.server.game.store;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.GameDifficulty;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.UserPlayer;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static de.uol.swp.server.util.TestUtils.createMapType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class MainMemoryBasedGameStoreTest {

    private GameStore gameStore;
    private Game game;

    @BeforeEach
    void setUp() {
        this.gameStore = new MainMemoryBasedGameStore();
        User user = new UserDTO("Test", "Test", "Test@test.de");
        User user2 = new UserDTO("Test2", "Test", "Test@test.de");

        Player player = new UserPlayer(user);
        Player player2 = new UserPlayer(user2);

        List<Plague> plagues = List.of(mock(Plague.class));
        MapType mapType = createMapType();
        Lobby lobby = new LobbyDTO("Test", user);

        lobby.addPlayer(player);
        lobby.addPlayer(player2);
        GameDifficulty difficulty = GameDifficulty.getDefault();
        this.game = new Game(lobby, mapType, new ArrayList<>(lobby.getPlayers()), plagues, difficulty);
    }

    @Test
    @DisplayName("Test to add a game")
    void addGameGameAlreadyExist() {
        game.setId(12345);

        gameStore.addGame(game);

        assertThatThrownBy(() -> gameStore.addGame(game)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Test to remove a game")
    void removeGame() {
        game.setId(12345);

        gameStore.addGame(game);
        assertThat(gameStore.getGame(game)).contains(game);
        gameStore.removeGame(game);

        assertThat(gameStore.getGame(game)).isEmpty();
    }

    @Test
    @DisplayName("Update an existing game")
    void updateExistingGame() {

        gameStore.addGame(game);

        gameStore.updateGame(game);

        assertThat(gameStore.getGame(game)).contains(game);


    }

    @Test
    @DisplayName("Update a non-existing game throws exception")
    void updateNonExistingGameThrowsException() {

        assertThatThrownBy(() -> gameStore.updateGame(game))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getAllGames() {
        assertThat(gameStore.getAllGames())
                .isEmpty();

        gameStore.addGame(game);

        assertThat(gameStore.getAllGames())
                .contains(game);
    }
}