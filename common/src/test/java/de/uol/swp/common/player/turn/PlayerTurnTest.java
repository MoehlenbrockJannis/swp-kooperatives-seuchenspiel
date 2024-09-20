package de.uol.swp.common.player.turn;

import de.uol.swp.common.action.Command;
import de.uol.swp.common.game.Game;

import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.UserPlayer;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
    private Player defaultPlayer;
    private Game defaultGame;
    private PlayerTurn defaultPlayerTurn;
    private int numberOfActionsToDo;
    private int numberOfPlayerCardsToDraw;
    private Command command;

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
        this.defaultPlayer = new UserPlayer(this.defaultUser);
        this.defaultGame = mock(Game.class);
        this.numberOfActionsToDo = 4;
        this.numberOfPlayerCardsToDraw = 2;

        this.defaultPlayerTurn = new PlayerTurn(
                defaultGame,
                this.defaultPlayer,
                this.numberOfActionsToDo,
                this.numberOfPlayerCardsToDraw
        );

        this.command = mock(Command.class);
    }


    @Test
    void getNextAutoTriggerable() {
        // TODO: 17.09.2024
    }

    
    @Test
    void hasNoNextAutoTriggerable() {
        assertThat(this.defaultPlayerTurn.hasNextAutoTriggerable()).isFalse();
    }

    @Test
    void getNextManualTriggerable() {
        // TODO: 17.09.2024
    }

    @Test
    void hasNoNextManualTriggerable() {
        assertThat(this.defaultPlayerTurn.hasNextManualTriggerable()).isFalse();
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
    void hasNoActionsToDo() {
        this.defaultPlayerTurn = new PlayerTurn(
                defaultGame,
                this.defaultPlayer,
                0,
                this.numberOfPlayerCardsToDraw
        );
        assertThat(this.defaultPlayerTurn.hasActionsToDo()).isFalse();
    }

    /**
     * This test verifies that the executeCommand method correctly invokes
     * the execute method on the provided Command instance.
     * It calls executeCommand on the defaultPlayerTurn with the mock command,
     * and then verifies that the command's execute method was called.
     *
     * @since 2024-09-18
     */
    @Test
    void testExecuteCommand() {
        defaultPlayerTurn.executeCommand(this.command);
        verify(command).execute();
    }

    @Test
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
    void playCarrier() {
        this.defaultPlayerTurn.playCarrier();
        Exception exception = assertThrows(IllegalStateException.class, defaultPlayerTurn::playCarrier);

        assertEquals("Carrier action has already been played.", exception.getMessage());
    }

    @Test
    void isNotOver() {
        assertThat(this.defaultPlayerTurn.isOver()).isFalse();
    }

    @Test
    void isOver() {
        this.defaultPlayerTurn = new PlayerTurn(
                defaultGame,
                this.defaultPlayer,
                0,
                this.numberOfPlayerCardsToDraw
        );
        assertThat(this.defaultPlayerTurn.isOver()).isTrue();
    }
}
