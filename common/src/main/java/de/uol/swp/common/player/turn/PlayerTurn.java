package de.uol.swp.common.player.turn;

import de.uol.swp.common.action.*;
import de.uol.swp.common.action.simple.MoveAllyAction;
import de.uol.swp.common.action.simple.MoveAllyToAllyAction;
import de.uol.swp.common.card.CityCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.triggerable.AutoTriggerable;
import de.uol.swp.common.triggerable.ManualTriggerable;
import de.uol.swp.common.util.Command;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

/**
 * The PlayerTurn class represents a player's turn in a game.
 * It manages the actions the player can perform and the triggerable actions
 * (either automatic or manual) that occur during the turn.
 */
public class PlayerTurn implements Serializable {
    private static final ActionFactory ACTION_FACTORY = new ActionFactory();

    private Game game;
    @Getter
    private Player player;
    @Getter
    @Setter
    private int numberOfActionsToDo;
    @Getter
    private int numberOfPlayerCardsToDraw;
    @Getter
    @Setter
    private int numberOfPlayerCardsToDiscard;
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
    @Getter
    private List<List<Field>> infectedFieldsInTurn = new ArrayList<>();

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
        return ACTION_FACTORY.createAllGeneralActionsExcludingSomeAndIncludingSomeRoleActions(
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
            if (action instanceof MoveAllyAction moveAllyAction) {
                determineAvailabilityOfMoveAllyAction(moveAllyAction);
            } else {
                determineAvailabilityOfAction(action);
            }
        }
    }

    /**
     * <p>
     *     Determines for a given {@link MoveAllyAction} if it is currently available to potentially be executed by {@link #player}.
     *     After duplicating the given {@link MoveAllyAction} for every {@link Player} in {@link #game} except {@link #player},
     *     its availability is determined by {@link #determineAvailabilityOfAction(Action)}
     * </p>
     *
     * @param action {@link MoveAllyAction} to check availability for
     * @see #duplicateMoveAllyActionForEveryPlayer(MoveAllyAction, List)
     * @see #getPlayersWithoutCurrentPlayer()
     * @see #determineAvailabilityOfAction(Action)
     */
    private void determineAvailabilityOfMoveAllyAction(final MoveAllyAction action) {
        final List<Player> movablePlayers = action instanceof MoveAllyToAllyAction ?
                game.getPlayersInTurnOrder() :
                getPlayersWithoutCurrentPlayer();
        final List<MoveAllyAction> moveAllyActions = duplicateMoveAllyActionForEveryPlayer(action, movablePlayers);
        for (final MoveAllyAction moveAllyAction : moveAllyActions) {
            determineAvailabilityOfAction(moveAllyAction);
        }
    }

    /**
     * <p>
     *     Returns a {@link List} of players from the {@link #game} without {@link #player}.
     * </p>
     *
     * @return a {@link List} of players from {@link #game} without {@link #player}
     * @see Game#getPlayersInTurnOrder()
     */
    private List<Player> getPlayersWithoutCurrentPlayer() {
        return this.game.getPlayersInTurnOrder().stream()
                .filter(playerFromGame -> !playerFromGame.equals(this.player))
                .toList();
    }

    /**
     * <p>
     *     Creates one copy of the given {@link MoveAllyAction} for every {@link Player} in {@code players} and calls {@link MoveAllyAction#setMovedAlly(Player)} with it.
     * </p>
     *
     * @param moveAllyAction {@link MoveAllyAction} to copy
     * @param players {@link List} of players to copy {@code moveAllyAction} for
     * @return {@link List} of copies of {@code moveAllyAction}
     * @implNote uses {@link LinkedList} as {@link List}
     */
    private List<MoveAllyAction> duplicateMoveAllyActionForEveryPlayer(final MoveAllyAction moveAllyAction, final List<Player> players) {
        final List<MoveAllyAction> moveAllyActions = new LinkedList<>();
        for (final Player p : players) {
            moveAllyActions.add(duplicateMoveAllyActionForPlayer(moveAllyAction, p));
        }
        return moveAllyActions;
    }

    /**
     * <p>
     *     Creates a copy of the given {@link MoveAllyAction} and calls {@link MoveAllyAction#setMovedAlly(Player)} with {@code player}.
     * </p>
     *
     * @param moveAllyAction the {@link MoveAllyAction} to copy
     * @param player {@link Player} to set as moved ally
     * @return a copy of the given {@link MoveAllyAction} with {@code player} as moved ally
     */
    private MoveAllyAction duplicateMoveAllyActionForPlayer(final MoveAllyAction moveAllyAction, final Player player) {
        final MoveAllyAction copy = ACTION_FACTORY.copyAction(moveAllyAction);
        copy.setMovedAlly(player);
        if (player.equals(this.player)) {
            copy.approve();
        }
        return copy;
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
     * Reduces {@link #numberOfPlayerCardsToDraw} by {@code 1}.
     */
    public void reduceNumberOfPlayerCardsToDraw() {
        this.numberOfPlayerCardsToDraw--;
    }

    /**
     * Reduces {@link #numberOfInfectionCardsToDraw} by {@code 1}.
     */
    public void reduceNumberOfInfectionCardsToDraw() {
        this.numberOfInfectionCardsToDraw--;
    }

    /**
     * Reduces {@link #numberOfPlayerCardsToDiscard} by {@code 1}.
     */
    public void reduceNumberOfPlayerCardsToDiscard() {
        this.numberOfPlayerCardsToDiscard--;
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
            fulfillDiscardCardsAction(discardCardsAction);
        } else {
            command.execute();
        }

        this.executedCommands.add(command);

        if (command instanceof Action) {
            reduceNumberOfActionsToDo();
        }

        if (isInActionPhase()) {
            createPossibleActions();
        }
    }
    /**
     * Fulfills the promise of a {@link DiscardCardsAction} by discarding the required card or cards after executing the action.
     *
     * @param discardCardsAction {@link DiscardCardsAction} to be executed and of which the cards are to be discarded
     * @see DiscardCardsAction#getDiscardedCards()
     */
    private void fulfillDiscardCardsAction(final DiscardCardsAction discardCardsAction) {
        final List<CityCard> cards = discardCardsAction.getDiscardedCards();
        discardCardsAction.execute();
        for (final CityCard card : cards) {
            discardCardsAction.getExecutingPlayer().removeHandCard(card);
            game.getPlayerDiscardStack().add(card);
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
    }

    /**
     * Returns {@code true} when {@link #numberOfActionsToDo} is greater than {@code 0}, {@code false} otherwise.
     *
     * @return {@code true} when {@link #numberOfActionsToDo} is greater than {@code 0}, {@code false} otherwise
     */
    public boolean isInActionPhase() {
        return hasActionsToDo();
    }

    /**
     * Returns {@code true} when {@link #isInActionPhase()} is {@code false} and {@link #numberOfPlayerCardsToDraw} is greater than {@code 0}, {@code false} otherwise.
     *
     * @return {@code true} when {@link #isInActionPhase()} is {@code false} and {@link #numberOfPlayerCardsToDraw} is greater than {@code 0}, {@code false} otherwise.
     */
    public boolean isInPlayerCardDrawPhase() {
        return !isInActionPhase() && numberOfPlayerCardsToDraw > 0;
    }

    /**
     * Returns {@code true} when {@link #isInPlayerCardDrawPhase()} is {@code false} and {@link #numberOfPlayerCardsToDiscard} is greater than {@code 0}, {@code false} otherwise.
     *
     * @return {@code true} when {@link #isInPlayerCardDrawPhase()} is {@code false} and {@link #numberOfPlayerCardsToDiscard} is greater than {@code 0}, {@code false} otherwise
     */
    public boolean isInPlayerCardDiscardPhase() {
        return !isInPlayerCardDrawPhase() && numberOfPlayerCardsToDiscard > 0;
    }

    /**
     * Returns {@code true} when {@link #isInPlayerCardDrawPhase()} is {@code false} and {@link #isInPlayerCardDiscardPhase()} is {@code false} and {@link #numberOfInfectionCardsToDraw} is greater than {@code 0}, {@code false} otherwise.
     *
     * @return {@code true} when {@link #isInPlayerCardDrawPhase()} is {@code false} and {@link #isInPlayerCardDiscardPhase()} is {@code false} and {@link #numberOfInfectionCardsToDraw} is greater than {@code 0}, {@code false} otherwise
     */
    public boolean isInInfectionCardDrawPhase() {
        return !isInActionPhase() && !isInPlayerCardDrawPhase() && !isInPlayerCardDiscardPhase() && numberOfInfectionCardsToDraw > 0;
    }

    /**
     * Checks if the player's turn is over.
     *
     * @return true if the turn is over, false otherwise
     */
    public boolean isOver() {
        return !isInActionPhase() && !isInPlayerCardDrawPhase() && !isInInfectionCardDrawPhase() &&
                !this.hasNextAutoTriggerable() && !this.hasNextManualTriggerable();
    }
}
