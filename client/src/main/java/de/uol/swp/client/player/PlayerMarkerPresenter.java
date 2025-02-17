package de.uol.swp.client.player;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.action.ActionService;
import de.uol.swp.client.approvable.ApprovableService;
import de.uol.swp.client.game.CityMarker;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.common.action.simple.MoveAction;
import de.uol.swp.common.action.simple.MoveAllyAction;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.turn.PlayerTurn;
import de.uol.swp.common.user.User;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import lombok.AllArgsConstructor;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Presents the player marker on the game map.
 * Contains all functions for the player marker.
 *
 * @author Jannis Moehlenbrock
 * @see PlayerMarker
 * @since 2024-11-13
 */


@AllArgsConstructor
public class PlayerMarkerPresenter extends AbstractPresenter {

    private final PlayerMarker playerMarker;
    private final LoggedInUserProvider loggedInUserProvider;
    private final ActionService actionService;
    private final ApprovableService approvableService;
    private final Game game;
    private final Map<Field, CityMarker> cityMarkers;
    private final Runnable unhighlightCityMarkers;

    /**
     * Initializes the mouse events of the player marker
     */
    public void initializeMouseEvents() {
        initializeHoverEvents();
        initializeClickEvent();
    }

    /**
     * Initializes the click event of the player marker
     */
    private void initializeHoverEvents() {
        playerMarker.setOnMouseEntered(event -> {
            if (isAssociatedPlayerMovableByLoggedInUser()) {
                playerMarker.setOpacity(0.5);
            }
        });

        playerMarker.setOnMouseExited(event -> {
            if (isAssociatedPlayerMovableByLoggedInUser()) {
                playerMarker.setOpacity(1.0);
            }
        });
    }

    /**
     * Initializes the click event of the player marker
     */
    private void initializeClickEvent() {
        playerMarker.setOnMouseClicked(event -> {
            if (!isAssociatedPlayerMovableByLoggedInUser()) {
                return;
            }

            final ContextMenu contextMenu = createContextMenu();
            contextMenu.show(playerMarker, event.getScreenX(), event.getScreenY());
        });
    }

    /**
     * Returns whether the {@link Player} stored on the {@link #playerMarker} is movable by the {@link Player} of the logged-in user.
     *
     * @return {@code true} if the {@link Player} of the logged-in user can move the {@link Player} of {@link #playerMarker}, {@code false} otherwise
     */
    private boolean isAssociatedPlayerMovableByLoggedInUser() {
        final User loggedInUser = loggedInUserProvider.get();
        final Optional<Player> playerOfLoggedInUserOptional = findPlayerForUserInGame(loggedInUser, game);
        return playerOfLoggedInUserOptional.filter(this::isAssociatedPlayerMovableByPlayer).isPresent();
    }

    /**
     * Returns an {@link Optional} that may contain the {@link Player} associated with the given {@code user} on the given {@code game}.
     *
     * @param user {@link User} to find the {@link Player} on {@code game} for
     * @param game {@link Game} on which to search the {@link Player} of {@code user}
     * @return an {@link Optional} containing the associated {@link Player} of {@code user} on {@code game} or an empty {@link Optional} if there is no such {@link Player}
     */
    private Optional<Player> findPlayerForUserInGame(final User user, final Game game) {
        return game.getPlayersInTurnOrder().stream()
                .filter(player -> player.containsUser(user))
                .findFirst();
    }

    /**
     * <p>
     *     Determines whether the given {@code player} can move the {@link Player} stored on {@link #playerMarker}.
     *     For that to be {@code true},
     *      the {@link #game}'s current {@link PlayerTurn} needs to be in its action phase,
     *      its current {@link Player} needs to be the given {@code player} and
     *      there needs to be at least one {@link MoveAction} targeting the {@link Player} of {@link #playerMarker}.
     * </p>
     *
     * @param player the {@link Player} to check active moving status for
     * @return {@code true} if the given {@code player} can move the associated {@link Player}, {@code false} otherwise
     */
    private boolean isAssociatedPlayerMovableByPlayer(final Player player) {
        return game.getCurrentTurn().isActionExecutable() && game.getCurrentPlayer().equals(player) &&
                doesMoveActionWithAssociatedPlayerAsMovedPlayerExistOnCurrentTurn();
    }

    /**
     * Determines whether a {@link MoveAction} exists on the {@link #game}'s current {@link PlayerTurn}
     * that targets the {@link Player} of {@link #playerMarker}.
     *
     * @return {@code true} if there is at least one available {@link MoveAction} for the associated {@link Player}, {@code false} otherwise
     */
    private boolean doesMoveActionWithAssociatedPlayerAsMovedPlayerExistOnCurrentTurn() {
        return !getAvailableMoveActions().isEmpty();
    }

    /**
     * Returns a {@link List} of available {@link MoveAction} from the {@link #game}'s current {@link PlayerTurn}
     * that targets the {@link Player} of {@link #playerMarker}.
     *
     * @return {@link List} of {@link MoveAction} with {@link PlayerMarker#getPlayer()} as moved {@link Player}
     */
    private List<MoveAction> getAvailableMoveActions() {
        return game.getCurrentTurn().getPossibleActions().stream()
                .filter(MoveAction.class::isInstance)
                .map(MoveAction.class::cast)
                .filter(action -> action.getMovedPlayer().equals(playerMarker.getPlayer()))
                .toList();
    }

    /**
     * Creates a context menu with possible actions for the current turn.
     * Each action is represented as a menu item that, when selected, highlights the fields to which the player can move.
     *
     * @return a ContextMenu containing the possible actions for the current turn
     * @author Jannis Moehlenbrock
     */
    private ContextMenu createContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        final List<MoveAction> possibleActions = getAvailableMoveActions();

