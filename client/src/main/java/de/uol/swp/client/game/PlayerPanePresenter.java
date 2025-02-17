package de.uol.swp.client.game;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.card.DiscardCardDialog;
import de.uol.swp.client.player.PlayerMarker;
import de.uol.swp.common.approvable.ApprovableMessageStatus;
import de.uol.swp.common.approvable.request.ApprovableRequest;
import de.uol.swp.common.card.InfectionCard;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.card.event_card.*;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.triggerable.request.TriggerableRequest;
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
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

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

    @Setter
    private Supplier<Game> gameSupplier;
    @Setter
    private GameMapPresenter gameMapPresenter;

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
        this.handCardToClickListenerAssociation.put(handCard, createRunnableForEventCard(handCard, clickListener));
        highlightHandCardCellsWithClickListeners();
    }

    /**
     * Creates a new click listener if given {@link PlayerCard} is an {@link EventCard}
     * by invoking methods that determine what to do with it.
     *
     * @param playerCard {@link PlayerCard} to create {@link Runnable} click listener for
     * @param clickListener original, unmodified {@link Runnable} click listener
     * @return potentially modified {@link Runnable} click listener for given {@link PlayerCard}
     */
    private Runnable createRunnableForEventCard(final PlayerCard playerCard, final Runnable clickListener) {
        if (playerCard instanceof AirBridgeEventCard airBridgeEventCard) {
            return prepareAirBridgeEventCard(airBridgeEventCard, clickListener);
        } else if (playerCard instanceof ForecastEventCard forecastEventCard) {

        } else if (playerCard instanceof GovernmentSubsidiesEventCard governmentSubsidiesEventCard) {

        } else if (playerCard instanceof ToughPopulationEventCard toughPopulationEventCard) {
            return prepareToughPopulationEventCard(toughPopulationEventCard, clickListener);
        }
        return clickListener;
    }

    /**
     * Prepares a given {@link AirBridgeEventCard} for use.
     *
     * @param airBridgeEventCard {@link AirBridgeEventCard} to prepare an action listener for
     * @param approve {@link Runnable} executing the given {@link AirBridgeEventCard}
     * @return {@link Runnable} as action listener to play given {@link AirBridgeEventCard}
     */
    private Runnable prepareAirBridgeEventCard(final AirBridgeEventCard airBridgeEventCard, final Runnable approve) {
        return () -> {
            final Game game = airBridgeEventCard.getGame();
            this.gameMapPresenter.setClickListenersForPlayerMarkersAndFields(
                    game.getPlayersInTurnOrder(),
                    game.getFields(),
                    createAirBridgeEventCardPlayerAndFieldClickConsumer(airBridgeEventCard)
            );
        };
    }

    /**
     * Creates a {@link BiConsumer} that is invoked with the target {@link Player} and {@link Field} of an {@link AirBridgeEventCard}.
     *
     * @param airBridgeEventCard {@link AirBridgeEventCard} that is played
     * @return {@link BiConsumer} invoked with the target {@link Player} and {@link Field} of an {@link AirBridgeEventCard}
     */
    private BiConsumer<Field, Player> createAirBridgeEventCardPlayerAndFieldClickConsumer(final AirBridgeEventCard airBridgeEventCard) {
        return (field, movedPlayer) -> {
            airBridgeEventCard.setTargetPlayer(movedPlayer);
            airBridgeEventCard.setTargetField(field);

            Message messageToSend = createCorrectAirBridgeEventCardMessage(airBridgeEventCard, movedPlayer);
            this.eventBus.post(messageToSend);
        };
    }

    /**
     * Creates the correct {@link Message} for an {@link AirBridgeEventCard}.
     * The returned {@link Message} will be a {@link TriggerableRequest}
     * if the holder of the {@link AirBridgeEventCard} is the moved {@link Player}.
     * It will be an {@link ApprovableRequest} with status {@link ApprovableMessageStatus#OUTBOUND}
     * if the moved {@link Player} is not the holder.
     *
     * @param airBridgeEventCard {@link AirBridgeEventCard} to send via the {@link Message}
     * @return {@link TriggerableRequest} if moved {@link Player} is event card holder or {@link ApprovableRequest} if not
     */
    private Message createCorrectAirBridgeEventCardMessage(final AirBridgeEventCard airBridgeEventCard, final Player movedPlayer) {
        Message messageToSend = new TriggerableRequest(airBridgeEventCard, null, null);

        final Player eventCardHolder = airBridgeEventCard.getPlayer();

        if (!eventCardHolder.equals(movedPlayer)) {
            messageToSend = new ApprovableRequest(
                    ApprovableMessageStatus.OUTBOUND,
                    airBridgeEventCard,
                    messageToSend,
                    eventCardHolder,
                    null,
                    null
            );
        } else {
            airBridgeEventCard.approve();
        }

        return messageToSend;
    }

    private Runnable prepareToughPopulationEventCard(final ToughPopulationEventCard toughPopulationEventCard, final Runnable approve) {
        return () -> {
            String buttonText = "Karte auswählen";
            DiscardCardDialog<InfectionCard> dialog = new DiscardCardDialog<>(gameSupplier.get().getInfectionDiscardStack(), buttonText, toughPopulationEventCard.getDescription());
            dialog.showAndWait().ifPresent(card -> {
                toughPopulationEventCard.setInfectionCard(card);
                approve.run();
            });
        };
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
