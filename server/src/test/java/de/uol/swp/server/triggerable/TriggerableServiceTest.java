package de.uol.swp.server.triggerable;

import de.uol.swp.common.action.simple.MoveAllyToAllyAction;
import de.uol.swp.common.action.simple.car.CarActionForAlly;
import de.uol.swp.common.action.simple.charter_flight.CharterFlightActionForAlly;
import de.uol.swp.common.action.simple.direct_flight.DirectFlightActionForAlly;
import de.uol.swp.common.action.simple.shuttle_flight.ShuttleFlightActionForAlly;
import de.uol.swp.common.answerable.server_message.AnswerableServerMessage;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.card.event_card.AQuietNightEventCard;
import de.uol.swp.common.card.event_card.EventCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.GameDifficulty;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.role.RoleAbility;
import de.uol.swp.common.role.RoleCard;
import de.uol.swp.common.triggerable.AutoTriggerable;
import de.uol.swp.common.triggerable.ManualTriggerable;
import de.uol.swp.common.triggerable.Triggerable;
import de.uol.swp.common.triggerable.request.TriggerableRequest;
import de.uol.swp.common.triggerable.server_message.TriggerableServerMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.EventBusBasedTest;
import de.uol.swp.server.game.GameManagement;
import de.uol.swp.server.game.GameService;
import de.uol.swp.server.lobby.LobbyService;
import de.uol.swp.server.util.TestUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static de.uol.swp.server.util.TestUtils.createMapType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TriggerableServiceTest extends EventBusBasedTest {
    private TriggerableService triggerableService;
    private GameManagement gameManagement;
    private GameService gameService;
    private LobbyService lobbyService;

    private Game game;
    private Player player1;
    private Player player2;
    private GameDifficulty difficulty;

    @BeforeEach
    void setUp() {
        gameManagement = mock(GameManagement.class);
        gameService = mock(GameService.class);
        lobbyService = mock(LobbyService.class);
        difficulty = GameDifficulty.getDefault();
        final EventBus eventBus = getBus();

        triggerableService = new TriggerableService(eventBus, gameManagement, gameService, lobbyService);

        final User user = new UserDTO("user", "", "");
        final Lobby lobby = new LobbyDTO("lobby", user);
        player1 = lobby.getPlayerForUser(user);

        if (player1 == null) {
            player1 = new AIPlayer(user.getUsername());
            lobby.addPlayer(player1);
        }

        player2 = new AIPlayer("ai");
        lobby.addPlayer(player2);

        RoleCard roleCard = new RoleCard("TestRole", null, new RoleAbility(
                Map.of(),
                List.of(
                        MoveAllyToAllyAction.class,
                        CarActionForAlly.class,
                        CharterFlightActionForAlly.class,
                        DirectFlightActionForAlly.class,
                        ShuttleFlightActionForAlly.class
                ),
                List.of()
        ));
        player1.setRole(roleCard);
        player2.setRole(roleCard);

        final List<Plague> plagues = List.of();

        game = new Game(lobby, createMapType(), new ArrayList<>(lobby.getPlayers()), plagues, difficulty);
        game.addPlayerTurn(TestUtils.createPlayerTurn(
                game,
                player1,
                2,
                2,
                2
        ));

        doAnswer(invocationOnMock -> {
            eventBus.post(invocationOnMock.getArguments()[1]);
            return null;
        }).when(lobbyService).sendToAllInLobby(any(), any());

        when(gameManagement.getGame(game))
                .thenReturn(Optional.of(game));
    }

    @Test
    @DisplayName("Should return true if there is a ManualTriggerable to be sent, should also send a Message")
    void checkForSendingManualTriggerables_true() throws InterruptedException {
        final EventCard eventCard = new AQuietNightEventCard();

        removeEventCardsFromPlayer(player1);
        removeEventCardsFromPlayer(player2);
        player1.addHandCard(eventCard);
        game.getCurrentTurn().resetManualTriggerables();

        assertThat(triggerableService.checkForSendingManualTriggerables(game, mock(), player1))
                .isTrue();

        waitForLock();

        assertThat(event)
                .isInstanceOf(TriggerableServerMessage.class);
        final TriggerableServerMessage triggerableServerMessage = (TriggerableServerMessage) event;
        assertThat(triggerableServerMessage.getTriggerable())
                .usingRecursiveComparison()
                .isEqualTo(eventCard);
    }

    @Test
    @DisplayName("Should return false if there is no ManualTriggerable to be sent")
    void checkForSendingManualTriggerables_false() {
        removeEventCardsFromPlayer(player1);
        removeEventCardsFromPlayer(player2);
        game.getCurrentTurn().resetManualTriggerables();

        assertThat(triggerableService.checkForSendingManualTriggerables(game, mock(), player1))
                .isFalse();
    }

    private void removeEventCardsFromPlayer(final Player player) {
        final List<EventCard> eventCardsToRemove = new LinkedList<>();
        for (final PlayerCard playerCard : player.getHandCards()) {
            if (playerCard instanceof EventCard eventCard) {
                eventCardsToRemove.add(eventCard);
            }
        }
        eventCardsToRemove.forEach(player::removeHandCard);
    }

    @Test
    @DisplayName("Should trigger the triggerable")
    void onTriggerableRequest() throws InterruptedException {
        final ManualTriggerable triggerable = createTriggerableMock(ManualTriggerable.class);
        final Message cause = null;
        final Player returningPlayer = null;

        final TriggerableRequest triggerableRequest = new TriggerableRequest(triggerable, cause, returningPlayer);
        post(triggerableRequest);

        waitForLock();

        verify(triggerable, times(1))
                .trigger();

        assertThat(event)
                .isInstanceOf(AnswerableServerMessage.class);
        final AnswerableServerMessage answerableServerMessage = (AnswerableServerMessage) event;
        final AnswerableServerMessage expected = new AnswerableServerMessage(triggerable, cause, returningPlayer);
        assertThat(answerableServerMessage)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Should trigger and discard the EventCard")
    void onTriggerableRequest_EventCard() throws InterruptedException {
        final EventCard eventCard = createTriggerableMock(EventCard.class);
        when(eventCard.getPlayer())
                .thenReturn(player1);
        player1.addHandCard(eventCard);
        final Message cause = null;
        final Player returningPlayer = null;

        final TriggerableRequest triggerableRequest = new TriggerableRequest(eventCard, cause, returningPlayer);
        post(triggerableRequest);

        waitForLock();

        verify(eventCard, times(1))
                .trigger();
    }

    @Test
    @DisplayName("Should trigger AutoTriggerable")
    void autoTriggerableTriggeredIfConditionsMet() throws InterruptedException {
        final AutoTriggerable triggerable = createTriggerableMock(AutoTriggerable.class);
        final Message cause = null;
        final Player returningPlayer = null;

        final TriggerableRequest triggerableRequest = new TriggerableRequest(triggerable, cause, returningPlayer);
        post(triggerableRequest);

        waitForLock();

        verify(triggerable, times(1))
                .trigger();
    }

    private <T extends Triggerable> T createTriggerableMock(final Class<T> typeOfMock) {
        final T mock = mock(typeOfMock);
        doAnswer(invocationOnMock -> {
            mock.trigger();
            return null;
        }).when(mock).execute();
        when(mock.getGame())
                .thenReturn(game);
        return mock;
    }

    @Subscribe
    public void onEvent(final AnswerableServerMessage answerableServerMessage) {
        handleEvent(answerableServerMessage);
    }

    @Subscribe
    public void onEvent(final TriggerableServerMessage triggerableServerMessage) {
        handleEvent(triggerableServerMessage);
    }
}