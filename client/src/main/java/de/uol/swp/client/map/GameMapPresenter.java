package de.uol.swp.client.map;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.action.ActionService;
import de.uol.swp.client.approvable.ApprovableService;
import de.uol.swp.client.map.research_laboratory.ResearchLaboratoryMarker;
import de.uol.swp.client.map.research_laboratory.ResearchLaboratoryMarkerPresenter;
import de.uol.swp.client.plague.PlagueCubeMarker;
import de.uol.swp.client.plague.PlagueCubeMarkerPresenter;
import de.uol.swp.client.player.PlayerMarker;
import de.uol.swp.client.player.PlayerMarkerPresenter;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.client.util.ColorService;
import de.uol.swp.client.util.NodeBindingUtils;
import de.uol.swp.common.action.Action;
import de.uol.swp.common.action.advanced.build_research_laboratory.BuildResearchLaboratoryAction;
import de.uol.swp.common.action.advanced.cure_plague.CurePlagueAction;
import de.uol.swp.common.action.simple.WaiveAction;
import de.uol.swp.common.card.event_card.GovernmentSubsidiesEventCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.turn.request.EndPlayerTurnRequest;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.role.RoleCard;
import de.uol.swp.common.user.User;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

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

    private Supplier<Game> gameSupplier;

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
    private final List<ResearchLaboratoryMarkerPresenter> researchLaboratoryMarkerPresenters = new ArrayList<>();
    private final List<PlagueCubeMarkerPresenter> plagueCubeMarkerPresenters = new ArrayList<>();

    private static final double PLAYER_MARKER_SCALE_FACTOR = 1.75;
    @Getter
    private static final double CITY_MARKER_SCALE_FACTOR = 2.25;
    private static final double PLAGUE_CUBE_MARKER_SCALE_FACTOR = 1.75;
    private static final double RESEARCH_LABORATORY_MARKER_SCALE_FACTOR = 1.75;

    /**
     * Initializes the game map with the given game data.
     *
     * @param gameSupplier the game data to initialize the map with
     */
    public void initialize(Supplier<Game> gameSupplier) {
        this.gameSupplier = gameSupplier;
        Game game = gameSupplier.get();
        this.webView.setContextMenuEnabled(false);
        setPanePickOnBoundsToFalse();

        cityConnectionPanePresenter = new CityConnectionPanePresenter(game, this.webView, this.cityConnectionPane);
        cityNamePanePresenter = new CityNamePanePresenter(game, this.webView, this.cityNamePane);

        bindSizePropertyOfWebView();
        loadSvgIntoWebView();
        addAllCityMarkers();
        addAllPlagueCubeMarkers();
        addAllPlayerMarkers();

        addResearchLaboratoryMarkers(game);
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
     * @param updatedGame the updated game state received from the server
     * @author Jannis Moehlenbrock
     */
    public void updateGameState(Game updatedGame) {
        Game game = gameSupplier.get();
        Platform.runLater(() -> {
            removeResearchLaboratoryMarkers();
            movePlayerMarker(game);
            addResearchLaboratoryMarkers(game);
            updatePlagueCubeMarkers();
            addNewPlagueCubeMarker(game);
            unhighlightCityMarkers();
        });
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
        Game game = gameSupplier.get();
        ResearchLaboratoryMarker researchLaboratoryMarker = new ResearchLaboratoryMarker(0.7);
        buildResearchLaboratoryMarker(researchLaboratoryMarker, field);
        ResearchLaboratoryMarkerPresenter researchLaboratoryMarkerPresenter = new ResearchLaboratoryMarkerPresenter(researchLaboratoryMarker, game, actionService, field);
        researchLaboratoryMarkerPresenters.add(researchLaboratoryMarkerPresenter);
        researchLaboratoryMarkerPresenter.initializeMouseEvents();
    }

    /**
     * Scales and adds the research laboratory marker to the pane
     * @param researchLaboratoryMarker laboratory marker
     * @param field current field
     */
    public void buildResearchLaboratoryMarker(ResearchLaboratoryMarker researchLaboratoryMarker, Field field) {
        Game game = gameSupplier.get();
        researchLaboratoryPane.getChildren().add(researchLaboratoryMarker);

        double xOffset = 2.5 * CityMarker.getRADIUS() / SVG_VIEW_BOX_MAX_X;
        double yOffset = 3.5 * CityMarker.getRADIUS() / SVG_VIEW_BOX_MAX_Y;
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
        researchLaboratoryPane.getChildren().removeIf(ResearchLaboratoryMarker.class::isInstance);
    }

    /**
     * Starts pulse animations for all research lab markers and adds click interactions.
     *
     */
    public void requireMoveResearchLaboratory() {
        for (Node node : researchLaboratoryPane.getChildren()) {
            Game game = gameSupplier.get();
            if (node instanceof ResearchLaboratoryMarker researchLaboratoryMarker) {
                Timeline pulseTimeline = createPulseAnimation(researchLaboratoryMarker);
                pulseTimeline.stop();
                pulseTimeline.play();

                game.setResearchLaboratoryButtonClicked(true);
            }
        }
    }

    /**
     * Prompts player to select a plague cube on their current field.
     * Starts pulsing animation for plague cubes on the current player's field
     * and sets up click handlers to trigger handlePlagueCubeClick().
     * Used when multiple plague types exist on the same field.
     */
    public void requireChoosePlagueCube() {
        Game game = gameSupplier.get();
        Player currentPlayer = game.getCurrentTurn().getPlayer();
        Field currentField = currentPlayer.getCurrentField();

        plagueCubeMarkerPresenters.forEach(PlagueCubeMarkerPresenter::stopAnimation);

        plagueCubeMarkerPresenters.stream()
                .filter(plagueCubeMarkerPresenter -> plagueCubeMarkerPresenter.getField().equals(currentField))
                .forEach(plagueCubeMarkerPresenter -> {
                    plagueCubeMarkerPresenter.startPulsingAnimation();
                    plagueCubeMarkerPresenter.addOnMouseClickedForEachPlagueCubeIcon(this::handlePlagueCubeClick);
                });
    }

    /**
     * Handles the click event on a plague cube and sends the CurePlagueAction to the backend.
     * Removes click handlers from all plague cube markers, stops their animations,
     * sets the selected plague on the CurePlagueAction, and sends it to the action service.
     *
     * @param plague The plague that was clicked and will be cured
     */
    private void handlePlagueCubeClick(Plague plague) {
        Game game = gameSupplier.get();
        plagueCubeMarkerPresenters.forEach(PlagueCubeMarkerPresenter::removeOnMouseClickedForEachPlagueCubeIcon);
        plagueCubeMarkerPresenters.forEach(PlagueCubeMarkerPresenter::stopAnimation);
        for (Action action : getPossibleTurnActions()) {
            if (action instanceof CurePlagueAction curePlagueAction) {
                curePlagueAction.setPlague(plague);
                actionService.sendAction(game, curePlagueAction);
                break;
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
        marker.getTransforms().removeIf(Scale.class::isInstance);
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

        if (infectedFieldsInTurn.isEmpty()) {
            sendEndPlayerTurnRequest(game);
            return;
        }

        List<Field> lastInfectedFields = infectedFieldsInTurn.get(infectedFieldsInTurn.size() - 1);
        final int numberOfInfectedFields = lastInfectedFields.size();
        for (int i = 0; i < numberOfInfectedFields; i++) {
            final Field field = lastInfectedFields.get(i);
            boolean isLastField = i == numberOfInfectedFields - 1;
            processField(field, game, isLastField);
        }
    }

    private void processField(Field field, Game game, boolean isLastField) {
        final List<Plague> plagues = new ArrayList<>(field.getPlagueCubes().keySet());
        final int plaguesSize = plagues.size();
        for (int i = 0; i < plaguesSize; i++) {
            final Plague plague = plagues.get(i);
            final boolean isLastPlague = i == plaguesSize - 1;
            final boolean requiresSendingOfEndPlayerTurnRequest = isLastField && isLastPlague;
            if (!game.hasAntidoteMarkerForPlague(plague)) {
                addAnimatedPlagueCubeMarker(field, requiresSendingOfEndPlayerTurnRequest);
            } else if (requiresSendingOfEndPlayerTurnRequest) {
                sendEndPlayerTurnRequest(game);
            }
        }
    }

    /**
     * Updates the PlagueCubeMarkers based on the current game state.
     *
     */
    private void updatePlagueCubeMarkers() {
        clearPlagueCubeMarkers();
        addAllPlagueCubeMarkers();
    }

    /**
     * Removes all PlagueCubeMarkers.
     */
    private void clearPlagueCubeMarkers() {
        plagueCubeMarkerPane.getChildren().clear();
        plagueCubeMarkerPresenters.clear();
    }

    /**
     * Removes all existing player markers and create alle existing player markers after that
     *
     * @param game the current game state
     */
    private void movePlayerMarker(Game game) {
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
        Game game = gameSupplier.get();
        playerMarkerPresenters.clear();
        List<Player> playersInTurnOrder = game.getPlayersInTurnOrder();
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
        Game game = gameSupplier.get();
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
    private double calculatePlayerXOffset(int playerOnFieldIndex, int playerAmountOnField, double playerMarkerWidth) {
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
     * @param player the player for which a new PlayerMarker should be created
     * @return the new PlayerMarker
     * @see PlayerMarker
     */
    public PlayerMarker createNewPlayerMarker(Player player) {
        Game game = gameSupplier.get();
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
        Game game = gameSupplier.get();
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
        Game game = gameSupplier.get();
        for (Field field : game.getFields()) {
            createPlagueCubeMarker(field);
        }
    }

    /**
     * Creates animated plagueCubeMarker for the associated plague of a given field and adds them to the pane with delay.
     *
     * @param field field to create PlagueCubeMarker for
     * @see PlagueCubeMarker
     * @since 2024-11-09
     */
    private void addAnimatedPlagueCubeMarker(Field field, boolean setFinnishListener) {
        PlagueCubeMarker plagueCubeMarker = createPlagueCubeMarker(field);

        animatePlagueCubeMarker(plagueCubeMarker, setFinnishListener);
    }

    /**
     * Animates the PlagueCubeMarker
     *
     * @param plagueCubeMarker The PlagueCubeMarker to be animated
     * @since 2025-01-21
     */
    private void animatePlagueCubeMarker(PlagueCubeMarker plagueCubeMarker, boolean setFinnishListener) {
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
        scaleTimeline.setDelay(Duration.seconds(0));

        scaleTimeline.setAutoReverse(true);
        scaleTimeline.play();
        if (setFinnishListener) {
            scaleTimeline.setOnFinished(event -> sendEndPlayerTurnRequest(gameSupplier.get()));
        }
    }

    /**
     * Sends an EndPlayerTurnRequest if the current player's turn is over and they are the player of this client.
     * <p>
     * This method checks if the current player's turn is over and if the current player is the player of this client.
     * If both conditions are met, it creates an EndPlayerTurnRequest and posts it to the event bus.
     * </p>
     */
    private void sendEndPlayerTurnRequest(Game game) {
        Player currentPlayer = game.getCurrentPlayer();
        User loggedInUser = loggedInUserProvider.get();
        if (isTurnOver(game) && currentPlayer.containsUser(loggedInUser)) {
            EndPlayerTurnRequest endTurnMessage = new EndPlayerTurnRequest(game);
            eventBus.post(endTurnMessage);
        }
    }

    /**
     * Checks if the current turn is over
     * @param game The game to check
     * @return True if the turn is over, false otherwise
     */
    private boolean isTurnOver(Game game) {
        return game.getCurrentTurn().isOver();
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
        Game game = gameSupplier.get();
        List<Action> possibleActions = game.getCurrentTurn().getPossibleActions();
        for (Action action : possibleActions) {
            if (action instanceof BuildResearchLaboratoryAction) {
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
        Game game = gameSupplier.get();
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
        Game game = gameSupplier.get();
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
        Game game = gameSupplier.get();
        if (game.isGameLost()) {
            return;
        }
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
        Game game = gameSupplier.get();
        if (game.isGameLost()) {
            return;
        }
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

    /**
     * Sets click listeners for the government subsidies fields.
     * <p>
     * This method sets up click listeners for the current player's marker and the fields
     * associated with the given {@link GovernmentSubsidiesEventCard}. When a field is clicked,
     * the provided {@code approve} runnable is executed.
     * </p>
     *
     * @param governmentSubsidiesEventCard the event card containing the fields for government subsidies
     * @param approve the runnable to execute when a field is clicked
     */
    public void setClickListenersForGovernmentSubsidiesFields(GovernmentSubsidiesEventCard governmentSubsidiesEventCard, Runnable approve) {
        Game game = gameSupplier.get();
        if (game.isGameLost()) {
            return;
        }
        highlightCityMarkers();
        setupGovernmentSubsidiesActions(governmentSubsidiesEventCard, approve);
    }

    /**
     * Highlights all city markers in {@link #cityMarkers}.
     */
    private void highlightCityMarkers() {
        for (final Map.Entry<Field, CityMarker> entry : cityMarkers.entrySet()) {
            final CityMarker cityMarker = entry.getValue();
            cityMarker.highlight();
        }
    }

    /**
     * Sets up the actions for the government subsidies event card.
     *
     * @param governmentSubsidiesEventCard the event card to set up the actions for
     * @param approve the runnable to execute when an action is approved
     */
    private void setupGovernmentSubsidiesActions(GovernmentSubsidiesEventCard governmentSubsidiesEventCard, Runnable approve) {
        for (final Map.Entry<Field, CityMarker> entry : cityMarkers.entrySet()) {
            final CityMarker cityMarker = entry.getValue();
            final Field field = entry.getKey();
            cityMarker.setOnMouseClicked(event -> executeGovernmentSubsidiesAction(governmentSubsidiesEventCard, approve, field));
        }
    }

    /**
     * Executes the action for the given {@link GovernmentSubsidiesEventCard} and {@link Field}.
     *
     * @param governmentSubsidiesEventCard the event card to execute the action for
     * @param approve the runnable to execute when the action is approved
     * @param field the field to execute the action for
     */
    private void executeGovernmentSubsidiesAction(GovernmentSubsidiesEventCard governmentSubsidiesEventCard, Runnable approve, Field field) {
        Game game = gameSupplier.get();
        governmentSubsidiesEventCard.setField(field);
        unhighlightCityMarkers();

        final boolean researchLaboratoryToBeMoved = game.requiresResearchLaboratoryMove();
        if (researchLaboratoryToBeMoved) {
            researchLaboratoryMarkerPresenters.forEach(presenter -> presenter.setClickListenerForGovernmentSubsidies(governmentSubsidiesEventCard, approve));
        } else {
            approve.run();
        }
    }
}