        for (final MoveAction possibleAction : possibleActions) {
            MenuItem actionItem = new MenuItem(possibleAction.toString());
            actionItem.setOnAction(event -> moveActionClicked(possibleAction));
            contextMenu.getItems().add(actionItem);
        }

        return contextMenu;
    }

    /**
     * Unhighlights all city markers and prepares the given {@link MoveAction}.
     *
     * @param moveAction {@link MoveAction} to prepare
     * @see #unhighlightAllCityMarkers()
     * @see #prepareMap(List, Consumer)
     */
    private void moveActionClicked(final MoveAction moveAction) {
        unhighlightAllCityMarkers();
        prepareMap(moveAction.getAvailableFields(), field -> executeMoveActionOnCity(moveAction, field));
    }

    /**
     * Prepares the field with highlighting and click listeners
     *
     * @param availableFields {@link List} of {@link Field} to highlight
     * @param fieldConsumer {@link Consumer} invoked when {@link Field} is clicked
     * @author Jannis Moehlenbrock
     */
    private void prepareMap(final List<Field> availableFields, final Consumer<Field> fieldConsumer) {
        Map<String, CityMarker> cityMarkerMap = createCityMarkerMap();
        List<CityMarker> localCityMarkers = getCityMarkers(cityMarkerMap, availableFields);
        setupCityMarkerActions(availableFields, cityMarkerMap, fieldConsumer);
        highlightCityMarkers(localCityMarkers);
    }

    /**
     * Retrieves a list of CityMarkers based on the given available fields.
     *
     * @param cityMarkerMap a map of city names to CityMarkers.
     * @param availableFields a list of available fields in the MoveAction.
     * @return a list of CityMarkers corresponding to the available fields.
     * @author Jannis Moehlenbrock
     */
    private List<CityMarker> getCityMarkers(Map<String, CityMarker> cityMarkerMap, List<Field> availableFields) {
        List<CityMarker> localCityMarkers = new ArrayList<>();
        for (Field field : availableFields) {
            String cityName = field.getCity().getName();
            CityMarker cityMarker = cityMarkerMap.get(cityName);
            if (cityMarker != null) {
                localCityMarkers.add(cityMarker);
            }
        }
        return localCityMarkers;
    }

    /**
     * Sets up click actions on the given list of CityMarkers.
     *
     * @param availableFields a list of available fields in the MoveAction.
     * @param cityMarkerMap a map of city names to CityMarkers.
     * @author Jannis Moehlenbrock
     */
    private void setupCityMarkerActions(List<Field> availableFields, Map<String, CityMarker> cityMarkerMap, Consumer<Field> fieldConsumer) {
        for (Field field : availableFields) {
            CityMarker cityMarker = cityMarkerMap.get(field.getCity().getName());
            if (cityMarker != null) {
                cityMarker.setOnMouseClicked(event -> fieldConsumer.accept(field));
            }
        }
    }

    /**
     * Executes the prepared action for the selected city.
     *
     * @param targetField the target field for the action
     * @author Jannis Moehlenbrock
     */
    private void executeMoveActionOnCity(MoveAction moveAction, Field targetField) {
        moveAction.setTargetField(targetField);

        if (moveAction instanceof MoveAllyAction approvable && !approvable.isApproved()) {
            approvableService.sendApprovableAction(approvable);
        } else {
            actionService.sendAction(game, moveAction);
        }

        unhighlightAllCityMarkers();
    }

    private void unhighlightAllCityMarkers() {
        unhighlightCityMarkers.run();
    }

    /**
     * Creates a map of city markers keyed by the city name for quick lookup.
     *
     * @return a Map where the keys are city names and the values are CityMarker objects
     * @author Jannis Moehlenbrock
     */
    private Map<String, CityMarker> createCityMarkerMap() {
        Map<String, CityMarker> cityMarkerMap = new HashMap<>();
        for (Map.Entry<Field, CityMarker> entry : cityMarkers.entrySet()) {
            cityMarkerMap.put(entry.getValue().getField().getCity().getName(), entry.getValue());
        }
        return cityMarkerMap;
    }

    /**
     * Highlights the given list of city markers by invoking their highlight method.
     *
     * @param cityMarkers the list of CityMarker objects to be highlighted
     */
    private void highlightCityMarkers(List<CityMarker> cityMarkers) {
        for (CityMarker cityMarker : cityMarkers) {
            cityMarker.highlight();
        }
    }

    /**
     * Sets a click listener to {@link #playerMarker} and highlights available fields and invokes given {@link Consumer} on click.
     *
     * @param availableFields {@link List} of {@link Field} to highlight
     * @param fieldAndPlayerSelectionConsumer {@link BiConsumer} invoked when {@link #playerMarker} and a {@link Field} are clicked
     */
    public void setClickListenerForPlayerMarkerAndFields(final List<Field> availableFields, final BiConsumer<Field, Player> fieldAndPlayerSelectionConsumer) {
        playerMarker.setOnMouseClicked(event -> {
            prepareMap(availableFields, field -> fieldAndPlayerSelectionConsumer.accept(field, playerMarker.getPlayer()));
        });
    }

    /**
     * Highlights the {@link #playerMarker}
     */
    public void highlight() {
        this.playerMarker.highlight();
    }

    /**
     * Unhighlights the {@link #playerMarker}
     */
    public void unhighlight() {
        this.playerMarker.unhighlight();
    }
}
