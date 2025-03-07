package de.uol.swp.common.game;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.map.GameMap;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.map.research_laboratory.ResearchLaboratory;
import de.uol.swp.common.marker.AntidoteMarker;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.plague.PlagueCube;
import de.uol.swp.common.plague.exception.NoPlagueCubesFoundException;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.UserPlayer;
import de.uol.swp.common.player.turn.PlayerTurn;
import de.uol.swp.common.role.RoleAbility;
import de.uol.swp.common.role.RoleCard;
import de.uol.swp.common.triggerable.Triggerable;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.util.Color;
import de.uol.swp.common.util.TestUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static de.uol.swp.common.util.TestUtils.createPlayerTurn;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;

public class GameTest {

    private final Color testPlagueColor = new Color(1, 2, 3);

    private Game game;
    private Lobby lobby;
    private List<Player> players;
    private Plague plague;
    private PlayerTurn playerTurn;

    @BeforeEach
    void setUp() {
        String testUserName = "TestUser";
        String testPassword = "TestPassword";
        String testEmail = "test@mail.com";
        User user = new UserDTO(testUserName, testPassword, testEmail);
        String testLobbyName = "TestLobby";
        lobby = new LobbyDTO(testLobbyName, user);

        players = createTestPlayers();
        game = new Game(lobby, TestUtils.createMapType(), players, List.of(), GameDifficulty.getDefault());
        game.setId(0);

        String testPlagueName = "plague";
        plague = new Plague(testPlagueName, testPlagueColor);
        playerTurn = createPlayerTurn(game, game.getCurrentPlayer(), 0, 0, 0);
        game.addPlayerTurn(playerTurn);
    }

