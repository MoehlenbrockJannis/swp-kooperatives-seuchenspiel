package de.uol.swp.client.game;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.action.ActionService;
import de.uol.swp.client.approvable.ApprovableService;
import de.uol.swp.client.plague.PlagueCubeMarker;
import de.uol.swp.client.plague.PlagueCubeMarkerPresenter;
import de.uol.swp.client.player.PlayerMarker;
import de.uol.swp.client.player.PlayerMarkerPresenter;
import de.uol.swp.client.research_laboratory.ResearchLaboratoryMarker;
import de.uol.swp.client.research_laboratory.ResearchLaboratoryMarkerPresenter;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.client.util.ColorService;
import de.uol.swp.client.util.NodeBindingUtils;
import de.uol.swp.common.action.Action;
import de.uol.swp.common.action.advanced.build_research_laboratory.BuildResearchLaboratoryAction;
import de.uol.swp.common.action.advanced.build_research_laboratory.ReducedCostBuildResearchLaboratoryAction;
import de.uol.swp.common.action.simple.WaiveAction;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.server_message.RetrieveUpdatedGameServerMessage;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.role.RoleCard;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Manages the window representing the game map
 *
 * @author David Scheffler & Jannis Moehlenbrock
 * @see AbstractPresenter
 * @since 2024-09-09
 */

public class GameMapPresenter extends AbstractPresenter {

    private static final Logger LOG = LogManager.getLogger(GameMapPresenter.class);
    @Getter
    private static final int SVG_VIEW_BOX_MIN_X = 0;
    @Getter
    private static final int SVG_VIEW_BOX_MIN_Y = 0;
    @Getter
    private static final int SVG_VIEW_BOX_MAX_X = 2097;
    @Getter
    private static final int SVG_VIEW_BOX_MAX_Y = 1075;
    private static final double ASPECT_RATIO = (double) SVG_VIEW_BOX_MAX_X / SVG_VIEW_BOX_MAX_Y;

    private Game game;

    @FXML
    private Pane webViewPane;
    @FXML
    private WebView webView;
    @FXML
    private Pane cityConnectionPane;
    @FXML
    private Pane cityNamePane;
    @FXML
    private Pane cityMarkerPane;
    @FXML
    private Pane playerMarkerPane;
    @FXML
    private Pane plagueCubeMarkerPane;
    @FXML
    private Pane researchLaboratoryPane;

    @Inject
    private LoggedInUserProvider loggedInUserProvider;
    @Inject
    private ActionService actionService;
    @Inject
    private ApprovableService approvableService;

    private CityConnectionPanePresenter cityConnectionPanePresenter;
    private CityNamePanePresenter cityNamePanePresenter;
    private final Map<Field, CityMarker> cityMarkers = new HashMap<>();
    private final Map<Player, PlayerMarkerPresenter> playerMarkerPresenters = new HashMap<>();
    private final List<PlagueCubeMarkerPresenter> plagueCubeMarkerPresenters = new ArrayList<>();

    private static final double PLAYER_MARKER_SCALE_FACTOR = 1.75;
    @Getter
    private static final double CITY_MARKER_SCALE_FACTOR = 2.25;
    private static final double PLAGUE_CUBE_MARKER_SCALE_FACTOR = 1.75;
    private static final double RESEARCH_LABORATORY_MARKER_SCALE_FACTOR = 1.75;

    @Override
    public String getFXMLFolderPath() {
        return DEFAULT_FXML_FOLDER_PATH + GamePresenter.GAME_FXML_FOLDER_PATH;
    }

    /**
     * Initializes the game map with the given game data.
     *
     * @param game the game data to initialize the map with
     */
    @FXML
    public void initialize(Game game) {
        this.game = game;
        this.webView.setContextMenuEnabled(false);
        setPanePickOnBoundsToFalse();

        cityConnectionPanePresenter = new CityConnectionPanePresenter(this.game, this.webView, this.cityConnectionPane);
        cityNamePanePresenter = new CityNamePanePresenter(this.game, this.webView, this.cityNamePane);

        bindSizePropertyOfWebView();
        loadSvgIntoWebView();
        addAllCityMarkers();
        addAllPlagueCubeMarkers();
        addAllPlayerMarkers();

        addResearchLaboratoryMarkers(this.game);
    }

