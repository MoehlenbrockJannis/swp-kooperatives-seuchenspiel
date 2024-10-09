package de.uol.swp.server.card;

import de.uol.swp.common.card.InfectionCard;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.card.event_card.AirBridgeEventCard;
import de.uol.swp.common.card.request.DiscardInfectionCardRequest;
import de.uol.swp.common.card.request.DiscardPlayerCardRequest;
import de.uol.swp.common.card.request.DrawInfectionCardRequest;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CardServiceTest extends EventBusBasedTest {

    private CardService cardService;
    private GameManagement gameManagement;
    private LobbyService lobbyService;
    private Game game;

    @BeforeEach
    void setUp() {
        gameManagement = mock(GameManagement.class);
        lobbyService = mock(LobbyService.class);
        EventBus eventBus = getBus();
        cardService = new CardService(eventBus, gameManagement, lobbyService);

        User user = new UserDTO("Test", "Test", "Test@test.de");
        List<Plague> plagues = List.of(mock(Plague.class));
        MapType mapType = mock(MapType.class);
        Lobby lobby = new LobbyDTO("Test", user,2,4);
        this.game = new Game(lobby, mapType, new ArrayList<>(lobby.getPlayers()), plagues);
    }

    @Test
    @DisplayName("Draw Player Card Request - Game Not Found")
    void onDrawPlayerCardRequest_gameNotFound() {
        DrawPlayerCardRequest request = new DrawPlayerCardRequest(mock(Game.class), mock(Player.class));
        when(gameManagement.getGame(any(Game.class))).thenReturn(Optional.empty());

        cardService.onDrawPlayerCardRequest(request);

        verify(gameManagement, never()).drawPlayerCard(any(Game.class));
    }

    @Test
    @DisplayName("Draw Player Card Request - Player Draw Stack Empty")
    void onDrawPlayerCardRequest_playerDrawStackEmpty() {
        Game mockedGame = mock(Game.class);
        List<Player> players = new ArrayList<>();
        players.add(mock(Player.class));

        when(gameManagement.getGame(any(Game.class))).thenReturn(Optional.of(mockedGame));
        when(mockedGame.getPlayerDrawStack()).thenReturn(new CardStack<>());
        when(mockedGame.getPlayersInTurnOrder()).thenReturn(players);

        DrawPlayerCardRequest request = new DrawPlayerCardRequest(mockedGame, mock(Player.class));
        cardService.onDrawPlayerCardRequest(request);

        verify(gameManagement, never()).drawPlayerCard(any(Game.class));
    }

    @Test
    @DisplayName("Draw Infection Card Request - Successful Draw")
    void onDrawPlayerCardRequest_successfulDraw() {


        PlayerCard playerCard = new AirBridgeEventCard();

        when(gameManagement.getGame(any(Game.class))).thenReturn(Optional.of(this.game));
        when(gameManagement.drawPlayerCard(any(Game.class))).thenReturn(playerCard);

        DrawPlayerCardRequest request = new DrawPlayerCardRequest(game, this.game.getLobby().getPlayerForUser(this.game.getLobby().getOwner()));
        post(request);

        verify(gameManagement, times(1)).updateGame(game);
        verify(lobbyService, times(1)).sendToAllInLobby(eq(this.game.getLobby()), any());
    }

    @Test
    @DisplayName("Discard Player Card Request - Successful Discard")
    void onDiscardPlayerCardRequest_successfulDiscard() {

        Player player = this.game.getLobby().getPlayerForUser(this.game.getLobby().getOwner());
        PlayerCard playerCard = new AirBridgeEventCard();
        player.addHandCard(playerCard);

        when(gameManagement.getGame(any(Game.class))).thenReturn(Optional.of(game));

        DiscardPlayerCardRequest<PlayerCard> request = new DiscardPlayerCardRequest<>(game, player, playerCard);
        post(request);



        verify(gameManagement, times(1)).updateGame(game);
        verify(lobbyService, times(1)).sendToAllInLobby(eq(this.game.getLobby()), any());
    }

    @Test
    @DisplayName("Discard Player Card Request - Card Not In Hand")
    void onDiscardPlayerCardRequest_cardNotInHand() {
        List<Plague> plagues = List.of(mock(Plague.class));
        MapType mapType = mock(MapType.class);
        List<Player> players = new ArrayList<>();
        players.add(mock(Player.class));
        Lobby lobby = mock(LobbyDTO.class);
        Game gameWithMockedPlayer = new Game(lobby, mapType, players, plagues);
        PlayerCard playerCard = new AirBridgeEventCard();
        Player player = players.get(0);

        when(gameManagement.getGame(any(Game.class))).thenReturn(Optional.of(gameWithMockedPlayer));
        doNothing().when(player).removeHandCard(playerCard);

        DiscardPlayerCardRequest<PlayerCard> request = new DiscardPlayerCardRequest<>(gameWithMockedPlayer, player, playerCard);
        post(request);

        assertThat(player.getHandCards()).doesNotContain(playerCard);
        verify(player, never()).removeHandCard(playerCard);
        verify(gameManagement, never()).discardPlayerCard(this.game, playerCard);
        verify(gameManagement, never()).updateGame(this.game);
    }

    @Test
    @DisplayName("Test: Draw Infection Card Request - Successful Draw")
    void onDrawInfectionCardRequest_successfulDraw() {
        InfectionCard infectionCard = mock(InfectionCard.class);

        when(gameManagement.getGame(any(Game.class))).thenReturn(Optional.of(this.game));
        when(gameManagement.drawInfectionCardFromTheTop(any(Game.class))).thenReturn(infectionCard);

        DrawInfectionCardRequest request = new DrawInfectionCardRequest(game, this.game.getLobby().getPlayerForUser(this.game.getLobby().getOwner()));
        post(request);

        verify(gameManagement, times(1)).drawInfectionCardFromTheTop(game);
        verify(gameManagement, times(1)).updateGame(game);
        verify(lobbyService, times(2)).sendToAllInLobby(eq(this.game.getLobby()), any());
    }

    @Test
    @DisplayName("Discard Infection Card Request - Successful Discard")
    void onDiscardInfectionCardRequest_successfulDiscard() {
        InfectionCard infectionCard = mock(InfectionCard.class);
        this.game.getInfectionDiscardStack().add(infectionCard);

        when(gameManagement.getGame(any(Game.class))).thenReturn(Optional.of(game));

        DiscardInfectionCardRequest request = new DiscardInfectionCardRequest(game,this.game.getLobby().getPlayerForUser(this.game.getLobby().getOwner()),  infectionCard);
        post(request);

        verify(gameManagement, times(1)).discardInfectionCard(game, infectionCard);
        verify(gameManagement, times(1)).updateGame(game);
        verify(lobbyService, times(1)).sendToAllInLobby(eq(this.game.getLobby()), any());
    }



}