    private List<Player> createTestPlayers() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            players.add(new UserPlayer(new UserDTO("User" + i, "password", "user" + i + "@mail.com")));
        }
        players.add(new AIPlayer("AIPlayer"));
        players.forEach(player -> player.setRole(new RoleCard("TestRole", new Color(), mock(RoleAbility.class))));
        return players;
    }

    @Test
    @DisplayName("Game should initialize with correct parameters")
    void gameInitializationTest() {
        assertAll(
                () -> assertThat(game.getLobby()).isEqualTo(lobby),
                () -> assertThat(game.getPlayersInTurnOrder().size()).isEqualTo(players.size())
        );
    }

    @Test
    @DisplayName("Players should receive correct number of initial hand cards")
    void initialHandCardsTest() {
        game.getPlayersInTurnOrder().forEach(player ->
                assertThat(player.getHandCards().size()).isEqualTo(2)
        );
    }

    @Test
    @DisplayName("Game should distribute initial plague cubes correctly")
    void distributeInitialPlagueCubesTest() {
        game.getFields().forEach(field ->
                assertThat(field.getPlagueCubes().size()).isBetween(0, 3)
        );
    }

    @Test
    @DisplayName("Game should assign players to starting field correctly")
    void assignPlayersToStartingFieldTest() {
        game.getPlayersInTurnOrder().forEach(player ->
                assertThat(player.getCurrentField()).isEqualTo(game.getMap().getStartingField())
        );
    }

    @Test
    @DisplayName("Game should not be equal to another game with the same input parameters but different ids")
    void gameEqualToCopyWithDifferentIdTest() {
        Game gameCopy = new Game(lobby, TestUtils.createMapType(), players, List.of(), GameDifficulty.getDefault());
        gameCopy.setId(1);
        assertThat(game.equals(gameCopy)).isFalse();
    }

    @Test
    @DisplayName("Should retrieve a plague cube when available")
    void getPlagueCubeWhenAvailableTest() {
        PlagueCube cube = game.getPlagueCubeOfPlague(plague);
        assertAll(
                () -> assertThat(cube).isNotNull(),
                () -> assertThat(cube.getPlague()).isEqualTo(plague)
        );
    }

    @Test
    @DisplayName("Should throw exception when no plague cubes are available")
    void getPlagueCubeWhenNotAvailableTest() {
        game.getPlagueCubes().get(plague).clear();
        assertThatThrownBy(() -> game.getPlagueCubeOfPlague(plague))
                .isInstanceOf(NoPlagueCubesFoundException.class);
    }

    @Test
    @DisplayName("Should throw exception when plague is not found")
    void getPlagueCubeWhenPlagueNotFoundTest() {
        Plague unknownPlague = new Plague("UnknownPlague", new Color(0, 0, 0));
        assertThatThrownBy(() -> game.getPlagueCubeOfPlague(unknownPlague))
                .isInstanceOf(NoPlagueCubesFoundException.class);
    }

    @Test
    @DisplayName("Should add plague cube to the game")
    void addPlagueCubeTest() {
        PlagueCube cube = new PlagueCube(plague);
        game.addPlagueCube(cube);
        List<PlagueCube> cubes = game.getPlagueCubes().get(plague);
        assertThat(cubes).isNotNull();
        Assertions.assertThat(cubes).contains(cube);
    }

    @Test
    @DisplayName("Should return new ResearchLaboratory")
    void getResearchLaboratoryTest() {
        ResearchLaboratory researchLaboratory = game.getResearchLaboratory();
        assertThat(researchLaboratory).isNotNull();
    }

    @Test
    @DisplayName("Should return whether the game has ResearchLaboratories")
    void hasResearchLaboratoryTest() {
        assertThat(game.hasResearchLaboratory()).isTrue();
    }

    @Test
    @DisplayName("Should add an antidote marker to the antidote marker list")
    void addAntidoteMarkerTest() {
        AntidoteMarker marker = new AntidoteMarker(plague);
        game.addAntidoteMarker(marker);
        assertThat(game.getAntidoteMarkers()).isNotNull();
        Assertions.assertThat(game.getAntidoteMarkers()).contains(marker);
    }

    @Test
    @DisplayName("Should return that the game has no antidote marker for a plague")
    void hasNoAntidoteMarkerForPlagueTest() {
        assertThat(game.hasAntidoteMarkerForPlague(plague)).isFalse();
    }

    @Test
    @DisplayName("Should return that the game has an antidote marker for a plague")
    void hasAntidoteMarkerForPlagueTest() {
        AntidoteMarker marker = new AntidoteMarker(plague);
        game.addAntidoteMarker(marker);
        assertThat(game.hasAntidoteMarkerForPlague(plague)).isTrue();
    }

    @Test
    @DisplayName("Should increase outbreak marker")
    void increaseOutbreakMarkerTest() {
        game.startOutbreak();
        assertThat(game.getOutbreakMarker().getLevelValue()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should set game lost after outbreak at final marker")
    void setGameLostAfterOutbreakTest() {
        while (!game.getOutbreakMarker().isAtMaximumLevel()) {
            game.startOutbreak();
        }
        assertAll(
                () -> assertThat(game.getOutbreakMarker().isAtMaximumLevel()).isTrue(),
                () -> assertThat(game.isGameLost()).isTrue()
        );
    }

    @Test
    @DisplayName("Should return the number of infection cards to draw per turn")
    void getNumberOfInfectionCardsToDrawPerTurnTest() {
        assertThat(game.getNumberOfInfectionCardsToDrawPerTurn()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should return the current player turn")
    void getCurrentPlayerTurnTest() {
        PlayerTurn currentTurn = game.getCurrentTurn();
        assertThat(currentTurn).isEqualTo(playerTurn);
    }

    @Test
    @DisplayName("Should return the current player")
    void getCurrentPlayerTest() {
        Player currentPlayer = game.getCurrentPlayer();
        assertThat(currentPlayer).isEqualTo(players.get(0));
    }

    @Test
    @DisplayName("Should set the index to the next player")
    void nextPlayerTest() {
        assertThat(game.getCurrentPlayer()).isEqualTo(players.get(0));
        game.nextPlayer();
        assertThat(game.getCurrentPlayer()).isEqualTo(players.get(1));
    }

    @Test
    @DisplayName("Should return the triggerables of this game")
    void getTriggerablesTest() {
        List<Triggerable> triggerables = game.getTriggerables();
        assertThat(triggerables).isNotNull();
    }

    @Test
    @DisplayName("Should return the player of this game for an input player")
    void findPlayerTest() {
        assertAll(
                () -> assertThat(game.getPlayersInTurnOrder().contains(players.get(0))).isTrue(),
                () -> assertThat(game.findPlayer(players.get(0)).orElseThrow()).isEqualTo(players.get(0))
        );
    }

    @Test
    @DisplayName("Should return the field of the game map for an input field")
    void findFieldTest() {
        MapType mapType = TestUtils.createMapType();
        GameMap gameMap = new GameMap(game, mapType);
        Field field = new Field(gameMap, mapType.getMap().get(0));
        assertThat(game.findField(field)).isPresent();
    }

    @Test
    @DisplayName("Should increase infection level")
    void increaseInfectionLevelTest() {
        assertThat(game.getInfectionMarker().getLevel()).isEqualTo(0);
        game.increaseInfectionLevel();
        assertThat(game.getInfectionMarker().getLevel()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return true if a research laboratory is has to be moved, false otherwise")
    void requiresResearchLaboratoryMoveTest() {
        assertThat(game.requiresResearchLaboratoryMove()).isFalse();
    }
}
