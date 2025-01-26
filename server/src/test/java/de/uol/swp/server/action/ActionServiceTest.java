package de.uol.swp.server.action;

import de.uol.swp.common.action.Action;
import de.uol.swp.common.action.request.ActionRequest;
import de.uol.swp.common.action.simple.WaiveAction;
import de.uol.swp.common.card.response.ReleaseToDrawPlayerCardResponse;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.server_message.RetrieveUpdatedGameServerMessage;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.player.turn.PlayerTurn;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.EventBusBasedTest;
import de.uol.swp.server.card.CardService;
import de.uol.swp.server.communication.UUIDSession;
import de.uol.swp.server.game.GameManagement;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static de.uol.swp.server.util.TestUtils.createMapType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ActionServiceTest extends EventBusBasedTest {
    private ActionService actionService;
    private CardService cardService;
    private GameManagement gameManagement;

    @BeforeEach
    void setUp() {
        cardService = mock();
        gameManagement = mock();
        EventBus eventBus = getBus();

        actionService = new ActionService(eventBus, cardService, gameManagement);
    }

    private static Stream<Arguments> onActionRequestSource() {
        return Stream.of(
                Arguments.of(true, 0),
                Arguments.of(false, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("onActionRequestSource")
    void onActionRequest(final boolean isInActionPhaseAfterActionExecution, final int timesCardServiceCalled) throws InterruptedException {
        final User user = new UserDTO("user", "pass", "");
        final Lobby lobby = new LobbyDTO("lobby", user, 1, 2);

        final MapType mapType = createMapType();

        final Game game = new Game(lobby, mapType, new ArrayList<>(lobby.getPlayers()), List.of());
        final PlayerTurn playerTurn = mock();
        when(playerTurn.isInActionPhase())
                .thenReturn(isInActionPhaseAfterActionExecution);
        game.addPlayerTurn(playerTurn);

        final Action action = new WaiveAction();
        action.setGame(game);
        action.setExecutingPlayer(lobby.getPlayerForUser(user));
        
        final ActionRequest actionRequest = new ActionRequest(game, action);
        final Session session = UUIDSession.create(user);
        actionRequest.setSession(session);
        post(actionRequest);

        waitForLock();

        verify(playerTurn, times(1))
                .executeCommand(action);
        verify(gameManagement, times(1))
                .updateGame(game);
        verify(cardService, times(timesCardServiceCalled))
                .allowDrawingOrDiscarding(game, session, ReleaseToDrawPlayerCardResponse.class);
        assertThat(this.event)
                .isInstanceOf(RetrieveUpdatedGameServerMessage.class);
    }

    @Subscribe
    public void onEvent(RetrieveUpdatedGameServerMessage retrieveUpdatedGameServerMessage) {
        handleEvent(retrieveUpdatedGameServerMessage);
    }
}