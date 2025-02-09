package de.uol.swp.common.player.turn;

import de.uol.swp.common.action.Action;
import de.uol.swp.common.action.DiscardCardsAction;
import de.uol.swp.common.action.simple.WaiveAction;
import de.uol.swp.common.card.CityCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.GameDifficulty;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.UserPlayer;
import de.uol.swp.common.role.RoleAbility;
import de.uol.swp.common.role.RoleCard;
import de.uol.swp.common.triggerable.AutoTriggerable;
import de.uol.swp.common.triggerable.ManualTriggerable;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.util.Color;
import de.uol.swp.common.util.Command;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static de.uol.swp.common.util.TestUtils.createMapType;
import static de.uol.swp.common.util.TestUtils.createPlayerTurn;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * This class contains unit tests for the PlayerTurn class.
 * It sets up a testing environment by initializing instances and mocks required for the tests.
 * The setup includes:
 * - defaultUser with the name "Joerg", ID "333", and email "Joerg@mail.com"
 * - defaultPlayer as a UserPlayer instance based on the defaultUser
 * - defaultGame as a mocked Game instance
 * - numberOfActionsToDo set to 4
 * - numberOfPlayerCardsToDraw set to 2
 * - defaultPlayerTurn as a PlayerTurn instance using the defaultGame, defaultPlayer,
 *   numberOfActionsToDo, and numberOfPlayerCardsToDraw
 * - command as a mocked Command instance
 *
 * The class tests various methods of the PlayerTurn class to ensure correct behavior.
 * Each test case ensures that the methods in PlayerTurn work as expected under different conditions.
 *
 * @see PlayerTurn
 * @since 2024-09-18
 */
class PlayerTurnTest {
    private User defaultUser;
    private User defaultUser2;
    private Player defaultPlayer;
    private Player defaultPlayer2;
    private RoleCard roleCard1;
    private RoleCard roleCard2;
    private RoleAbility roleAbility;
    private Game defaultGame;
    private PlayerTurn defaultPlayerTurn;
    private int numberOfActionsToDo;
    private int numberOfPlayerCardsToDraw;
    private int numberOfInfectionCardsToDraw;
    private Command command;
    private GameDifficulty difficulty;

    /**
     * This method is executed before each test case.
     * It initializes the test environment by setting up instances and mocks required for the tests.
     * Specifically, it sets up:
     * - defaultUser with the name "Joerg", ID "333", and email "Joerg@mail.com"
     * - defaultPlayer as a UserPlayer instance based on the defaultUser
     * - defaultGame as a mocked Game instance
     * - numberOfActionsToDo set to 4
     * - numberOfPlayerCardsToDraw set to 2
     * - defaultPlayerTurn as a PlayerTurn instance using the defaultGame, defaultPlayer,
     *   numberOfActionsToDo, and numberOfPlayerCardsToDraw
     * - command as a mocked Command instance
     *
     * This setup ensures that each test starts with a consistent and predefined state,
     * and provides the necessary mocks and objects for testing.
     *
     * @since 2024-09-18
     */
    @BeforeEach
    public void setUp() {
        this.defaultUser = new UserDTO("Joerg", "333", "Joerg@mail.com");
        this.defaultUser2 = new UserDTO("Juergen", "333", "Juergen@mail.com");
        final Lobby lobby = new LobbyDTO("lobby", defaultUser, 1, 2);

        this.roleAbility = mock(RoleAbility.class);
        this.roleCard1 = new RoleCard("", new Color(), roleAbility);
        this.roleCard2 = new RoleCard("", new Color(), roleAbility);
        this.defaultPlayer = new UserPlayer(this.defaultUser);
        this.defaultPlayer.setRole(roleCard1);
        this.difficulty = GameDifficulty.getDefault();
        this.defaultPlayer2 = new UserPlayer(this.defaultUser2);
        this.defaultPlayer2.setRole(roleCard2);

        lobby.addPlayer(this.defaultPlayer);
        lobby.addPlayer(this.defaultPlayer2);

        final MapType mapType = createMapType();

        this.defaultGame = new Game(lobby, mapType, new ArrayList<>(lobby.getPlayers()), List.of(), difficulty);
        this.numberOfActionsToDo = 4;
        this.numberOfPlayerCardsToDraw = 2;
        this.numberOfInfectionCardsToDraw = 1;

        this.defaultPlayerTurn = createPlayerTurn(
                defaultGame,
                this.defaultPlayer,
                this.numberOfActionsToDo,
                this.numberOfPlayerCardsToDraw,
                this.numberOfInfectionCardsToDraw
        );
        this.defaultGame.addPlayerTurn(defaultPlayerTurn);

        this.command = mock(Command.class);
    }

