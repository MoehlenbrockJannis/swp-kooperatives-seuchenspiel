package de.uol.swp.server.card;

import de.uol.swp.common.action.Action;
import de.uol.swp.common.action.ActionFactory;
import de.uol.swp.common.card.EpidemicCard;
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
import de.uol.swp.common.game.GameDifficulty;
import de.uol.swp.common.game.server_message.RetrieveUpdatedGameServerMessage;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.map.GameMap;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.marker.InfectionMarker;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.message.MessageContext;
import de.uol.swp.common.message.response.AbstractGameResponse;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.plague.PlagueCube;
import de.uol.swp.common.player.AIPlayer;
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
import de.uol.swp.server.triggerable.TriggerableService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
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
    private TriggerableService triggerableService;
    private PlayerTurnManagement playerTurnManagement;
    private Game game;
    private MapType mapType;
    private Message message;
    private List<Object> responses;
    private Player player1;
    private Player player2;
    private GameDifficulty difficulty;
    private Game mockGame;
    private EpidemicCard epidemicCard;
    private InfectionCard mockBottomCard;
    private Field mockField;
    private Plague mockPlague;
    private Player mockPlayer;
    private PlayerTurn mockTurn;
    private GameMap mockMap;
    private CardStack<InfectionCard> discardStack;
    private CardStack<InfectionCard> drawStack;

    @BeforeEach
    void setUp() {
        cardManagement = mock(CardManagement.class);
        lobbyService = mock(LobbyService.class);
        gameManagement = mock(GameManagement.class);
        playerTurnManagement = mock(PlayerTurnManagement.class);
        triggerableService = mock();
        responses = new ArrayList<>();
        EventBus eventBus = getBus();
        cardService = new CardService(eventBus, cardManagement, gameManagement, lobbyService, triggerableService);
        difficulty = GameDifficulty.getDefault();
        User user = new UserDTO("Test", "Test", "Test@test.de");
        User user2 = new UserDTO("TestZwei", "Test", "Test@test.de");

        this.player1 = new UserPlayer(user);
        this.player2 = new UserPlayer(user2);

        List<Plague> plagues = List.of(mock(Plague.class));
        mapType = createMapType();
        Lobby lobby = new LobbyDTO("Test", user);

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        this.game = new Game(lobby, mapType, new ArrayList<>(lobby.getPlayers()), plagues, difficulty);
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

        message = mock();
        final Session session = UUIDSession.create(game.getLobby().getOwner());
        when(message.getSession())
                .thenReturn(Optional.of(session));
        final MessageContext context = mock();
        when(message.getMessageContext())
                .thenReturn(Optional.of(context));
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
        request.initWithMessage(message);
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
        request.initWithMessage(message);
        post(request);

        verify(gameManagement, times(1)).updateGame(game);
        verify(lobbyService, times(1)).sendToAllInLobby(eq(this.game.getLobby()), any());
    }

    @Test
    @DisplayName("Discard Player Card Request - Card Not In Hand")
    void onDiscardPlayerCardRequest_cardNotInHand() {
        this.player1 = new AIPlayer("ai1");
        this.player2 = new AIPlayer("ai2");

        List<Plague> plagues = List.of(mock(Plague.class));
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        Lobby lobby = new LobbyDTO("lobby", mock());
        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game gameWithMockedPlayer = new Game(lobby, mapType, players, plagues, difficulty);
        gameWithMockedPlayer.addPlayerTurn(new PlayerTurn(gameWithMockedPlayer, player1, 0, 0, 0));
        PlayerCard playerCard = new AirBridgeEventCard();
        while (player1.hasHandCard(playerCard)) {
            player1.removeHandCard(playerCard);
        }

        when(gameManagement.getGame(any(Game.class))).thenReturn(Optional.of(gameWithMockedPlayer));

        DiscardPlayerCardRequest<PlayerCard> request = new DiscardPlayerCardRequest<>(gameWithMockedPlayer, player1, playerCard);
        post(request);

        assertThat(player1.getHandCards()).doesNotContain(playerCard);
        verify(cardManagement, never()).discardPlayerCard(this.game, playerCard);
        verify(gameManagement, times(1)).updateGame(this.game);
    }

    @Test
    @DisplayName("Test: Draw Infection Card Request - Successful Draw")
    void onDrawInfectionCardRequest_successfulDraw() {
        Game mockGame = mock(Game.class);
        when(mockGame.getLobby()).thenReturn(game.getLobby());

        InfectionCard infectionCard = mock(InfectionCard.class);
        Field mockField = mock(Field.class);
        Plague mockPlague = mock(Plague.class);
        PlagueCube mockPlagueCube = mock(PlagueCube.class);
        GameMap mockMap = mock(GameMap.class);

        when(mockPlague.getName()).thenReturn("TestPlague");
        when(mockGame.getPlagueCubeOfPlague(any(Plague.class))).thenReturn(mockPlagueCube);
        when(infectionCard.getAssociatedField()).thenReturn(mockField);
        when(mockField.getPlague()).thenReturn(mockPlague);
        when(mockField.isInfectable(any())).thenReturn(true);
        when(mockGame.getMap()).thenReturn(mockMap);

        PlayerTurn mockTurn = mock(PlayerTurn.class);
        when(mockTurn.isInInfectionCardDrawPhase()).thenReturn(true);
        when(mockTurn.getNumberOfInfectionCardsToDraw()).thenReturn(1);
        when(mockTurn.isInfectionCardDrawExecutable()).thenReturn(true);
        when(mockTurn.getInfectedFieldsInTurn()).thenReturn(new ArrayList<>());
        when(mockGame.getCurrentTurn()).thenReturn(mockTurn);

        when(gameManagement.getGame(any(Game.class))).thenReturn(Optional.of(mockGame));
        when(cardManagement.drawInfectionCardFromTheTop(any(Game.class))).thenReturn(infectionCard);

        Player mockPlayer = game.getLobby().getPlayerForUser(game.getLobby().getOwner());
        when(mockGame.findPlayer(any(Player.class))).thenReturn(Optional.of(mockPlayer));
        when(triggerableService.checkForSendingManualTriggerables(any(Game.class), any(), any(Player.class)))
                .thenReturn(false);

        DrawInfectionCardRequest request = new DrawInfectionCardRequest(mockGame, mockPlayer);
        post(request);

        verify(cardManagement).drawInfectionCardFromTheTop(any(Game.class));
        verify(gameManagement).updateGame(any(Game.class));
        verify(lobbyService, times(2)).sendToAllInLobby(eq(mockGame.getLobby()), any());
        verify(mockMap).setOutbreakCallback(any());
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
        request.initWithMessage(message);
        post(request);

        waitForLock();

        DrawPlayerCardResponse drawPlayerCardResponse = responses.stream()
                .filter(DrawPlayerCardResponse.class::isInstance)
                .map(DrawPlayerCardResponse.class::cast).findFirst()
                .orElse(null);

        assertThat(drawPlayerCardResponse).isNotNull();
    }

    @Test
    @DisplayName("Player has more than max hand cards - Successful sending of the ReleaseToDrawCardResponse")
    void sendReleaseToDrawCardResponse() throws InterruptedException {
        Function<Game, ReleaseToDrawPlayerCardResponse> responseSupplier = ReleaseToDrawPlayerCardResponse::new;
        cardService.sendReleaseToDrawCardResponse(game, message, responseSupplier);
        waitForLock();

        ReleaseToDrawPlayerCardResponse response = (ReleaseToDrawPlayerCardResponse) event;

        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("")
    void allowPlayerCardDrawing() throws InterruptedException {
        cardService.allowDrawingOrDiscarding(game, message, ReleaseToDrawPlayerCardResponse.class);

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
    void allowInfectionCardDrawing() throws InterruptedException {
        cardService.allowDrawingOrDiscarding(game, message, ReleaseToDrawInfectionCardResponse.class);

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
        Class<? extends AbstractGameResponse> responseMessage = AbstractGameResponse.class;

        assertThatThrownBy(() -> cardService.allowDrawingOrDiscarding(game, message, responseMessage))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Draw Player Card Request - Draw Epidemic Card")
    void onDrawPlayerCardRequest_drawEpidemicCard() {
        setupEpidemicTestState();
        when(gameManagement.getGame(any())).thenReturn(Optional.of(mockGame));
        when(mockGame.findPlayer(any())).thenReturn(Optional.of(mockPlayer));
        when(mockGame.getCurrentTurn()).thenReturn(mockTurn);
        when(gameManagement.drawPlayerCard(any())).thenReturn(epidemicCard);
        when(triggerableService.checkForSendingManualTriggerables(any(), any(), any())).thenReturn(false);

        DrawPlayerCardRequest request = new DrawPlayerCardRequest(mockGame, mockPlayer);
        request.initWithMessage(message);
        post(request);

        verify(mockGame).increaseInfectionLevel();
        verify(cardManagement).drawInfectionCardFromTheBottom(mockGame);
        verify(lobbyService).sendToAllInLobby(eq(mockGame.getLobby()), any(RetrieveUpdatedGameServerMessage.class));
    }

    private void setupEpidemicTestState() {
        mockGame = mock(Game.class);
        epidemicCard = mock(EpidemicCard.class);
        mockBottomCard = mock(InfectionCard.class);
        mockField = mock(Field.class);
        mockPlague = mock(Plague.class);
        mockPlayer = mock(Player.class);
        mockTurn = mock(PlayerTurn.class);
        mockMap = mock(GameMap.class);
        discardStack = spy(new CardStack<>());
        drawStack = spy(new CardStack<>());

        InfectionMarker mockInfectionMarker = mock(InfectionMarker.class);
        when(mockGame.getInfectionMarker()).thenReturn(mockInfectionMarker);
        when(mockInfectionMarker.hasInfectionrateChanged()).thenReturn(true);
        when(mockInfectionMarker.getLevelValue()).thenReturn(2);

        CardStack<PlayerCard> playerDrawStack = new CardStack<>();
        playerDrawStack.push(epidemicCard);

        when(gameManagement.getGame(any(Game.class))).thenReturn(Optional.of(mockGame));
        when(gameManagement.drawPlayerCard(any())).thenReturn(epidemicCard);
        when(mockGame.getPlayerDrawStack()).thenReturn(playerDrawStack);
        when(mockGame.getPlayersInTurnOrder()).thenReturn(List.of(mockPlayer));
        when(mockGame.getCurrentTurn()).thenReturn(mockTurn);
        when(mockGame.getLobby()).thenReturn(mock(Lobby.class));
        when(mockGame.getMap()).thenReturn(mockMap);
        when(mockGame.getInfectionDiscardStack()).thenReturn(discardStack);
        when(mockGame.getInfectionDrawStack()).thenReturn(drawStack);

        when(cardManagement.drawInfectionCardFromTheBottom(mockGame)).thenReturn(mockBottomCard);
        when(mockBottomCard.getAssociatedField()).thenReturn(mockField);
        when(mockField.getPlague()).thenReturn(mockPlague);
    }

    @Test
    @DisplayName("Process Bottom Card - With Antidote Marker")
    void processBottomInfectionCard_EpidemicBehavior_withAntidoteMarker() {
        setupEpidemicTestState();

        when(gameManagement.getGame(any(Game.class))).thenReturn(Optional.of(mockGame));
        when(mockGame.findPlayer(any(Player.class))).thenReturn(Optional.of(mockPlayer));
        when(triggerableService.checkForSendingManualTriggerables(any(), any(), any())).thenReturn(false);

        CardStack<PlayerCard> playerDrawStack = new CardStack<>();
        playerDrawStack.push(epidemicCard);
        when(mockGame.getPlayerDrawStack()).thenReturn(playerDrawStack);
        when(gameManagement.drawPlayerCard(any())).thenReturn(epidemicCard);

        PlayerTurn mockTurn = mock(PlayerTurn.class);
        when(mockGame.getCurrentTurn()).thenReturn(mockTurn);
        when(mockTurn.isPlayerCardDrawExecutable()).thenReturn(true);

        when(cardManagement.drawInfectionCardFromTheBottom(mockGame)).thenReturn(mockBottomCard);
        when(mockBottomCard.getAssociatedField()).thenReturn(mockField);
        when(mockField.getPlague()).thenReturn(mockPlague);
        when(mockGame.hasAntidoteMarkerForPlague(mockPlague)).thenReturn(true);

        DrawPlayerCardRequest request = new DrawPlayerCardRequest(mockGame, mockPlayer);
        request.initWithMessage(message);
        post(request);

        InOrder inOrder = inOrder(mockGame, mockField, cardManagement);
        inOrder.verify(mockGame).increaseInfectionLevel();
        verify(mockField, never()).isInfectable(mockPlague);
        inOrder.verify(cardManagement).discardInfectionCard(mockGame, mockBottomCard);
        verify(mockMap, never()).startOutbreak(any(), any(), any());
    }

    @Test
    @DisplayName("Process Bottom Card - Causes Outbreak")
    void processBottomInfectionCard_EpidemicBehavior_causesOutbreak() {
        setupEpidemicTestState();

        when(mockGame.hasAntidoteMarkerForPlague(mockPlague)).thenReturn(false);
        when(mockField.isInfectable(mockPlague)).thenReturn(false);

        when(gameManagement.getGame(any(Game.class))).thenReturn(Optional.of(mockGame));
        when(mockGame.findPlayer(any(Player.class))).thenReturn(Optional.of(mockPlayer));
        when(triggerableService.checkForSendingManualTriggerables(any(), any(), any())).thenReturn(false);

        CardStack<PlayerCard> playerDrawStack = new CardStack<>();
        playerDrawStack.push(epidemicCard);
        when(mockGame.getPlayerDrawStack()).thenReturn(playerDrawStack);
        when(gameManagement.drawPlayerCard(any())).thenReturn(epidemicCard);

        PlayerTurn mockTurn = mock(PlayerTurn.class);
        when(mockGame.getCurrentTurn()).thenReturn(mockTurn);
        when(mockTurn.isPlayerCardDrawExecutable()).thenReturn(true);

        when(cardManagement.drawInfectionCardFromTheBottom(mockGame)).thenReturn(mockBottomCard);
        when(mockBottomCard.getAssociatedField()).thenReturn(mockField);
        when(mockField.getPlague()).thenReturn(mockPlague);

        DrawPlayerCardRequest request = new DrawPlayerCardRequest(mockGame, mockPlayer);
        request.initWithMessage(message);
        post(request);

        InOrder inOrder = inOrder(mockGame, mockMap, cardManagement);
        inOrder.verify(mockGame).increaseInfectionLevel();
        verify(mockMap, times(Game.EPIDEMIC_CARD_DRAW_NUMBER_OF_INFECTIONS)).startOutbreak(eq(mockField), eq(mockPlague), any());
        inOrder.verify(cardManagement).discardInfectionCard(mockGame, mockBottomCard);

        verify(mockGame, atLeast(1)).hasAntidoteMarkerForPlague(mockPlague);
        verify(mockField, times(Game.EPIDEMIC_CARD_DRAW_NUMBER_OF_INFECTIONS)).isInfectable(mockPlague);
    }

    @Test
    @DisplayName("Process Bottom Card - Multiple Infections")
    void processBottomInfectionCard_EpidemicBehavior_multipleInfections() {
        setupEpidemicTestState();

        List<List<Field>> infectedFieldsList = new ArrayList<>();
        when(mockTurn.getInfectedFieldsInTurn()).thenReturn(infectedFieldsList);

        when(mockGame.hasAntidoteMarkerForPlague(mockPlague)).thenReturn(false);
        when(mockField.isInfectable(mockPlague)).thenReturn(true);
        when(mockField.getPlague()).thenReturn(mockPlague);

        when(gameManagement.getGame(any(Game.class))).thenReturn(Optional.of(mockGame));
        when(mockGame.findPlayer(any(Player.class))).thenReturn(Optional.of(mockPlayer));
        when(triggerableService.checkForSendingManualTriggerables(any(), any(), any())).thenReturn(false);

        CardStack<PlayerCard> playerDrawStack = new CardStack<>();
        playerDrawStack.push(epidemicCard);
        when(mockGame.getPlayerDrawStack()).thenReturn(playerDrawStack);
        when(gameManagement.drawPlayerCard(any())).thenReturn(epidemicCard);

        when(cardManagement.drawInfectionCardFromTheBottom(mockGame)).thenReturn(mockBottomCard);
        when(mockBottomCard.getAssociatedField()).thenReturn(mockField);
        when(mockGame.getCurrentTurn()).thenReturn(mockTurn);
        when(mockTurn.isPlayerCardDrawExecutable()).thenReturn(true);

        DrawPlayerCardRequest request = new DrawPlayerCardRequest(mockGame, mockPlayer);
        request.initWithMessage(message);
        post(request);

        verify(mockField, times(Game.EPIDEMIC_CARD_DRAW_NUMBER_OF_INFECTIONS)).isInfectable(mockPlague);
        verify(mockGame).increaseInfectionLevel();
        verify(cardManagement).discardInfectionCard(mockGame, mockBottomCard);
        verify(mockMap, never()).startOutbreak(any(), any(), any());
        verify(mockTurn, times(Game.EPIDEMIC_CARD_DRAW_NUMBER_OF_INFECTIONS)).getInfectedFieldsInTurn();
    }

    @Test
    @DisplayName("Trigger Epidemic - Complete Process")
    void triggerEpidemic_completeProcess() {
        setupEpidemicTestState();

        when(mockField.isInfectable(any(Plague.class))).thenReturn(false);
        when(mockGame.hasAntidoteMarkerForPlague(any(Plague.class))).thenReturn(false);
        when(gameManagement.getGame(any(Game.class))).thenReturn(Optional.of(mockGame));
        when(mockGame.findPlayer(any(Player.class))).thenReturn(Optional.of(mockPlayer));
        when(triggerableService.checkForSendingManualTriggerables(any(), any(), any())).thenReturn(false);

        InfectionCard card1 = mock(InfectionCard.class);
        InfectionCard card2 = mock(InfectionCard.class);
        discardStack.push(card1);
        discardStack.push(card2);

        PlayerTurn mockTurn = mock(PlayerTurn.class);
        when(mockGame.getCurrentTurn()).thenReturn(mockTurn);
        when(mockTurn.isPlayerCardDrawExecutable()).thenReturn(true);

        DrawPlayerCardRequest request = new DrawPlayerCardRequest(mockGame, mockPlayer);
        request.initWithMessage(message);
        post(request);

        InOrder inOrder = inOrder(mockGame, cardManagement, discardStack, mockMap);
        inOrder.verify(mockGame).increaseInfectionLevel();
        inOrder.verify(cardManagement).drawInfectionCardFromTheBottom(mockGame);
        inOrder.verify(mockMap).startOutbreak(eq(mockField), any(Plague.class), any());
        inOrder.verify(discardStack).shuffle();

        verify(mockGame, atLeast(1)).getInfectionDrawStack();
        verify(discardStack).clear();
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
