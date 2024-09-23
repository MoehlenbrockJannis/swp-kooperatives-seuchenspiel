package de.uol.swp.client.game;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.MapSlot;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Manages the window representing the game map
 *
 * @see AbstractPresenter
 * @author David Scheffler
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

    /**
     * <p>
     *     Return {@value #DEFAULT_FXML_FOLDER_PATH}+{@value GamePresenter#GAME_FXML_FOLDER_PATH}
     * </p>
     *
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
     * @see CityMarker
     * @author David Scheffler
     * @since 2024-09-10
     */
    private void addCityMarkers() {
        for (MapSlot mapSlot : game.getMap().getType().getMap()){
            CityMarker cityMarker = new CityMarker(mapSlot);

            pane.getChildren().add(cityMarker);

            cityMarker.layoutXProperty().bind(webView.widthProperty().multiply(mapSlot.getXCoordinate() / (double) SVG_VIEW_BOX_WIDTH));
            cityMarker.layoutYProperty().bind(webView.heightProperty().multiply(mapSlot.getYCoordinate() / (double) SVG_VIEW_BOX_HEIGHT));
        }
    }

}
