package de.uol.swp.client.game;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.player.PlayerMarker;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.client.util.ColorService;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.map.GameMap;
import de.uol.swp.common.map.MapSlot;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.role.RoleCard;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Manages the window representing the game map
 *
 * @author David Scheffler
 * @see AbstractPresenter
 * @since 2024-09-09
 */
public class GameMapPresenter extends AbstractPresenter {

    private Game game;

    private static final Logger LOG = LogManager.getLogger(GameMapPresenter.class);

    private static final int SVG_VIEW_BOX_WIDTH = 2097;
    private static final int SVG_VIEW_BOX_HEIGHT = 1075;

    private static final double ASPECT_RATIO = (double) SVG_VIEW_BOX_WIDTH / SVG_VIEW_BOX_HEIGHT;

    @FXML
    private Pane pane;

    @FXML
    private WebView webView;

    @Inject
    private LoggedInUserProvider loggedInUserProvider;

    /**
     * <p>
     * Return {@value #DEFAULT_FXML_FOLDER_PATH}+{@value GamePresenter#GAME_FXML_FOLDER_PATH}
     * </p>
     * <p>
     * {@inheritDoc}
     */
    @Override
    public String getFXMLFolderPath() {
        return DEFAULT_FXML_FOLDER_PATH + GamePresenter.GAME_FXML_FOLDER_PATH;
    }

    /**
     * Initializes the game map window
     *
     * @author David Scheffler
     * @since 2024-09-09
     */
    @FXML
    public void initialize(Game game) {
        this.game = game;

        bindSizePropertyOfWorldMapWebView();

        loadSvgIntoWebView();

        addCityMarkers();

        addAllPlayerMarkers();
    }

    /**
     * Adds all players of the game to the pane as PlayerMarkers
     *
     * @see PlayerMarker
     * @since 2024-09-28
     */
    private void addAllPlayerMarkers() {
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
        PlayerMarker playerMarker = createNewPlayerMarker(player);
        pane.getChildren().add(playerMarker);

        Field startingField = getStartingField(this.game.getMap());
        int playerOnFieldIndex = startingField.getPlayersOnField().indexOf(player);

        bindPlayerMarkerToField(playerMarker, startingField, playerOnFieldIndex);
    }

    /**
     * Returns the starting field of the game map
     *
     * @param gameMap The game map
     * @return The starting field of the game map
     * @see Field
     * @since 2024-10-04
     */
    private Field getStartingField(GameMap gameMap) {
        return gameMap.getStartingField();
    }

    /**
     * Binds the position of the playerMarker to a field
     *
     * @param playerMarker The playerMarker to be bound
     * @param field The field to which the playerMarker should be bound
     * @param playerOnFieldIndex The index of the player on the field
     * @see PlayerMarker
     */
    private void bindPlayerMarkerToField(PlayerMarker playerMarker, Field field, int playerOnFieldIndex) {
        double playerMarkerHeight = playerMarker.getHeight();
        double playerMarkerWidth = playerMarker.getWidth();

        double xOffset = calculatePlayerXOffset(playerOnFieldIndex, playerMarkerWidth);
        int playerAmountOnField = getPlayerAmountOnField(field);

        bindLayoutProperties(playerMarker, field, playerMarkerWidth, playerMarkerHeight, xOffset, playerAmountOnField);
    }

    /**
     * Binds the layout properties of the playerMarker
     *
     * @param playerMarker The playerMarker for which the properties should be bound
     * @param field The field to which the playerMarker should be bound
     * @param playerMarkerWidth The width of the playerMarker
     * @param playerMarkerHeight The height of the playerMarker
     * @param xOffset The x offset of the playerMarker
     * @param playerAmountOnField The amount of players on the field
     * @since 2024-10-04
     */
    private void bindLayoutProperties(PlayerMarker playerMarker, Field field, double playerMarkerWidth, double playerMarkerHeight, double xOffset, int playerAmountOnField) {
        playerMarker.layoutXProperty().bind(
                webView.widthProperty().multiply(field.getXCoordinate() / (double) SVG_VIEW_BOX_WIDTH)
                        .subtract((playerMarkerWidth * playerAmountOnField) / 2)
                        .add(xOffset)
        );

        playerMarker.layoutYProperty().bind(
                webView.heightProperty().multiply(field.getYCoordinate() / (double) SVG_VIEW_BOX_HEIGHT)
                        .subtract(playerMarkerHeight)
        );
    }

    /**
     * Calculates the x offset of the playerMarker
     *
     * @param playerOnFieldIndex The index of the player on the field
     * @param playerMarkerWidth The width of the playerMarker
     * @return The x offset of the playerMarker
     * @since 2024-10-04
     */
    private double calculatePlayerXOffset(int playerOnFieldIndex, double playerMarkerWidth) {
        return playerOnFieldIndex * playerMarkerWidth;
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
    private PlayerMarker createNewPlayerMarker(Player player) {
        RoleCard playerRole = player.getRole();
        Color playerColor = ColorService.convertColorToJavaFXColor(playerRole.getColor());

        double playerSize = 0.75;
        return new PlayerMarker(playerColor, playerSize, loggedInUserProvider, player);
    }

    /**
     * Binds the width and height of the webView to the pane
     *
     * @author David Scheffler
     * @since 2024-09-09
     */
    private void bindSizePropertyOfWorldMapWebView() {
        pane.widthProperty().addListener((obs, oldVal, newVal) -> {
            double newWidth = newVal.doubleValue();
            double newHeight = newWidth / ASPECT_RATIO;

            if (newHeight > pane.getHeight()) {
                newHeight = pane.getHeight();
                newWidth = newHeight * ASPECT_RATIO;
            }

            webView.setPrefSize(newWidth, newHeight);
        });

        pane.heightProperty().addListener((obs, oldVal, newVal) -> {
            double newHeight = newVal.doubleValue();
            double newWidth = newHeight * ASPECT_RATIO;

            if (newWidth > pane.getWidth()) {
                newWidth = pane.getWidth();
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
                    + "html, body { background-color: #a6d1f1; margin: 0; padding: 0; width: 100%; height: 100%; overflow: hidden; display: flex; align-items: center; justify-content: center; }"
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
     * Adds all cities of the mapType to the pane as CityMarkers
     *
     * @author David Scheffler
     * @see CityMarker
     * @since 2024-09-10
     */
    private void addCityMarkers() {
        for (MapSlot mapSlot : game.getMap().getType().getMap()) {
            CityMarker cityMarker = new CityMarker(mapSlot);

            pane.getChildren().add(cityMarker);

            cityMarker.layoutXProperty().bind(webView.widthProperty().multiply(mapSlot.getXCoordinate() / (double) SVG_VIEW_BOX_WIDTH));
            cityMarker.layoutYProperty().bind(webView.heightProperty().multiply(mapSlot.getYCoordinate() / (double) SVG_VIEW_BOX_HEIGHT));
        }
    }

}
