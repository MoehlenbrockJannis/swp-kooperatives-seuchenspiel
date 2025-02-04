package de.uol.swp.server.card;

import de.uol.swp.common.action.ActionFactory;
import de.uol.swp.common.card.InfectionCard;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.card.event_card.AirBridgeEventCard;
import de.uol.swp.common.card.request.DiscardInfectionCardRequest;
import de.uol.swp.common.card.request.DiscardPlayerCardRequest;
import de.uol.swp.common.card.request.DrawInfectionCardRequest;
import de.uol.swp.common.card.request.DrawPlayerCardRequest;
import de.uol.swp.common.card.response.DrawPlayerCardResponse;
import de.uol.swp.common.card.response.ReleaseToDiscardPlayerCardResponse;
import de.uol.swp.common.card.response.ReleaseToDrawInfectionCardResponse;
import de.uol.swp.common.card.response.ReleaseToDrawPlayerCardResponse;
import de.uol.swp.common.card.stack.CardStack;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.message.response.AbstractGameResponse;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.UserPlayer;
import de.uol.swp.common.player.turn.PlayerTurn;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.EventBusBasedTest;
import de.uol.swp.server.communication.UUIDSession;
import de.uol.swp.server.game.GameManagement;
import de.uol.swp.server.lobby.LobbyService;
import de.uol.swp.server.player.turn.PlayerTurnManagement;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;
import org.greenrobot.eventbus.Subscribe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