    @Test
    @DisplayName("Should reduce the number of player cards required to draw by one")
    void reduceNumberOfPlayerCardsToDraw() {
        assertThat(defaultPlayerTurn.getNumberOfPlayerCardsToDraw())
                .isEqualTo(numberOfPlayerCardsToDraw);

        defaultPlayerTurn.reduceNumberOfPlayerCardsToDraw();

        assertThat(defaultPlayerTurn.getNumberOfPlayerCardsToDraw())
                .isEqualTo(numberOfPlayerCardsToDraw - 1);
    }

    @Test
    @DisplayName("Should reduce the number of infection cards required to draw by one")
    void reduceNumberOfInfectionCardsToDraw() {
        assertThat(defaultPlayerTurn.getNumberOfInfectionCardsToDraw())
                .isEqualTo(numberOfInfectionCardsToDraw);

        defaultPlayerTurn.reduceNumberOfInfectionCardsToDraw();

        assertThat(defaultPlayerTurn.getNumberOfInfectionCardsToDraw())
                .isEqualTo(numberOfInfectionCardsToDraw - 1);
    }

    @Test
    @DisplayName("")
    void getNextAutoTriggerable() {
        // TODO: 17.09.2024
    }

    
    @Test
    @DisplayName("Should return false if there is no next auto triggerable to check, true otherwise")
    void hasNextAutoTriggerable() {
        final boolean isAutoTriggerableInGame = defaultGame.getTriggerables().stream()
                .anyMatch(AutoTriggerable.class::isInstance);

        assertThat(this.defaultPlayerTurn.hasNextAutoTriggerable()).isEqualTo(isAutoTriggerableInGame);
    }

    @Test
    @DisplayName("")
    void getNextManualTriggerable() {
        // TODO: 17.09.2024
    }

    @Test
    @DisplayName("Should return false if there is no next manual triggerable to check, true otherwise")
    void hasNextManualTriggerable() {
        final boolean isManualTriggerableInGame = defaultGame.getTriggerables().stream()
                .anyMatch(ManualTriggerable.class::isInstance);

        assertThat(this.defaultPlayerTurn.hasNextManualTriggerable()).isEqualTo(isManualTriggerableInGame);
    }

    /**
     * This test verifies that the hasActionsToDo method correctly returns true
     * when there are actions left to be performed in the PlayerTurn instance.
     * It asserts that the defaultPlayerTurn instance, initialized with a number
     * of actions to do, properly indicates that there are actions remaining.
     *
     * @since 2024-09-18
     */
    @Test
    @DisplayName("Should return true if there are still actions to do")
    void hasActionsToDo() {
        assertThat(this.defaultPlayerTurn.hasActionsToDo()).isTrue();
    }

    /**
     * This test verifies that the hasActionsToDo method correctly returns false
     * when there are no actions left to be performed in the PlayerTurn instance.
     * It reinitializes the defaultPlayerTurn instance with zero actions to do
     * and asserts that the method indicates no actions remaining.
     *
     * @since 2024-09-18
     */
    @Test
    @DisplayName("Should return false if there are no more actions to do")
    void hasNoActionsToDo() {
        this.defaultPlayerTurn = createPlayerTurn(
                defaultGame,
                this.defaultPlayer,
                0,
                this.numberOfPlayerCardsToDraw,
                this.numberOfInfectionCardsToDraw
        );
        assertThat(this.defaultPlayerTurn.hasActionsToDo()).isFalse();
    }

    private static Stream<Arguments> executeCommandSource() {
        final Command command = mock(Command.class);
        final Action action = mock(Action.class);
        final DiscardCardsAction discardCardsAction = mock(DiscardCardsAction.class);

        final CityCard cityCard = mock();
        when(discardCardsAction.getDiscardedCards())
                .thenReturn(List.of(cityCard));

        return Stream.of(
                Arguments.of(command, 4),
                Arguments.of(action, 3),
                Arguments.of(discardCardsAction, 3)
        );
    }

