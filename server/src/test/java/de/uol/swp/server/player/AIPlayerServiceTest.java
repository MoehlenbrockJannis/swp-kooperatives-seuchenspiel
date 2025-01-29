package de.uol.swp.server.player;

import de.uol.swp.common.approvable.Approvable;
import de.uol.swp.common.approvable.ApprovableMessageStatus;
import de.uol.swp.common.approvable.server_message.ApprovableServerMessage;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.card.response.ReleaseToDiscardPlayerCardResponse;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.server_message.CreateGameServerMessage;
import de.uol.swp.common.game.server_message.RetrieveUpdatedGameServerMessage;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.turn.PlayerTurn;
import de.uol.swp.common.user.Session;
import de.uol.swp.server.EventBusBasedTest;
import de.uol.swp.server.game.GameService;
import org.greenrobot.eventbus.EventBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Test class for AIPlayerService
 *
 * @see de.uol.swp.server.player.AIPlayerService
 *
 * @author Silas van Thiel
 * @since 2025-01-28
 */
public class AIPlayerServiceTest extends EventBusBasedTest {

    private AIPlayerService aiPlayerService;
    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameService = mock(GameService.class);
        final EventBus eventBus = getBus();

        aiPlayerService = new AIPlayerService(eventBus, gameService);
    }

    @Test
    @DisplayName("Should handle AI player turn when a game is created")
    void onCreateGameServerMessage_handlesAIPlayerTurn() {
        Game game = mock(Game.class);
        PlayerTurn playerTurn = mock(PlayerTurn.class);
        Player aiPlayer = mock(AIPlayer.class);

        when(game.getCurrentTurn()).thenReturn(playerTurn);
        when(playerTurn.getPlayer()).thenReturn(aiPlayer);

        CreateGameServerMessage message = new CreateGameServerMessage(game);
        post(message);

        verify(game, times(2)).getCurrentTurn();
        assertThat(playerTurn.getPlayer()).isInstanceOf(AIPlayer.class);
    }

    @Test
    @DisplayName("Should handle AI player turn when a game is updated")
    void onRetrieveUpdatedGameServerMessage() {
        Game game = mock(Game.class);
        PlayerTurn playerTurn = mock(PlayerTurn.class);
        Player aiPlayer = mock(AIPlayer.class);

        when(game.getCurrentTurn()).thenReturn(playerTurn);
        when(playerTurn.getPlayer()).thenReturn(aiPlayer);

        RetrieveUpdatedGameServerMessage message = new RetrieveUpdatedGameServerMessage(game);
        post(message);

        verify(game, times(2)).getCurrentTurn();
        assertThat(playerTurn.getPlayer()).isInstanceOf(AIPlayer.class);
    }

    @Test
    @DisplayName("Should handle AI player action phase correctly")
    void handleAIPlayerActionPhase_executesCorrectAction() {
        Game game = mock(Game.class);
        Player aiPlayer = mock(AIPlayer.class);
        Field currentField = mock(Field.class);
        Field targetField = mock(Field.class);

        when(aiPlayer.getCurrentField()).thenReturn(currentField);
        when(currentField.getNeighborFields()).thenReturn(List.of(targetField));

        aiPlayerService.handleAIPlayerActionPhase(game, aiPlayer);

        verify(currentField, times(1)).getNeighborFields();
        verify(targetField, never()).getNeighborFields();
    }

    @Test
    @DisplayName("Should draw player cards correctly during AI player turn")
    void handleAIPlayerCardDrawPhase_executesCorrectRequest() {
        Game game = mock(Game.class);
        AIPlayer aiPlayer = mock(AIPlayer.class);
        Session session = mock(Session.class);

        when(gameService.getSession(aiPlayer)).thenReturn(java.util.Optional.of(session));

        aiPlayerService.handleAIPlayerCardDrawPhase(game, aiPlayer);

        verify(gameService, times(1)).getSession(aiPlayer);
    }

    @Test
    @DisplayName("Should draw infection cards correctly during AI player turn")
    void handleAIInfectionCardDrawPhase_executesCorrectRequest() {
        Game game = mock(Game.class);
        AIPlayer aiPlayer = mock(AIPlayer.class);
        Session session = mock(Session.class);

        when(gameService.getSession(aiPlayer)).thenReturn(java.util.Optional.of(session));

        aiPlayerService.handleAIInfectionCardDrawPhase(game, aiPlayer);

        verify(gameService, times(1)).getSession(aiPlayer);
    }

    @Test
    @DisplayName("Should handle approvable server message for AI player")
    void onApprovableServerMessage_approvesAutomatically() {
        Game game = mock(Game.class);
        AIPlayer aiPlayer = mock(AIPlayer.class);
        Approvable approvable = mock(Approvable.class);
        when(approvable.getApprovingPlayer()).thenReturn(aiPlayer);

        ApprovableServerMessage message = new ApprovableServerMessage(ApprovableMessageStatus.OUTBOUND, approvable);
        post(message);

        verify(message.getApprovable(), times(1)).approve();
    }

    @Test
    @DisplayName("Should discard a random card during AI player turn")
    void onReceiveReleaseToDiscardPlayerCardResponse_discardsCard() {
        Game game = mock(Game.class);
        Player aiPlayer = mock(AIPlayer.class);
        PlayerTurn playerTurn = mock(PlayerTurn.class);
        PlayerCard card = mock(PlayerCard.class);

        when(game.getCurrentTurn()).thenReturn(playerTurn);
        when(game.getCurrentPlayer()).thenReturn(aiPlayer);
        when(playerTurn.getPlayer()).thenReturn(aiPlayer);
        when(aiPlayer.getHandCards()).thenReturn(List.of(card));

        ReleaseToDiscardPlayerCardResponse message = new ReleaseToDiscardPlayerCardResponse(game);
        post(message);

        verify(aiPlayer, times(1)).getHandCards();
        assertThat(aiPlayer.getHandCards()).contains(card);
    }

}
