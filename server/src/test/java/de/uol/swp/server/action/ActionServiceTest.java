package de.uol.swp.server.action;

import de.uol.swp.common.action.Action;
import de.uol.swp.common.action.advanced.transfer_card.SendCardAction;
import de.uol.swp.common.action.advanced.transfer_card.ShareKnowledgeAction;
import de.uol.swp.common.action.request.ActionRequest;
import de.uol.swp.common.action.simple.WaiveAction;
import de.uol.swp.common.card.CityCard;
import de.uol.swp.common.card.response.ReleaseToDiscardPlayerCardResponse;
import de.uol.swp.common.card.response.ReleaseToDrawPlayerCardResponse;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.GameDifficulty;
import de.uol.swp.common.game.server_message.RetrieveUpdatedGameServerMessage;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.UserPlayer;
import de.uol.swp.common.game.turn.PlayerTurn;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.EventBusBasedTest;
import de.uol.swp.server.card.CardService;
import de.uol.swp.server.communication.UUIDSession;
import de.uol.swp.server.game.GameManagement;
import de.uol.swp.server.player.PlayerManagement;
import de.uol.swp.server.triggerable.TriggerableService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static de.uol.swp.server.util.TestUtils.createMapType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ActionServiceTest extends EventBusBasedTest {
    private static final BiConsumer<ActionServiceTest, Action> emptyRunnable = (actionServiceTest, action) -> {};

    private ActionService actionService;
    private CardService cardService;
    private GameManagement gameManagement;
    private TriggerableService triggerableService;
    private PlayerManagement playerManagement;

    private Game game;
    private UserPlayer userPlayer1;
    private UserPlayer userPlayer2;

    @BeforeEach
    void setUp() {
        cardService = mock();
        gameManagement = mock();
        triggerableService = mock();
        playerManagement = mock();
        EventBus eventBus = getBus();

        actionService = new ActionService(eventBus, cardService, gameManagement, triggerableService, playerManagement);

        final User user = new UserDTO("user", "pass", "");
        final User user2 = new UserDTO("user1", "pass", "");
        userPlayer2 = new UserPlayer(user2);

        final Lobby lobby = new LobbyDTO("lobby", user);
        userPlayer1 = (UserPlayer) lobby.getPlayerForUser(user);
        final GameDifficulty difficulty = GameDifficulty.getDefault();

        lobby.addPlayer(userPlayer2);

        final MapType mapType = createMapType();

        game = new Game(lobby, mapType, new ArrayList<>(lobby.getPlayers()), List.of(), difficulty);
    }

    private static Stream<Arguments> onActionRequestSource() {
        return Stream.of(
                Arguments.of(true, 0, new WaiveAction(), emptyRunnable, emptyRunnable),
                Arguments.of(false, 1, new WaiveAction(), emptyRunnable, emptyRunnable),
                Arguments.of(true, 0, new SendCardAction(), (BiConsumer<ActionServiceTest, Action>) ActionServiceTest::executeShareKnowledgeActionSetup, (BiConsumer<ActionServiceTest, Action>) ActionServiceTest::executeShareKnowledgeActionAssertions)
        );
    }

    private void executeShareKnowledgeActionSetup(final Action action) {
        final Player receiver = userPlayer2;
        if (action instanceof ShareKnowledgeAction shareKnowledgeAction) {
            shareKnowledgeAction.setTargetPlayer(receiver);
            CityCard cityCard = userPlayer1.getHandCards().stream()
                            .filter(CityCard.class::isInstance)
                            .map(CityCard.class::cast)
                            .findFirst().orElseThrow();
            shareKnowledgeAction.setTransferredCard(cityCard);
        }
        when(playerManagement.findSession(receiver))
                .thenReturn(Optional.of(mock()));
        when(cardService.doesPlayerRequireDiscardingOfHandCards(receiver, game))
                .thenReturn(true);
    }

    private void executeShareKnowledgeActionAssertions(final Action action) {
        verify(cardService, times(1))
                .allowDrawingOrDiscarding(eq(game), any(), eq(ReleaseToDiscardPlayerCardResponse.class));
    }

    @ParameterizedTest
    @MethodSource("onActionRequestSource")
    void onActionRequest(final boolean isInActionPhaseAfterActionExecution,
                         final int timesCardServiceCalled,
                         final Action action,
                         final BiConsumer<ActionServiceTest, Action> additionalSetup,
                         final BiConsumer<ActionServiceTest, Action> additionalAssertions) throws InterruptedException {
        final PlayerTurn playerTurn = mock();
        when(playerTurn.isActionExecutable())
                .thenReturn(isInActionPhaseAfterActionExecution);
        game.addPlayerTurn(playerTurn);

        when(gameManagement.getGame(game))
                .thenReturn(Optional.of(game));

        additionalSetup.accept(this, action);

        action.setGame(game);
        action.setExecutingPlayer(userPlayer1);
        
        final ActionRequest actionRequest = new ActionRequest(game, action);
        final Session session = UUIDSession.create(userPlayer1.getUser());
        actionRequest.setSession(session);
        post(actionRequest);

        waitForLock();

        verify(playerTurn, times(1))
                .executeCommand(action);
        verify(gameManagement, times(1))
                .updateGame(game);
        verify(cardService, times(timesCardServiceCalled))
                .allowDrawingOrDiscarding(game, actionRequest, ReleaseToDrawPlayerCardResponse.class);
        assertThat(this.event)
                .isInstanceOf(RetrieveUpdatedGameServerMessage.class);

        additionalAssertions.accept(this, action);
    }

    @Subscribe
    public void onEvent(RetrieveUpdatedGameServerMessage retrieveUpdatedGameServerMessage) {
        handleEvent(retrieveUpdatedGameServerMessage);
    }
}