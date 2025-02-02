package de.uol.swp.client.game;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.util.NodeBindingUtils;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;

import java.util.List;

/**
 * Creates all city names of a game and presents them on the {@link de.uol.swp.client.game.GameMapPresenter}.
 */
public class CityNamePanePresenter extends AbstractPresenter {

    private final Pane cityNamePane;

    private final WebView webView;

    private static final double CITY_NAME_SCALE_FACTOR = 1.0;

    /**
     * Constructor
     *
     * @param game    The {@link Game} for which city names will be created.
     * @param webView The {@link WebView} to which all city names will be bound to.
     */
    public CityNamePanePresenter(Game game, WebView webView, Pane cityNamePane) {
        this.webView = webView;
        this.cityNamePane = cityNamePane;

        createAllNames(game.getFields());
    }

    /**
     * Creates city names for all given {@link Field}s.
     *
     * @param fields The {@link Field} list for which the city names will be created.
     */
    private void createAllNames(List<Field> fields) {
        for (Field field : fields) {
            createSingleName(field);
        }
    }

    /**
     * Creates a city name for the given {@link Field} and binds it to the {@link #webView}.
     *
     * @param field The {@link Field} for which a city name will be created.
     */
    private void createSingleName(Field field) {
        Text cityName = new Text(field.getCity().getName());
        cityName.setFont(Font.font(null, FontWeight.BOLD, 20));

        double textWidth = cityName.getBoundsInLocal().getWidth();
        double textHeight = cityName.getBoundsInLocal().getHeight();

        cityName.setX(-textWidth / 2 - textWidth / 50);
        cityName.setY(textHeight / 2 - textHeight / 4.3);

        cityNamePane.getChildren().add(cityName);

        bindLineToWebView(cityName, field);
    }

    /**
     * Binds the given {@link Text} to the {@link #webView}.
     *
     * @param text  The {@link Text} to be bound to the {@link #webView}.
     * @param field The {@link Field} associated with the given {@link Text}.
     * @see NodeBindingUtils
     */
    private void bindLineToWebView(Text text, Field field) {
        double yOffset = CityMarker.getRADIUS() * GameMapPresenter.getCITY_MARKER_SCALE_FACTOR() * 1.75;
        double xCoordinate = (double) field.getXCoordinate() / GameMapPresenter.getSVG_VIEW_BOX_MAX_X();
        double yCoordinate = (field.getYCoordinate() + yOffset) / GameMapPresenter.getSVG_VIEW_BOX_MAX_Y();
        double xScaleFactor = CITY_NAME_SCALE_FACTOR / GameMapPresenter.getSVG_VIEW_BOX_MAX_X();
        double yScaleFactor = CITY_NAME_SCALE_FACTOR / GameMapPresenter.getSVG_VIEW_BOX_MAX_Y();
        NodeBindingUtils.bindWebViewSizeAndPositionToNode(webView, text, xCoordinate, yCoordinate, xScaleFactor, yScaleFactor);
    }
}
