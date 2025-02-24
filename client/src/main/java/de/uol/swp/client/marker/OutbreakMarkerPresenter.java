package de.uol.swp.client.marker;

import de.uol.swp.client.util.ScalableSVGStackPane;
import de.uol.swp.client.util.exception.NodeNotFoundException;
import de.uol.swp.common.marker.OutbreakMarker;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;

/**
 * Uses the {@link LevelableMarkerPresenter} to display all levels of a given {@link OutbreakMarker} in a given
 * {@link StackPane} and shows the current level. Replaces the final level number with an SVG image.
 */
public class OutbreakMarkerPresenter extends LevelableMarkerPresenter {

    private static final Logger LOG = LogManager.getLogger(OutbreakMarkerPresenter.class);

    /**
     * Constructor
     * <p>
     * Creates a {@link LevelableMarkerPresenter} and replaces the final level number with an SVG image.
     *
     * @param stackPane      The {@link StackPane} that will display {@link #gridPane} containing the outbreak marker.
     * @param outbreakMarker The {@link OutbreakMarker} that will be represented by this presenter.
     */
    public OutbreakMarkerPresenter(StackPane stackPane, OutbreakMarker outbreakMarker, List<Color> colorList) {
        super(stackPane, outbreakMarker, colorList);
        replaceFinalLevelNumberWithSvg();
    }

    /**
     * Replaces the final level number of the {@link #gridPane} with an SVG image using {@link ScalableSVGStackPane}.
     */
    private void replaceFinalLevelNumberWithSvg() {
        int rowIndex = 0;
        int columnIndex = 1;
        Pane targetCell;

        try {
            targetCell = (Pane) getNodeFromGridPane(rowIndex, columnIndex);
        } catch (NodeNotFoundException e) {
            LOG.error(e);
            return;
        }

        targetCell.getChildren().removeIf(StackPane.class::isInstance);

        File svgFile = new File("client/src/main/resources/images/skull.svg");
        ScalableSVGStackPane svgStackPane = new ScalableSVGStackPane(svgFile, 0.2);
        gridPane.add(svgStackPane, columnIndex, rowIndex);
    }
}