    /**
     * This test verifies that the executeCommand method correctly invokes
     * the execute method on the provided Command instance.
     * It calls executeCommand on the defaultPlayerTurn with the mock command,
     * and then verifies that the command's execute method was called.
     *
     * @since 2024-09-18
     */
    @ParameterizedTest
    @MethodSource("executeCommandSource")
    @DisplayName("Should call the execute() method on the given command")
    void executeCommand(final Command executedCommand, final int numberOfExpectedActionsToDoAfter) {
        if (executedCommand instanceof DiscardCardsAction discardCardsAction) {
            discardCardsAction.getDiscardedCards().forEach(defaultPlayer::addHandCard);
            when(discardCardsAction.getExecutingPlayer())
                    .thenReturn(defaultPlayer);
        }

        defaultPlayerTurn.executeCommand(executedCommand);

        verify(executedCommand).execute();
        assertThat(defaultPlayerTurn.getNumberOfActionsToDo())
                .isEqualTo(numberOfExpectedActionsToDoAfter);

        if (executedCommand instanceof DiscardCardsAction) {
            assertThat(defaultPlayer.getHandCards())
                    .isEmpty();
            assertThat(defaultGame.getPlayerDiscardStack())
                    .isNotEmpty();
        }
    }

    @Test
    @DisplayName("")
    void testDrawPlayerCards() {
        // TODO: 18.09.2024  
    }

    /**
     * This test verifies that the playCarrier method correctly handles the case
     * when the carrier action has already been played.
     * It first calls playCarrier on the defaultPlayerTurn, then verifies that calling
     * playCarrier again throws an IllegalStateException with the expected message.
     *
     * @since 2024-09-18
     */
    @Test
    @DisplayName("Should throw exception if carrier has already been played")
    void playCarrier() {
        this.defaultPlayerTurn.playCarrier();
        Exception exception = assertThrows(IllegalStateException.class, defaultPlayerTurn::playCarrier);

        assertEquals("Carrier action has already been played.", exception.getMessage());
    }

    private static Stream<Arguments> isInActionPhaseSource() {
        return Stream.of(
                Arguments.of(false, true),
                Arguments.of(true, false)
        );
    }

    @ParameterizedTest
    @MethodSource("isInActionPhaseSource")
    @DisplayName("Should return true if there are actions to do")
    void isInActionPhase(final boolean hasWaived,
                         final boolean isInActionPhase) {
        evaluateActionPhase(hasWaived);

        assertThat(defaultPlayerTurn.isInActionPhase())
                .isEqualTo(isInActionPhase);
    }

    private static Stream<Arguments> isInPlayerCardDrawPhaseSource() {
        return Stream.of(
                Arguments.of(false, 0, false),
                Arguments.of(true, 0, true),
                Arguments.of(true, 1, true),
                Arguments.of(true, 2, false)
        );
    }

    @ParameterizedTest
    @MethodSource("isInPlayerCardDrawPhaseSource")
    @DisplayName("Should return true if all actions are done but not all required player cards are drawn")
    void isInPlayerCardDrawPhase(final boolean hasWaived,
                                 final int amountOfPlayerCardsDrawn,
                                 final boolean isInPlayerCardDrawPhase) {
        evaluateActionPhase(hasWaived);
        evaluatePlayerCardDrawPhase(amountOfPlayerCardsDrawn);

        assertThat(defaultPlayerTurn.isInPlayerCardDrawPhase())
                .isEqualTo(isInPlayerCardDrawPhase);
    }

    private static Stream<Arguments> isInInfectionCardDrawPhaseSource() {
        return Stream.of(
                Arguments.of(false, 0, 0, false),
                Arguments.of(true, 0, 0, false),
                Arguments.of(true, 2, 0, true),
                Arguments.of(true, 2, 1, false)
        );
    }

    @ParameterizedTest
    @MethodSource("isInInfectionCardDrawPhaseSource")
    @DisplayName("Should return true if all actions are done and all player cards are drawn but not all infection cards are drawn")
    void isInInfectionCardDrawPhase(final boolean hasWaived,
                                    final int amountOfPlayerCardsDrawn,
                                    final int amountOfInfectionCardsDrawn,
                                    final boolean isInInfectionCardDrawPhase) {
        evaluateActionPhase(hasWaived);
        evaluatePlayerCardDrawPhase(amountOfPlayerCardsDrawn);
        evaluateInfectionCardDrawPhase(amountOfInfectionCardsDrawn);

        assertThat(defaultPlayerTurn.isInInfectionCardDrawPhase())
                .isEqualTo(isInInfectionCardDrawPhase);
    }

    private static Stream<Arguments> isOverSource() {
        return Stream.of(
                Arguments.of(false, 0, 0, false),
                Arguments.of(true, 0, 0, false),
                Arguments.of(true, 2, 0, false),
                Arguments.of(true, 2, 1, true)
        );
    }