    /**
     * Sets the pick-on-bounds property of the {@link Pane}s within the {@link GameMapPresenter} to false.
     * This ensures that the {@link Pane}s do not intercept mouse events based on their bounds.
     */
    private void setPanePickOnBoundsToFalse() {
        this.cityConnectionPane.setPickOnBounds(false);
        this.cityNamePane.setPickOnBounds(false);
        this.cityMarkerPane.setPickOnBounds(false);
        this.playerMarkerPane.setPickOnBounds(false);
        this.plagueCubeMarkerPane.setPickOnBounds(false);
        this.researchLaboratoryPane.setPickOnBounds(false);
    }

    /**
     * Handles game updates from the server, updating the game state as necessary.
     *
     * @param retrieveUpdatedGameServerMessage the message containing the updated game state from the server
     * @author Jannis Moehlenbrock
     */
    @Subscribe
    public void onRetrieveUpdatedGameServerMessage(RetrieveUpdatedGameServerMessage retrieveUpdatedGameServerMessage) {
        if (this.game.getId() == retrieveUpdatedGameServerMessage.getGame().getId()) {
            Platform.runLater(() -> {
                removeResearchLaboratoryMarkers();

                addNewPlagueCubeMarker(retrieveUpdatedGameServerMessage.getGame());

                movePlayerMarker(retrieveUpdatedGameServerMessage.getGame());

                addResearchLaboratoryMarkers(retrieveUpdatedGameServerMessage.getGame());

                unhighlightCityMarkers();
            });
        }
    }

    /**
     * Adds research laboratory markers for fields with research laboratories.
     *
     * @param game The current game state
     */
    private void addResearchLaboratoryMarkers(Game game) {
        for (Field field : game.getFields()) {
            if (field.hasResearchLaboratory()) {
                createResearchLaboratoryMarker(field);
            }
        }
    }

    /**
     * Creates and initializes a research laboratory marker for a specific field.
     *
     * @param field The field containing the research laboratory
     */
    private void createResearchLaboratoryMarker(Field field) {
        ResearchLaboratoryMarker researchLaboratoryMarker = new ResearchLaboratoryMarker(0.7);
        buildResearchLaboratoryMarker(researchLaboratoryMarker, field);
        ResearchLaboratoryMarkerPresenter researchLaboratoryMarkerPresenter = new ResearchLaboratoryMarkerPresenter(researchLaboratoryMarker, game, actionService, field);
        researchLaboratoryMarkerPresenter.initializeMouseEvents();
    }

    /**
     * Scales and adds the research laboratory marker to the pane
     * @param researchLaboratoryMarker
     * @param field
     */
    public void buildResearchLaboratoryMarker(ResearchLaboratoryMarker researchLaboratoryMarker, Field field) {
        researchLaboratoryPane.getChildren().add(researchLaboratoryMarker);

        double xOffset = 2.5 * CityMarker.getRADIUS() / SVG_VIEW_BOX_MAX_X;
        double yOffset = 3.5 * CityMarker.getRADIUS() / SVG_VIEW_BOX_MAX_X;
        double xCoordinate = (double) field.getXCoordinate() / SVG_VIEW_BOX_MAX_X + xOffset;
        double yCoordinate = (double) field.getYCoordinate() / SVG_VIEW_BOX_MAX_Y + yOffset;
        double xScaleFactor = RESEARCH_LABORATORY_MARKER_SCALE_FACTOR / SVG_VIEW_BOX_MAX_X;
        double yxScaleFactor = RESEARCH_LABORATORY_MARKER_SCALE_FACTOR / SVG_VIEW_BOX_MAX_Y;
        NodeBindingUtils.bindWebViewSizeAndPositionToNode(webView, researchLaboratoryMarker, xCoordinate, yCoordinate, xScaleFactor, yxScaleFactor);
        game.setResearchLaboratoryButtonClicked(false);
    }

    /**
     * Removes all existing labs from the pane.
     */
    private void removeResearchLaboratoryMarkers() {
        researchLaboratoryPane.getChildren().removeIf(node -> node instanceof ResearchLaboratoryMarker);
    }

    /**
     * Starts pulse animations for all research lab markers and adds click interactions.
     *
     */
    public void requireMoveResearchLaboratory() {
        for (Node node : researchLaboratoryPane.getChildren()) {
            if (node instanceof ResearchLaboratoryMarker researchLaboratoryMarker) {
                Timeline pulseTimeline = createPulseAnimation(researchLaboratoryMarker);
                pulseTimeline.stop();
                pulseTimeline.play();

                game.setResearchLaboratoryButtonClicked(true);
            }
        }
    }

