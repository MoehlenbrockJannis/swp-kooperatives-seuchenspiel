package de.uol.swp.server.card;

import de.uol.swp.common.card.InfectionCard;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.server.game.GameManagement;
import de.uol.swp.server.game.store.MainMemoryBasedGameStore;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.player.turn.PlayerTurnManagement;
import de.uol.swp.server.role.RoleManagement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.uol.swp.server.util.TestUtils.createMapType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class CardManagementTest {
    private GameManagement gameManagement;
    private LobbyManagement lobbyManagement;
    private CardManagement cardManagement;
    private PlayerTurnManagement playerTurnManagement;
    private RoleManagement roleManagement;
    private Lobby mockLobby;
    private MapType mockMapType;
    private List<Plague> mockPlagues;
    private PlayerCard mockPlayerCard;
    private InfectionCard mockInfectionCard;

    @BeforeEach
    void setUp() {
        lobbyManagement = mock(LobbyManagement.class);
        playerTurnManagement = mock(PlayerTurnManagement.class);
        roleManagement = mock(RoleManagement.class);
        cardManagement = new CardManagement();

        gameManagement = new GameManagement(new MainMemoryBasedGameStore());
        gameManagement.setLobbyManagement(lobbyManagement);
        gameManagement.setPlayerTurnManagement(playerTurnManagement);
        gameManagement.setRoleManagement(roleManagement);

        mockLobby = mock(Lobby.class);
        mockMapType = createMapType();
        mockPlagues = new ArrayList<>();
        mockPlayerCard = mock(PlayerCard.class);
        mockInfectionCard = mock(InfectionCard.class);
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
        cardManagement.discardPlayerCard(game, mockPlayerCard);
        assertThat(game.getPlayerDiscardStack()).contains(mockPlayerCard);
    }

    @Test
    @DisplayName("Test drawing an infection card from the top")
    void drawInfectionCard_fromTheTop() {
        Game game = gameManagement.createGame(mockLobby, mockMapType, mockPlagues);
        gameManagement.addGame(game);
        Optional<Game> optionalGame = gameManagement.getGame(game);
        optionalGame.get().getInfectionDrawStack().push(mockInfectionCard);
        InfectionCard infectionCard = cardManagement.drawInfectionCardFromTheTop(optionalGame.get());

        assertThat(infectionCard).isNotNull();
    }

    @Test
    @DisplayName("Test drawing an infection card from the bottom")
    void drawInfectionCard_fromTheBottom() {
        Game game = gameManagement.createGame(mockLobby, mockMapType, mockPlagues);
        game.getInfectionDrawStack().removeAllElements();
        game.getInfectionDrawStack().push(mockInfectionCard);
        game.getInfectionDrawStack().push(mock(InfectionCard.class));
        InfectionCard infectionCard = cardManagement.drawInfectionCardFromTheBottom(game);

        assertThat(infectionCard).isNotNull().isEqualTo(mockInfectionCard);
    }

    @Test
    @DisplayName("Test discarding an infection card")
    void discardInfectionCard() {
        Game game = gameManagement.createGame(mockLobby, mockMapType, mockPlagues);
        gameManagement.addGame(game);
        cardManagement.discardInfectionCard(game, mockInfectionCard);
        assertThat(game.getInfectionDiscardStack()).contains(mockInfectionCard);
    }
}