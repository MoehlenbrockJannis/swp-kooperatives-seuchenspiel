package de.uol.swp.common.player.turn;

import de.uol.swp.common.action.Action;
import de.uol.swp.common.action.Command;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.role.RoleAbility;
import de.uol.swp.common.triggerable.AutoTriggerable;
import de.uol.swp.common.triggerable.ManualTriggerable;

import java.util.ArrayList;
import java.util.List;

/**
 * The PlayerTurn class represents a player's turn in a game.
 * It manages the actions the player can perform and the triggerable actions
 * (either automatic or manual) that occur during the turn.
 */
public class PlayerTurn {

    private Game game;
    private Player player;
    private int numberOfActionsToDo;
    private int numberOfPlayerCardsToDraw;
    private boolean playedCarrier;
    private List<Action> possibleActions;
    private int currentAutoTriggerable;
    private List<AutoTriggerable> autoTriggerables;
    private int currentManualTriggerable;
    private List<ManualTriggerable> manualTriggerables;
    private List<Command> executedCommands;

    /**
     * Constructor for creating a new PlayerTurn instance.
     *
     * @param game the current game instance
     * @param player the player whose turn it is
     * @param numberOfActionsToDo the number of actions the player can perform
     * @param numberOfPlayerCardsToDraw the number of player cards to draw
     */
    public PlayerTurn(Game game, Player player, int numberOfActionsToDo, int numberOfPlayerCardsToDraw) {
        this.game = game;
        this.player = player;
        this.numberOfActionsToDo = numberOfActionsToDo;
        this.numberOfPlayerCardsToDraw = numberOfPlayerCardsToDraw;
        this.executedCommands = new ArrayList<>();
        this.possibleActions = new ArrayList<>();
        this.autoTriggerables = new ArrayList<>();
        this.manualTriggerables = new ArrayList<>();
    }

    /**
     * Creates a list of possible actions the player can perform during their turn.
     */
    private void createPossibleActions() {
        this.possibleActions.clear();

        if (this.hasActionsToDo()) {
            if (this.player.getRole() != null && this.player.getRole().getAbility() != null) {
                RoleAbility roleAbility = this.player.getRole().getAbility();
                // TODO: Actions müssen hier festgelegt werden
            }

            if (hasNextAutoTriggerable()) {
                // TODO: Auto-Trigger Actions
            }
            if (hasNextManualTriggerable()) {
                // TODO: Manual Trigger Actions
            }
        }
    }

    /**
     * Sets up the triggerable actions (automatic and manual) for the player's turn.
     */
    private void createTriggerables() {
        // TODO: 07.09.2024
    }

    /**
     * Reduces the number of actions the player has left to perform during their turn.
     */
    private void reduceNumberOfActionsToDo() {
        if (this.numberOfActionsToDo > 0) {
            numberOfActionsToDo --;
        }
    }

    /**
     * Checks if there are any remaining automatic triggerable actions.
     *
     * @return true if there are more automatic triggerables, false otherwise
     */
    public boolean hasNextAutoTriggerable() {
        return this.currentAutoTriggerable < this.autoTriggerables.size();
    }

    /**
     * Retrieves the next automatic triggerable action for the player.
     *
     * @return the next AutoTriggerable, or null if none are available
     */
    public AutoTriggerable getNextAutoTriggerable() {
        if (this.hasNextAutoTriggerable()) {
            return this.autoTriggerables.get(currentAutoTriggerable++);
        }
        return null;
    }

    /**
     * Checks if there are any remaining manual triggerable effects.
     *
     * @return true if there are more manual triggerables, false otherwise
     */
    public boolean hasNextManualTriggerable() {
        return this.currentManualTriggerable < this.manualTriggerables.size();
    }

    /**
     * Retrieves the next manual triggerable effect for the player.
     *
     * @return the next ManualTriggerable, or null if none are available
     */
    public ManualTriggerable getNextManualTriggerable() {
        if (hasNextManualTriggerable()) {
            return this.manualTriggerables.get(currentManualTriggerable++);
        }
        return null;
    }

    /**
     * Checks if the player still has actions to perform during their turn.
     *
     * @return true if there are remaining actions, false otherwise
     */
    public boolean hasActionsToDo() {
        return this.numberOfActionsToDo > 0;
    }

    /**
     * Executes a given command as part of the player's turn.
     *
     * @param command the command to be executed
     */
    public void executeCommand(Command command) {
        command.execute();
        this.executedCommands.add(command);
        reduceNumberOfActionsToDo();
    }

    /**
     * Draws the specified number of player cards for the player.
     */
    public void drawPlayerCards() {
        for (int i = 0; i < numberOfPlayerCardsToDraw; i++) {
            this.player.addHandCard(null);
        }
    }

    /**
     * Executes the carrier action, if applicable, during the player's turn.
     */
    public void playCarrier() {
        if (this.playedCarrier) {
            throw new IllegalStateException("Carrier action has already been played.");
        }
        // TODO: Actions müssen hier implementiert werden
        playedCarrier = true;
        reduceNumberOfActionsToDo();
    }

    /**
     * Checks if the player's turn is over.
     *
     * @return true if the turn is over, false otherwise
     */
    public boolean isOver() {
        return this.numberOfActionsToDo == 0 &&
                !this.hasNextAutoTriggerable() && !this.hasNextManualTriggerable();
    }
}
