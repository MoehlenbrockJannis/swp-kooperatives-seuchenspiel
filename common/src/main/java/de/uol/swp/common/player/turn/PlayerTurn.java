package de.uol.swp.common.player.turn;

import de.uol.swp.common.action.*;
import de.uol.swp.common.card.CityCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.triggerable.AutoTriggerable;
import de.uol.swp.common.triggerable.ManualTriggerable;
import de.uol.swp.common.util.Command;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * The PlayerTurn class represents a player's turn in a game.
 * It manages the actions the player can perform and the triggerable actions
 * (either automatic or manual) that occur during the turn.
 */
public class PlayerTurn implements Serializable {

    private Game game;
    private Player player;
    private int numberOfActionsToDo;
    @Getter
    private int numberOfPlayerCardsToDraw;
    @Getter
    private int numberOfInfectionCardsToDraw;
    private boolean playedCarrier;
    @Getter
    private List<Action> possibleActions;
    private int currentAutoTriggerable;
    private List<AutoTriggerable> autoTriggerables;
    private int currentManualTriggerable;
    private List<ManualTriggerable> manualTriggerables;
    private List<Command> executedCommands;
    private transient ActionFactory actionFactory = new ActionFactory();

    /**
     * Constructor for creating a new PlayerTurn instance.
     *
     * @param game the current game instance
     * @param player the player whose turn it is
     * @param numberOfActionsToDo the number of actions the player can perform
     * @param numberOfPlayerCardsToDraw the number of player cards to draw
     */
    public PlayerTurn(Game game, Player player, int numberOfActionsToDo, int numberOfPlayerCardsToDraw, int numberOfInfectionCardsToDraw) {
        this.game = game;
        this.player = player;
        this.numberOfActionsToDo = numberOfActionsToDo;
        this.numberOfPlayerCardsToDraw = numberOfPlayerCardsToDraw;
        this.numberOfInfectionCardsToDraw = numberOfInfectionCardsToDraw;
        this.executedCommands = new ArrayList<>();
        this.possibleActions = new ArrayList<>();
        this.autoTriggerables = new ArrayList<>();
        this.manualTriggerables = new ArrayList<>();
        createPossibleActions();
    }

    /**
     * Creates a list of possible actions the player can perform during their turn.
     */
    private void createPossibleActions() {
        this.possibleActions.clear();

        if (this.hasActionsToDo()) {
            final List<Action> actions = createAllActions();
            determineAvailabilityOfActions(actions);
        }
    }

    /**
     * <p>
     *     Creates a {@link List} of all actions according to the role of the {@link #player}.
     *     Factors in role actions that are only available to that role and general actions that are no longer available to that role.
     * </p>
     *
     * <p>
     *     Creates actions by calling factory method: {@link ActionFactory#createAllGeneralActionsExcludingSomeAndIncludingSomeRoleActions(Collection, Collection)}
     * </p>
     *
     * @return {@link List} of all actions
     * @see Player#getRoleSpecificAdditionallyAvailableActionClasses()
     * @see Player#getRoleSpecificUnavailableActionClasses()
     * @see ActionFactory#createAllGeneralActionsExcludingSomeAndIncludingSomeRoleActions(Collection, Collection)
     */
    private List<Action> createAllActions() {
        final Set<Class<? extends RoleAction>> includeActionClasses = player.getRoleSpecificAdditionallyAvailableActionClasses();
        final Set<Class<? extends GeneralAction>> excludedActionClasses = player.getRoleSpecificUnavailableActionClasses();
        return actionFactory.createAllGeneralActionsExcludingSomeAndIncludingSomeRoleActions(
                excludedActionClasses,
                includeActionClasses
        );
    }

    /**
     * <p>
     *     Determines for a given {@link List} of actions which one of them is currently available to potentially be executed by {@link #player}.
     * </p>
     *
     * @param actions {@link List} of actions to check availability for
     * @see #determineAvailabilityOfAction(Action)
     */
    private void determineAvailabilityOfActions(final List<Action> actions) {
        for (final Action action : actions) {
            determineAvailabilityOfAction(action);
        }
    }

    /**
     * <p>
     *     Determines for a given {@link Action} if it is currently available to potentially be executed by {@link #player}.
     *     If it is available, it gets added to {@link #possibleActions}.
     * </p>
     *
     * <p>
     *     To be able to check availability for the given {@link Action},
     *     some methods are called first (
     *      {@link Action#setExecutingPlayer(Player)},
     *      {@link Action#setGame(Game)}
     *     ).
     *     After that, the {@link Action#isAvailable()} method is called
     *     so that the given {@link Action} can determine itself
     *     whether or not the current game state allows for it.
     * </p>
     *
     * @param action {@link Action} to check availability for
     * @see Action#setExecutingPlayer(Player)
     * @see Action#setGame(Game)
     * @see Action#isAvailable()
     */
    private void determineAvailabilityOfAction(final Action action) {
        action.setExecutingPlayer(player);
        action.setGame(game);
        if (action.isAvailable()) {
            this.possibleActions.add(action);
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
        if (command instanceof DiscardCardsAction discardCardsAction) {
            final List<CityCard> cards = discardCardsAction.getDiscardedCards();
            for (final CityCard card : cards) {
                game.getPlayerDiscardStack().add(card);
            }
        }
        command.execute();
        this.executedCommands.add(command);
        if (command instanceof Action) {
            reduceNumberOfActionsToDo();
        }
        createPossibleActions();
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