    @ParameterizedTest
    @MethodSource("isOverSource")
    @DisplayName("Should return true if there are no more actions to do and all player and infection cards are drawn")
    void isOver(final boolean hasWaived,
                final int amountOfPlayerCardsDrawn,
                final int amountOfInfectionCardsDrawn,
                final boolean isOver) {
        evaluateActionPhase(hasWaived);
        evaluatePlayerCardDrawPhase(amountOfPlayerCardsDrawn);
        evaluateInfectionCardDrawPhase(amountOfInfectionCardsDrawn);

        assertThat(defaultPlayerTurn.isOver())
                .isEqualTo(isOver);
    }

    private static Stream<Arguments> isActionExecutableSource() {
        return Stream.of(
                Arguments.of(true,false, false),
                Arguments.of(false,true,false),
                Arguments.of(true,true,false),
                Arguments.of(false,false,true)
        );
    }

    @ParameterizedTest
    @MethodSource("isActionExecutableSource")
    @DisplayName("Should return true if the action is executable")
    void evaluateActionExecutable(final boolean hasWaived,
            final boolean areInteractionsBlocked,
            final boolean areActionsExecutable
    ) {
        evaluateActionPhase(hasWaived);
        defaultPlayerTurn.setAreInteractionsBlocked(areInteractionsBlocked);

        assertThat(defaultPlayerTurn.isActionExecutable())
                .isEqualTo(areActionsExecutable);
    }

    private static Stream<Arguments> isPlayerCardDrawExecutableSource() {
        return Stream.of(
                Arguments.of(true, 0, false, true),
                Arguments.of(true, 0, true, false),
                Arguments.of(true, 1, false, true),
                Arguments.of(true, 1, true, false),
                Arguments.of(true, 2, false, false),
                Arguments.of(true, 2, true, false)
        );
    }

    @ParameterizedTest
    @MethodSource("isPlayerCardDrawExecutableSource")
    @DisplayName("Should return true if the player card draw is executable")
    void evaluatePlayerCardDrawExecutable(final boolean hasWaived,
                                          final int amountOfPlayerCardsDrawn,
                                          final boolean areInteractionsBlocked,
                                          final boolean isPlayerCardDrawExecutable) {
        evaluateActionPhase(hasWaived);
        evaluatePlayerCardDrawPhase(amountOfPlayerCardsDrawn);
        defaultPlayerTurn.setAreInteractionsBlocked(areInteractionsBlocked);

        assertThat(defaultPlayerTurn.isPlayerCardDrawExecutable())
                .isEqualTo(isPlayerCardDrawExecutable);
    }

    private static Stream<Arguments> isInfectionCardDrawExecutableSource() {
        return Stream.of(
                Arguments.of(true, 0, 0, false, false),
                Arguments.of(true, 0, 0, true, false),
                Arguments.of(true, 2, 0, false, true),
                Arguments.of(true, 2, 0, true, false),
                Arguments.of(true, 2, 1, false, false),
                Arguments.of(true, 2, 1, true, false)
        );
    }

    @ParameterizedTest
    @MethodSource("isInfectionCardDrawExecutableSource")
    @DisplayName("Should return true if the infection card draw is executable")
    void evaluateInfectionCardDrawExecutable(final boolean hasWaived,
                                             final int amountOfPlayerCardsDrawn,
                                             final int amountOfInfectionCardsDrawn,
                                             final boolean areInteractionsBlocked,
                                             final boolean isInfectionCardDrawExecutable) {
        evaluateActionPhase(hasWaived);
        evaluatePlayerCardDrawPhase(amountOfPlayerCardsDrawn);
        evaluateInfectionCardDrawPhase(amountOfInfectionCardsDrawn);
        defaultPlayerTurn.setAreInteractionsBlocked(areInteractionsBlocked);

        assertThat(defaultPlayerTurn.isInfectionCardDrawExecutable())
                .isEqualTo(isInfectionCardDrawExecutable);
    }

    private void evaluateActionPhase(final boolean hasWaived) {
        if (hasWaived) {
            final Action action = new WaiveAction();
            action.setExecutingPlayer(defaultPlayer);
            action.setGame(defaultGame);
            defaultPlayerTurn.executeCommand(action);
        }
    }

    private void evaluatePlayerCardDrawPhase(final int amountOfPlayerCardsDrawn) {
        for (int i = 0; i < amountOfPlayerCardsDrawn; i++) {
            defaultPlayerTurn.reduceNumberOfPlayerCardsToDraw();
        }
    }

    private void evaluateInfectionCardDrawPhase(final int amountOfInfectionCardsDrawn) {
        for (int i = 0; i < amountOfInfectionCardsDrawn; i++) {
            defaultPlayerTurn.reduceNumberOfInfectionCardsToDraw();
        }
    }
}