import static de.uol.swp.server.util.TestUtils.createMapType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CardServiceTest extends EventBusBasedTest {

    private CardService cardService;
    private CardManagement cardManagement;
    private GameManagement gameManagement;
    private LobbyService lobbyService;
    private PlayerTurnManagement playerTurnManagement;
    private Game game;
    private MapType mapType;
    private Session session;
    private List<Object> responses;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        cardManagement = mock(CardManagement.class);
        lobbyService = mock(LobbyService.class);
        gameManagement = mock(GameManagement.class);
        playerTurnManagement = mock(PlayerTurnManagement.class);
        responses = new ArrayList<>();
        EventBus eventBus = getBus();
        cardService = new CardService(eventBus, cardManagement, gameManagement, lobbyService);

        User user = new UserDTO("Test", "Test", "Test@test.de");
        User user2 = new UserDTO("TestZwei", "Test", "Test@test.de");

        this.player1 = new UserPlayer(user);
        this.player2 = new UserPlayer(user2);

        List<Plague> plagues = List.of(mock(Plague.class));
        mapType = createMapType();
        Lobby lobby = new LobbyDTO("Test", user);

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        this.game = new Game(lobby, mapType, new ArrayList<>(lobby.getPlayers()), plagues);
        try (MockedConstruction<ActionFactory> mockedActionFactory = Mockito.mockConstruction(ActionFactory.class, (mock, context) -> {
            when(mock.createAllGeneralActionsExcludingSomeAndIncludingSomeRoleActions(any(), any()))
                    .thenReturn(List.of());
        })) {
            this.game.addPlayerTurn(new PlayerTurn(
                    game,
                    game.getPlayersInTurnOrder().get(0),
                    game.getNumberOfActionsPerTurn(),
                    game.getNumberOfPlayerCardsToDrawPerTurn(),
                    game.getNumberOfInfectionCardsToDrawPerTurn()
            ));
        }

        session = UUIDSession.create(game.getLobby().getOwner());
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
        request.setSession(session);
        post(request);

        verify(gameManagement, times(1)).updateGame(game);
        verify(lobbyService, times(1)).sendToAllInLobby(eq(this.game.getLobby()), any());
    }

    @Test
    @DisplayName("Discard Player Card Request - Successful Discard")
    void onDiscardPlayerCardRequest_successfulDiscard() {

        Player player = this.game.getCurrentPlayer();
        PlayerCard playerCard = new AirBridgeEventCard();
        player.addHandCard(playerCard);

        when(gameManagement.getGame(any(Game.class))).thenReturn(Optional.of(game));

        DiscardPlayerCardRequest<PlayerCard> request = new DiscardPlayerCardRequest<>(game, player, playerCard);
        request.setSession(session);
        post(request);

        verify(gameManagement, times(1)).updateGame(game);
        verify(lobbyService, times(1)).sendToAllInLobby(eq(this.game.getLobby()), any());
    }

    @Test
    @DisplayName("Discard Player Card Request - Card Not In Hand")
    void onDiscardPlayerCardRequest_cardNotInHand() {
        this.player1 = mock(Player.class);
        this.player2 = mock(Player.class);

        List<Plague> plagues = List.of(mock(Plague.class));
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        Lobby lobby = mock(LobbyDTO.class);
        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game gameWithMockedPlayer = new Game(lobby, mapType, players, plagues);
        PlayerCard playerCard = new AirBridgeEventCard();
        Player player = players.get(0);

        when(gameManagement.getGame(any(Game.class))).thenReturn(Optional.of(gameWithMockedPlayer));
        doNothing().when(player).removeHandCard(playerCard);

        DiscardPlayerCardRequest<PlayerCard> request = new DiscardPlayerCardRequest<>(gameWithMockedPlayer, player, playerCard);
        post(request);

        assertThat(player.getHandCards()).doesNotContain(playerCard);
        verify(player, never()).removeHandCard(playerCard);
        verify(cardManagement, never()).discardPlayerCard(this.game, playerCard);
        verify(gameManagement, never()).updateGame(this.game);
    }

    @Test
    @DisplayName("Test: Draw Infection Card Request - Successful Draw")
    void onDrawInfectionCardRequest_successfulDraw() {
        InfectionCard infectionCard = mock(InfectionCard.class);
        Field associatedField = mock(Field.class);

        when(gameManagement.getGame(any(Game.class))).thenReturn(Optional.of(this.game));
        when(cardManagement.drawInfectionCardFromTheTop(any(Game.class))).thenReturn(infectionCard);
        when(infectionCard.getAssociatedField()).thenReturn(associatedField);

        DrawInfectionCardRequest request = new DrawInfectionCardRequest(game, this.game.getLobby().getPlayerForUser(this.game.getLobby().getOwner()));
        post(request);

        verify(cardManagement, times(1)).drawInfectionCardFromTheTop(game);
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

        verify(cardManagement, times(1)).discardInfectionCard(game, infectionCard);
        verify(gameManagement, times(1)).updateGame(game);
        verify(lobbyService, times(1)).sendToAllInLobby(eq(this.game.getLobby()), any());
    }

    @Test
    @DisplayName("Player has more than max hand cards - Discard required")
    void playerHasMoreThanMaxHandCards_discardRequired() throws InterruptedException {
        Player player = this.game.getLobby().getPlayerForUser(this.game.getLobby().getOwner());
        for(int i = 0; i < 4; i++) {
            player.addHandCard(new AirBridgeEventCard());
        }

        when(gameManagement.getGame(any(Game.class))).thenReturn(Optional.of(game));


        DrawPlayerCardRequest request = new DrawPlayerCardRequest(game, player);
        request.setSession(session);
        post(request);

        waitForLock();

        DrawPlayerCardResponse drawPlayerCardResponse = responses.stream()
                .filter(DrawPlayerCardResponse.class::isInstance)
                .map(DrawPlayerCardResponse.class::cast).findFirst()
                .orElse(null);

        assertThat(drawPlayerCardResponse).isNotNull();

    }

    @Test
    @DisplayName("Player has more than max hand cards - Session is empty")
    void playerHasMoreThanMaxHandCards_sessionIsEmpty() throws InterruptedException {
        Player player = this.game.getLobby().getPlayerForUser(this.game.getLobby().getOwner());
        for (int i = 0; i < 8; i++) {
            player.addHandCard(new AirBridgeEventCard());
        }

        when(gameManagement.getGame(any(Game.class))).thenReturn(Optional.of(game));

        DrawPlayerCardRequest request = new DrawPlayerCardRequest(game, player);
        assertThatThrownBy(() -> post(request))
                .isInstanceOf(EventBusException.class)
                        .hasCauseInstanceOf(NoSuchElementException.class);

        waitForLock();
    }

    @Test
    @DisplayName("Player has more than max hand cards - Successful sending of the ReleaseToDrawCardResponse")
    void sendReleaseToDrawCardResponse() throws InterruptedException {
        Function<Game, ReleaseToDrawPlayerCardResponse> responseSupplier = ReleaseToDrawPlayerCardResponse::new;
        cardService.sendReleaseToDrawCardResponse(game, session, responseSupplier);
        waitForLock();

        ReleaseToDrawPlayerCardResponse response = (ReleaseToDrawPlayerCardResponse) event;

        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("")
    void allowPlayerCardDrawing() throws InterruptedException {
        cardService.allowDrawingOrDiscarding(game, session, ReleaseToDrawPlayerCardResponse.class);

        waitForLock();

        assertThat(event)
                .isInstanceOf(ReleaseToDrawPlayerCardResponse.class);
        final ReleaseToDrawPlayerCardResponse releaseToDrawPlayerCardResponse = (ReleaseToDrawPlayerCardResponse) event;
        assertThat(releaseToDrawPlayerCardResponse.getGame())
                .usingRecursiveComparison()
                .isEqualTo(game);
        assertThat(releaseToDrawPlayerCardResponse.getNumberOfPlayerCardsToDraw())
                .isEqualTo(game.getNumberOfPlayerCardsToDrawPerTurn());
    }

    @Test
    @DisplayName("")
    void allowInfectionCardDrawing() throws InterruptedException, IllegalStateException {
        cardService.allowDrawingOrDiscarding(game, session, ReleaseToDrawInfectionCardResponse.class);

        waitForLock();

        assertThat(event)
                .isInstanceOf(ReleaseToDrawInfectionCardResponse.class);
        final ReleaseToDrawInfectionCardResponse releaseToDrawInfectionCardResponse = (ReleaseToDrawInfectionCardResponse) event;
        assertThat(releaseToDrawInfectionCardResponse.getGame())
                .usingRecursiveComparison()
                .isEqualTo(game);
        assertThat(releaseToDrawInfectionCardResponse.getNumberOfInfectionCardsToDraw())
                .isEqualTo(game.getNumberOfInfectionCardsToDrawPerTurn());
    }

    @Test
    @DisplayName("Allow Drawing Or Discarding - Handle exception")
    void allowDrawingOrDiscarding_handleException() {
        Game game = mock(Game.class);
        Session session = mock(Session.class);
        Class<? extends AbstractGameResponse> responseMessage = AbstractGameResponse.class;

        assertThatThrownBy(() -> cardService.allowDrawingOrDiscarding(game, session, responseMessage))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Subscribe
    public void onEvent(ReleaseToDiscardPlayerCardResponse releaseToDiscardPlayerCardResponse) {
        handleEvent(releaseToDiscardPlayerCardResponse);
        responses.add(releaseToDiscardPlayerCardResponse);
    }

    @Subscribe
    public void onEvent(DrawPlayerCardResponse drawPlayerCardResponse) {
        handleEvent(drawPlayerCardResponse);
        responses.add(drawPlayerCardResponse);
    }

    @Subscribe
    public void onEvent(ReleaseToDrawPlayerCardResponse releaseToDrawPlayerCardResponse) {
        handleEvent(releaseToDrawPlayerCardResponse);
        responses.add(releaseToDrawPlayerCardResponse);
    }

    @Subscribe
    public void onEvent(ReleaseToDrawInfectionCardResponse releaseToDrawInfectionCardResponse) {
        handleEvent(releaseToDrawInfectionCardResponse);
        responses.add(releaseToDrawInfectionCardResponse);
    }
}
