package de.uol.swp.client.game;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.player.PlayerMarker;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.player.Player;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    private Map<PlayerCard, Runnable> handCardToClickListenerAssociation;

    /**
     * Initializes this {@link PlayerPanePresenter} when the corresponding fxml file is loaded.
     * Initializes the {@link #handCardToClickListenerAssociation} and assigns click listeners to {@link #handCardsList}.
     *
     * @see #assignClickListenerToHandCardsList()
     */
    @FXML
    private void initialize() {
        this.handCardToClickListenerAssociation = new HashMap<>();
        assignClickListenerToHandCardsList();
    }

    /**
     * Assigns click listeners to {@link #handCardsList} and
     * executes a {@link Runnable} stored in {@link #handCardToClickListenerAssociation}
     * depending on which {@link PlayerCard} was clicked.
     */
    private void assignClickListenerToHandCardsList() {
        this.handCardsList.setOnMouseClicked(event -> {
            final PlayerCard selectedItem = this.handCardsList.getSelectionModel().getSelectedItem();
            final Runnable clickListener = this.handCardToClickListenerAssociation.getOrDefault(selectedItem, () -> {});
            clickListener.run();
        });
    }

    /**
     * Sets the hand cards of the player and updates the ListView.
     *
     * @param handCardTitles the list of hand cards to display
     * @since 2025-01-16
     * @author Marvin Tischer
     */
    public void setHandCards(List<PlayerCard> handCardTitles) {
        Platform.runLater(() -> handCardsList.getItems().setAll(handCardTitles));
        highlightHandCardCellsWithClickListeners();
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

    /**
     * Highlights given {@link PlayerCard} in {@link #handCardsList} and assigns given {@link Runnable} as click listener.
     *
     * @param handCard {@link PlayerCard} to highlight
     * @param clickListener {@link Runnable} that is executed when given {@link PlayerCard} in {@link #handCardsList} is clicked
     */
    public void highlightHandCardAndAssignClickListener(final PlayerCard handCard, final Runnable clickListener) {
        this.handCardToClickListenerAssociation.put(handCard, clickListener);
        highlightHandCardCellsWithClickListeners();
    }

    /**
     * Highlights all {@link ListCell} within {@link #handCardsList}
     * that have hand cards with click listeners as defined by {@link #handCardToClickListenerAssociation}.
     */
    private void highlightHandCardCellsWithClickListeners() {
        for (final Node node : this.handCardsList.lookupAll(".list-cell")) {
            if (node instanceof ListCell<?> handCardListCell) {
                highlightHandCardCellWithClickListener(handCardListCell);
            }
        }
    }

    /**
     * Highlights a given {@link ListCell} if its item is contained in {@link #handCardToClickListenerAssociation}.
     *
     * @param handCardListCell the {@link ListCell} to determine highlighting status for
     */
    private void highlightHandCardCellWithClickListener(final ListCell<?> handCardListCell) {
        Platform.runLater(() -> {
            if (handCardListCell.getItem() instanceof PlayerCard playerCard &&
                    this.handCardToClickListenerAssociation.containsKey(playerCard)) {
                handCardListCell.setBackground(Background.fill(Color.BLUE));
            } else {
                handCardListCell.setBackground(Background.EMPTY);
            }
        });
    }
}
