package de.uol.swp.client.player;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.action.ActionService;
import de.uol.swp.client.game.CityMarker;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.common.action.Action;
import de.uol.swp.common.action.simple.MoveAction;
import de.uol.swp.common.action.simple.WaiveAction;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.user.User;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import lombok.AllArgsConstructor;

import java.util.*;

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
    private final Game game;
    private final List<CityMarker> cityMarkers;

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
            if (isEventTriggeredByBoundPlayer()) {
                playerMarker.setOpacity(0.5);
            }
        });

        playerMarker.setOnMouseExited(event -> {
            if (isEventTriggeredByBoundPlayer()) {
                playerMarker.setOpacity(1.0);
            }
        });
    }

    /**
     * Initializes the click event of the player marker
     */
    private void initializeClickEvent() {
        playerMarker.setOnMouseClicked(event -> {
            if (game.getCurrentPlayer().equals(playerMarker.getPlayer()) && isEventTriggeredByBoundPlayer()) {
                ContextMenu contextMenu = createContextMenu();
                contextMenu.show(playerMarker, event.getScreenX(), event.getScreenY());
            }
        });
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
        List<Action> possibleActions = game.getCurrentTurn().getPossibleActions();

        for (Action action : possibleActions) {
            if (action instanceof MoveAction || action instanceof WaiveAction) {
                MenuItem actionItem = new MenuItem(action.toString());
                actionItem.setOnAction(event -> prepareAction(action));
                contextMenu.getItems().add(actionItem);
            }
        }

        return contextMenu;
    }

    /**
     * Prepares the selected action for execution.
     * Highlights available fields and city markers.
     *
     * @param action the action to prepare
     * @author Jannis Moehlenbrock
     */
    private void prepareAction(Action action) {
        if (!isActionRelevant(action)) {
            return;
        }

        if (!(action instanceof MoveAction)) {
            return;
        }

        MoveAction moveAction = (MoveAction) action;
        prepareMoveAction(moveAction);
    }

    /**
     * Prepares the necessary steps for a MoveAction.
     *
     * @param moveAction the MoveAction to prepare.
     * @author Jannis Moehlenbrock
     */
    private void prepareMoveAction(MoveAction moveAction) {
        List<Field> availableFields = moveAction.getAvailableFields();
        Map<String, CityMarker> cityMarkerMap = createCityMarkerMap();
        List<CityMarker> localCityMarkers = getCityMarkers(cityMarkerMap, availableFields);
        setupCityMarkerActions(availableFields, cityMarkerMap, moveAction);
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
    private void setupCityMarkerActions(List<Field> availableFields, Map<String, CityMarker> cityMarkerMap, MoveAction moveAction) {
        for (Field field : availableFields) {
            CityMarker cityMarker = cityMarkerMap.get(field.getCity().getName());
            if (cityMarker != null) {
                cityMarker.setOnMouseClicked(event -> executeMoveActionOnCity(moveAction, field));
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
        actionService.sendAction(game, moveAction);

        unhighlightAllCityMarkers();
    }

    private void unhighlightAllCityMarkers() {
        for (CityMarker cityMarker : cityMarkers) {
            cityMarker.unhighlight();
            cityMarker.setOnMouseClicked(null);
        }
    }

    /**
     * Checks if the action is relevant to the current game.
     *
     * @param action the action to check
     * @return true if the action is relevant, false otherwise
     * @author Jannis Moehlenbrock
     */
    private boolean isActionRelevant(Action action) {
        return action.getGame().equals(game);
    }

    /**
     * Creates a map of city markers keyed by the city name for quick lookup.
     *
     * @return a Map where the keys are city names and the values are CityMarker objects
     * @author Jannis Moehlenbrock
     */
    private Map<String, CityMarker> createCityMarkerMap() {
        Map<String, CityMarker> cityMarkerMap = new HashMap<>();
        for (CityMarker cityMarker : cityMarkers) {
            cityMarkerMap.put(cityMarker.getField().getCity().getName(), cityMarker);
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

    private boolean isEventTriggeredByBoundPlayer() {
        User user = loggedInUserProvider.get();
        return Objects.equals(user.getUsername(), playerMarker.getPlayer().getName());
    }
}