    /**
     * Creates a pulsating animation for a ResearchLaboratoryMarker
     *
     * @param marker The marker to animate
     * @return Timeline for the pulsation animation
     */
    private Timeline createPulseAnimation(ResearchLaboratoryMarker marker) {
        Scale animationScale = new Scale(1.0, 1.0);
        marker.getTransforms().removeIf(transform -> transform instanceof Scale);
        marker.getTransforms().add(animationScale);

        Timeline pulseTimeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(animationScale.xProperty(), 1.0),
                        new KeyValue(animationScale.yProperty(), 1.0)
                ),
                new KeyFrame(Duration.seconds(0.5),
                        new KeyValue(animationScale.xProperty(), 1.2),
                        new KeyValue(animationScale.yProperty(), 1.2)
                ),
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(animationScale.xProperty(), 1.0),
                        new KeyValue(animationScale.yProperty(), 1.0)
                )
        );

        pulseTimeline.setCycleCount(2);
        pulseTimeline.setAutoReverse(true);

        return pulseTimeline;
    }

    /**
     * Adds the new plague cube markers on the game map.
     *
     * @param game the game to update the plague cube markers for
     */
    private void addNewPlagueCubeMarker(Game game) {
        List<List<Field>> infectedFieldsInTurn = game.getCurrentTurn().getInfectedFieldsInTurn();
        if (!infectedFieldsInTurn.isEmpty()) {
            List<Field> lastInfectedFields = infectedFieldsInTurn.get(infectedFieldsInTurn.size() - 1);
            for (int i = 0; i < lastInfectedFields.size(); i++) {
                addAnimatedPlagueCubeMarker(lastInfectedFields.get(i), i);
            }
        }
    }

    /**
     * Removes all existing player markers and create alle existing player markers after that
     * @param game
     */
    private void movePlayerMarker(Game game) {
        this.game = game;
        playerMarkerPane.getChildren().removeIf(PlayerMarker.class::isInstance);
        addAllPlayerMarkers();
    }

    /**
     * Adds all players of the game to the pane as PlayerMarkers
     *
     * @see PlayerMarker
     * @since 2024-09-28
     */
    private void addAllPlayerMarkers() {
        playerMarkerPresenters.clear();
        List<Player> playersInTurnOrder = this.game.getPlayersInTurnOrder();
        for (Player player : playersInTurnOrder) {
            addPlayerMarker(player);
        }
    }

    /**
     * Adds a playerMarker for the given player to the pane
     *
     * @param player The player for which a playerMarker should be added
     * @see PlayerMarker
     * @since 2024-10-04
     */
    private void addPlayerMarker(Player player) {
        PlayerMarker newPlayerMarker = createNewPlayerMarker(player);
        PlayerMarkerPresenter playerMarkerPresenter = new PlayerMarkerPresenter(
                newPlayerMarker,
                loggedInUserProvider,
                actionService,
                approvableService,
                game,
                cityMarkers,
                this::unhighlightCityMarkers
        );
        playerMarkerPresenter.initializeMouseEvents();

        playerMarkerPane.getChildren().add(newPlayerMarker);

        Field currentField = player.getCurrentField();
        int playerOnFieldIndex = currentField.getPlayersOnField().indexOf(player);

        bindPlayerMarkerToField(newPlayerMarker, currentField, playerOnFieldIndex);
        playerMarkerPresenters.put(player, playerMarkerPresenter);
    }

    /**
     * Binds the position of the playerMarker to a field
     *
     * @param playerMarker       The playerMarker to be bound
     * @param field              The field to which the playerMarker should be bound
     * @param playerOnFieldIndex The index of the player on the field
     * @see PlayerMarker
     */
    private void bindPlayerMarkerToField(PlayerMarker playerMarker, Field field, int playerOnFieldIndex) {
        double xOffset = calculatePlayerXOffset(playerOnFieldIndex, getPlayerAmountOnField(field), playerMarker.getWidth());
        double yOffset = playerMarker.getHeight() / 2 * PLAYER_MARKER_SCALE_FACTOR;

        bindLayoutProperties(playerMarker, field, xOffset, yOffset);
    }


    /**
     * Binds the layout properties of the playerMarker
     *
     * @param playerMarker The playerMarker for which the properties should be bound
     * @param field The field to which the playerMarker should be bound
     * @param xOffset The x offset of the playerMarker
     * @param yOffset The y offset of the playerMarker
     * @since 2024-10-04
     */
    private void bindLayoutProperties(PlayerMarker playerMarker, Field field, double xOffset, double yOffset) {
        double xCoordinate = (field.getXCoordinate() + xOffset) / SVG_VIEW_BOX_MAX_X;
        double yCoordinate = (field.getYCoordinate() - yOffset) / SVG_VIEW_BOX_MAX_Y;
        double xScaleFactor = PLAYER_MARKER_SCALE_FACTOR / SVG_VIEW_BOX_MAX_X;
        double yxScaleFactor = PLAYER_MARKER_SCALE_FACTOR / SVG_VIEW_BOX_MAX_Y;
        NodeBindingUtils.bindWebViewSizeAndPositionToNode(webView, playerMarker, xCoordinate, yCoordinate, xScaleFactor, yxScaleFactor);
    }


    /**
     * Calculates the x offset of the playerMarker
     *
     * @param playerOnFieldIndex The index of the player on the field
     * @param playerMarkerWidth  The width of the playerMarker
     * @return The x offset of the playerMarker
     * @since 2024-10-04
     */
    private double calculatePlayerXOffset(int playerOnFieldIndex, int playerAmountOnField,  double playerMarkerWidth) {
        return (playerOnFieldIndex - (playerAmountOnField - 1) / 2.0) * playerMarkerWidth * PLAYER_MARKER_SCALE_FACTOR;
    }

    /**
     * Returns the amount of players on the field
     *
     * @param field The field for which the amount of players should be returned
     * @return The amount of players on the field
     */
    private int getPlayerAmountOnField(Field field) {
        return field.getPlayersOnField().size();
    }

    /**
     * Creates a new PlayerMarker for the given player
     *
     * @param player The player for which a new PlayerMarker should be created
     * @return The new PlayerMarker
     * @see PlayerMarker
     */
    public PlayerMarker createNewPlayerMarker(Player player) {
        RoleCard playerRole = player.getRole();
        Color playerColor = ColorService.convertColorToJavaFXColor(playerRole.getColor());
        double playerSize = 0.75;
        return new PlayerMarker(playerColor, playerSize, loggedInUserProvider, player, game);
    }

    /**
     * Binds the width and height of the webView to the pane
     *
     * @author David Scheffler
     * @since 2024-09-09
     */
    private void bindSizePropertyOfWebView() {
        webViewPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            double newWidth = newVal.doubleValue();
            double newHeight = newWidth / ASPECT_RATIO;

            if (newHeight > webViewPane.getHeight()) {
                newHeight = webViewPane.getHeight();
                newWidth = newHeight * ASPECT_RATIO;
            }

            webView.setPrefSize(newWidth, newHeight);
        });

        webViewPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            double newHeight = newVal.doubleValue();
            double newWidth = newHeight * ASPECT_RATIO;

            if (newWidth > webViewPane.getWidth()) {
                newWidth = webViewPane.getWidth();
                newHeight = newWidth / ASPECT_RATIO;
            }

            webView.setPrefSize(newWidth, newHeight);
        });
    }

    /**
     * Loads svg file of the game map into the webView
     *
     * @author David Scheffler
     * @since 2024-09-09
     */
    private void loadSvgIntoWebView() {
        WebEngine webEngine = webView.getEngine();
        try {
            String svgContent = new String(Files.readAllBytes(Paths.get("client/src/main/resources/images/gameboard-world-map.svg")));
            String htmlContent = "<html><head><style>"
                    + "html, body { background-color: #a6d1f1; margin: 0; padding: 0; width: 100%; height: 100%; "
                    + "overflow: hidden; display: flex; align-items: center; justify-content: center; }"
                    + "svg { width: 100%; height: 100%; object-fit: contain; }"
                    + "</style></head><body>"
                    + svgContent
                    + "</body></html>";
            webEngine.loadContent(htmlContent, "text/html");
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    /**
     * Creates {@link CityMarker} for each field in the game and adds them to the {@link #cityMarkerPane}.
     *
     * @author David Scheffler
     * @see CityMarker
     * @since 2024-09-10
     */
    private void addAllCityMarkers() {
        for (Field field : game.getFields()) {
            CityMarker cityMarker = new CityMarker(field);
            cityMarkerPane.getChildren().add(cityMarker);
            cityMarkers.put(field, cityMarker);

            handleWebViewSizeAndPosition(webView, cityMarker, field, CITY_MARKER_SCALE_FACTOR);
        }
    }

    /**
     * Unhighlights all city markers in {@link #cityMarkers} and removes action listeners from them.
     */
    private void unhighlightCityMarkers() {
        for (final Map.Entry<Field, CityMarker> entry : cityMarkers.entrySet()) {
            final CityMarker cityMarker = entry.getValue();
            cityMarker.unhighlight();
            cityMarker.setOnMouseClicked(null);
        }
    }

    /**
     * Creates {@link PlagueCubeMarker} for each field in the game and adds them to the {@link #plagueCubeMarkerPane}.
     *
     * @see PlagueCubeMarker
     * @since 2024-11-09
     */
    private void addAllPlagueCubeMarkers() {
        for (Field field : game.getFields()) {
            createPlagueCubeMarker(field);
        }
    }

    /**
     * Creates animated plagueCubeMarker for the associated plague of a given field and adds them to the pane with delay.
     *
     * @param field field to create PlagueCubeMarker for
     * @param delay the delay of the animation
     * @see PlagueCubeMarker
     * @since 2024-11-09
     */
    private void addAnimatedPlagueCubeMarker(Field field, int delay) {
        PlagueCubeMarker plagueCubeMarker = createPlagueCubeMarker(field);

        animatePlagueCubeMarker(plagueCubeMarker, delay);
    }

    /**
     * Animates the PlagueCubeMarker
     *
     * @param plagueCubeMarker The PlagueCubeMarker to be animated
     * @param delay            The delay of the animation
     * @since 2025-01-21
     */
    private void animatePlagueCubeMarker(PlagueCubeMarker plagueCubeMarker, int delay) {
        int animationDuration = 1000;
        int cycleCount = 2;
        long fromScaling = 1;
        long toScaling = 2;

        Timeline scaleTimeline;

        Scale animationScale = new Scale(1.0, 1.0);

        plagueCubeMarker.getTransforms().add(animationScale);

        scaleTimeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(animationScale.xProperty(), fromScaling),
                        new KeyValue(animationScale.yProperty(), fromScaling)
                ),
                new KeyFrame(Duration.millis(animationDuration),
                        new KeyValue(animationScale.xProperty(), toScaling),
                        new KeyValue(animationScale.yProperty(), toScaling)
                )
        );

        scaleTimeline.setCycleCount(cycleCount);
        scaleTimeline.setDelay(Duration.seconds(delay));

        scaleTimeline.setAutoReverse(true);
        scaleTimeline.play();
    }

    /**
     * Creates a PlagueCubeMarker for the given field
     *
     * @param field The field for which a PlagueCubeMarker should be created
     * @return The created PlagueCubeMarker
     * @see PlagueCubeMarker
     */
    private PlagueCubeMarker createPlagueCubeMarker(Field field) {
        PlagueCubeMarker plagueCubeMarker = new PlagueCubeMarker(field);
        PlagueCubeMarkerPresenter plagueCubeMarkerPresenter = new PlagueCubeMarkerPresenter(plagueCubeMarker, field);
        plagueCubeMarkerPresenters.add(plagueCubeMarkerPresenter);
        plagueCubeMarkerPane.getChildren().add(plagueCubeMarker);

        handleWebViewSizeAndPosition(webView, plagueCubeMarker, field, PLAGUE_CUBE_MARKER_SCALE_FACTOR);

        return plagueCubeMarker;
    }

    /**
     * Handles the size and position of the WebView
     *
     * @param sourceWebView The WebView whose size and position will be bound to the targetNode
     * @param targetNode The node whose size and position will be adjusted based on the sourceWebView
     * @param field The field to which the targetNode should be bound
     */
    private void handleWebViewSizeAndPosition(WebView sourceWebView, Node targetNode, Field field, double scaleFactor) {
        double xCoordinate = (double) field.getXCoordinate() / SVG_VIEW_BOX_MAX_X;
        double yCoordinate = (double) field.getYCoordinate() / SVG_VIEW_BOX_MAX_Y;
        double xScaleFactor = scaleFactor / SVG_VIEW_BOX_MAX_X;
        double yxScaleFactor = scaleFactor / SVG_VIEW_BOX_MAX_Y;
        NodeBindingUtils.bindWebViewSizeAndPositionToNode(sourceWebView, targetNode, xCoordinate, yCoordinate, xScaleFactor, yxScaleFactor);
    }

    /**
     * Places a research laboratory marker on the current player's field and sends the corresponding action to the backend.
     * <p>
     * This method iterates through the list of possible actions for the current turn and checks if the action
     * is related to building a research laboratory. If such an action is found, it is sent to the backend
     * using the ActionService.
     * </p>
     */
    public void addResearchLaboratoryMarkerToField() {
        List<Action> possibleActions = game.getCurrentTurn().getPossibleActions();
        for (Action action : possibleActions) {
            if (action instanceof BuildResearchLaboratoryAction || action instanceof ReducedCostBuildResearchLaboratoryAction) {
                actionService.sendAction(game, action);
            }
        }
    }

    /**
     * Sends a waive action for the current turn.
     * This method iterates through the list of possible actions for the current turn,
     * and if an action is an instance of WaiveAction, it sends that action using the action service.
     */
    public void sendWaiveAction() {
        for(Action action : getPossibleTurnActions()) {
            if(action instanceof WaiveAction) {
                actionService.sendAction(game, action);
            }
        }
    }

    /**
     * @return the possible turn actions
     */
    public List<Action> getPossibleTurnActions() {
        return game.getCurrentTurn().getPossibleActions();
    }

    /**
     * Sets a click listener to all {@link PlayerMarker} with any of given {@code players}
     * to highlight all given {@code availableFields} and invoke given {@code fieldAndPlayerSelectionConsumer}
     * with {@link Player} of clicked {@link PlayerMarker} and clicked {@link Field}.
     *
     * @param players {@link List} of {@link Player} to assign click listeners to respective {@link PlayerMarker}
     * @param availableFields {@link List} of {@link Field} to highlight
     * @param fieldAndPlayerSelectionConsumer {@link BiConsumer} invoked with selected {@link Field} and {@link Player}
     */
    public void setClickListenersForPlayerMarkersAndFields(final List<Player> players,
                                                           final List<Field> availableFields,
                                                           final BiConsumer<Field, Player> fieldAndPlayerSelectionConsumer) {
        final List<PlayerMarkerPresenter> playerMarkerPresentersList = findPlayerMarkerPresentersByPlayers(players);

        final Runnable unhighlightAllPlayerMarkers = () -> playerMarkerPresentersList.forEach(PlayerMarkerPresenter::unhighlight);
        final BiConsumer<Field, Player> fieldAndPlayerSelectionConsumerWithUnhighlighting = (field, player) -> {
            unhighlightAllPlayerMarkers.run();
            fieldAndPlayerSelectionConsumer.accept(field, player);
        };

        playerMarkerPresentersList.forEach(playerMarkerPresenter -> setClickListenersForPlayerMarkerAndFields(
                playerMarkerPresenter,
                availableFields,
                fieldAndPlayerSelectionConsumerWithUnhighlighting
        ));
    }

    /**
     * Sets a click listeners to given {@link PlayerMarkerPresenter} and {@code availableFields} with highlighting and unhighlighting.
     *
     * @param playerMarkerPresenter {@link PlayerMarkerPresenter} of {@link PlayerMarker} to highlight
     * @param availableFields {@link List} of fields to highlight and add click listeners to
     * @param fieldAndPlayerSelectionConsumer {@link BiConsumer} called with clicked {@link Field} and {@link Player}
     */
    private void setClickListenersForPlayerMarkerAndFields(final PlayerMarkerPresenter playerMarkerPresenter,
                                                           final List<Field> availableFields,
                                                           final BiConsumer<Field, Player> fieldAndPlayerSelectionConsumer) {
        playerMarkerPresenter.highlight();
        playerMarkerPresenter.setClickListenerForPlayerMarkerAndFields(
                availableFields,
                fieldAndPlayerSelectionConsumer
        );
    }

    /**
     * Returns a {@link List} of {@link PlayerMarkerPresenter} for players in {@code players}.
     *
     * @param players {@link List} of {@link Player} for each {@link PlayerMarkerPresenter}
     * @return {@link List} of {@link PlayerMarkerPresenter} for players in {@code players}
     */
    private List<PlayerMarkerPresenter> findPlayerMarkerPresentersByPlayers(final List<Player> players) {
        return this.playerMarkerPresenters.entrySet().stream()
                .filter(entry -> players.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .toList();
    }
}
