package de.uol.swp.server.card;

import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.card.event_card.AirBridgeEventCard;
import de.uol.swp.common.card.request.DrawPlayerCardRequest;
import de.uol.swp.common.card.stack.CardStack;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.EventBusBasedTest;
import de.uol.swp.server.game.GameManagement;
import de.uol.swp.server.lobby.LobbyService;
import org.greenrobot.eventbus.EventBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CardServiceTest extends EventBusBasedTest {

    private CardService cardService;
    private GameManagement gameManagement;
    private LobbyService lobbyService;
    private EventBus eventBus;

    @BeforeEach
    void setUp() {
        gameManagement = mock(GameManagement.class);
        lobbyService = mock(LobbyService.class);
        eventBus = getBus();
        cardService = new CardService(eventBus, gameManagement, lobbyService);
    }

    @Test
    void onDrawPlayerCardRequest_gameNotFound() {
        DrawPlayerCardRequest request = new DrawPlayerCardRequest(mock(Game.class), mock(Player.class));
        when(gameManagement.getGame(any(Game.class))).thenReturn(Optional.empty());

        cardService.onDrawPlayerCardRequest(request);

        verify(gameManagement, never()).drawPlayerCard(any(Game.class));
    }

    @Test
    void onDrawPlayerCardRequest_playerDrawStackEmpty() {
        Game game = mock(Game.class);
        when(gameManagement.getGame(any(Game.class))).thenReturn(Optional.of(game));
        when(game.getPlayerDrawStack()).thenReturn(new CardStack<>());

        DrawPlayerCardRequest request = new DrawPlayerCardRequest(game, mock(Player.class));
        cardService.onDrawPlayerCardRequest(request);

        verify(gameManagement, never()).drawPlayerCard(any(Game.class));
    }

    @Test
    void onDrawPlayerCardRequest_successfulDraw() {

        User user = new UserDTO("Test", "Test", "Test@test.de");
        List<Plague> plagues = List.of(mock(Plague.class));
        MapType mapType = mock(MapType.class);
        Lobby lobby = new LobbyDTO("Test", user,2,4);
        Game game = new Game(lobby, mapType, new ArrayList<>(lobby.getPlayers()), plagues);

        PlayerCard playerCard = new AirBridgeEventCard();

        when(gameManagement.getGame(any(Game.class))).thenReturn(Optional.of(game));
        when(gameManagement.drawPlayerCard(any(Game.class))).thenReturn(playerCard);

        DrawPlayerCardRequest request = new DrawPlayerCardRequest(game, lobby.getPlayerForUser(user));
        post(request);

        assertThat(lobby.getPlayerForUser(user).getHandCards()).contains(playerCard);
        verify(gameManagement, times(1)).updateGame(game);
        verify(lobbyService, times(1)).sendToAllInLobby(eq(lobby), any());
    }


}
