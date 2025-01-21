package de.uol.swp.client.game;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.player.PlayerMarker;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.player.Player;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.List;

/**
 * Presenter class for managing the player pane in the game UI.
 * <p>
 * This class is responsible for updating the player information, handling the player's hand cards,
 * and managing the visual representation of the player marker.
 * </p>
 *
 * @since 2025-01-16
 * @author Marvin Tischer
 */
public class PlayerPanePresenter extends AbstractPresenter {
    @FXML
    private Text playerNameText;
    @FXML
    private Label playerRoleLabel;
    @FXML
    private Pane symbolPane;

    private Player player;

    @FXML
    private ListView<PlayerCard> handCardsList;

    /**
     * Sets the hand cards of the player and updates the ListView.
     *
     * @param handCardTitles the list of hand cards to display
     * @since 2025-01-16
     * @author Marvin Tischer
     */
    public void setHandCards(List<PlayerCard> handCardTitles) {
        Platform.runLater(() -> handCardsList.getItems().setAll(handCardTitles));
    }

    /**
     * Updates the player's information in the UI, including name, role, and hand cards.
     *
     * @param player the player whose information is to be displayed
     * @since 2025-01-16
     * @author Marvin Tischer
     */
    public void setPlayerInfo(Player player) {
        playerNameText.setText(player.getName());
        playerRoleLabel.setText(player.getRole().toString());
        setHandCards(player.getHandCards());
        this.player = player;
    }

    /**
     * Sets the visual marker for the player.
     *
     * @param playerMarker the marker to be displayed in the symbol pane
     * @since 2025-01-16
     * @author Marvin Tischer
     */
    public void setPlayerMarker(PlayerMarker playerMarker) {
        symbolPane.getChildren().clear();
        symbolPane.minWidthProperty().bind(symbolPane.heightProperty());
        symbolPane.prefWidthProperty().bind(symbolPane.heightProperty());
        symbolPane.maxWidthProperty().bind(symbolPane.heightProperty());
        symbolPane.getChildren().add(playerMarker);
        bindPlayerMarkerToSymbolPane(playerMarker);
    }

    private void bindPlayerMarkerToSymbolPane(PlayerMarker playerMarker){
        double scaleFactor = 0.027;
        playerMarker.layoutXProperty().bind(symbolPane.widthProperty().divide(2));
        playerMarker.layoutYProperty().bind(symbolPane.heightProperty().divide(2));
        playerMarker.scaleXProperty().bind(symbolPane.widthProperty().multiply(scaleFactor));
        playerMarker.scaleYProperty().bind(symbolPane.heightProperty().multiply(scaleFactor));
    }

    @Override
    public String getFXMLFilePath() {
        return "/fxml/game/PlayerPaneComponent.fxml";
    }

    public boolean hasPlayer(Player player) {
        return this.player.equals(player);
    }

}
