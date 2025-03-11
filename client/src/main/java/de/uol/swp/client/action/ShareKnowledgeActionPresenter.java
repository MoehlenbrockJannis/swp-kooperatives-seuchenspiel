package de.uol.swp.client.action;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.approvable.ApprovableService;
import de.uol.swp.common.action.advanced.transfer_card.ReceiveCardAction;
import de.uol.swp.common.action.advanced.transfer_card.SendCardAction;
import de.uol.swp.common.action.advanced.transfer_card.ShareKnowledgeAction;
import de.uol.swp.common.approvable.Approvable;
import de.uol.swp.common.card.CityCard;
import de.uol.swp.common.player.Player;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import lombok.Setter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Presenter for the ShareKnowledgeActionView with which the Player can select a card to trade with another Player
 *
 * @author Tom Weelborg
 */
public class ShareKnowledgeActionPresenter extends AbstractPresenter {
    @Setter(onMethod_ = @Inject)
    private ApprovableService approvableService;

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab sendCardActionTab;
    @FXML
    private Tab receiveCardActionTab;
    @FXML
    private ListView<CityCard> sendablePlayerCardsListView;
    @FXML
    private ListView<Player> sendingTargetPlayersListView;
    @FXML
    private ListView<CityCard> receivablePlayerCardsListView;
    @FXML
    private ListView<Player> receivingTargetPlayersListView;

    /**
     * Initializes the ShareKnowledgeActionView with the given {@link SendCardAction} and {@link ReceiveCardAction}.
     * Validates whether one of the given actions is null.
     * Also determines which tab to select by default,
     * focussing the {@link #receiveCardActionTab} if the {@link #sendCardActionTab} is disabled.
     *
     * @param sendCardAction the {@link SendCardAction} the {@link Player} may execute, may be {@code null}
     * @param receiveCardAction the {@link ReceiveCardAction} the {@link Player} may execute, may be {@code null}
     * @see #initializeSendCardAction(SendCardAction)
     * @see #initializeReceiveCardAction(ReceiveCardAction)
     */
    public void initialize(final SendCardAction sendCardAction, final ReceiveCardAction receiveCardAction) {
        initializeSendCardAction(sendCardAction);
        initializeReceiveCardAction(receiveCardAction);

        if (sendCardActionTab.isDisabled()) {
            tabPane.getSelectionModel().select(receiveCardActionTab);
        }
    }

    /**
     * Initializes the {@link #sendCardActionTab} with the given {@link SendCardAction}.
     * Fills the {@link #sendablePlayerCardsListView} with all player cards that can be sent.
     * Also assigns an action listener to every card within {@link #sendablePlayerCardsListView}
     * to select a target player from {@link #sendingTargetPlayersListView} after clicking on it.
     *
     * @param sendCardAction a {@link SendCardAction} the {@link Player} may execute, may be {@code null}
     * @see #initializeShareKnowledgeAction(ShareKnowledgeAction, Tab, ListView, BiConsumer, Runnable)
     * @see #setPlayerSelectionActionListener(List, ListView, ShareKnowledgeAction)
     */
    private void initializeSendCardAction(final SendCardAction sendCardAction) {
        initializeShareKnowledgeAction(sendCardAction, sendCardActionTab, sendablePlayerCardsListView, (cityCard, players) -> {
            sendCardAction.setTransferredCard(cityCard);
            setPlayerSelectionActionListener(players, sendingTargetPlayersListView, sendCardAction);
        }, () -> clearListView(sendingTargetPlayersListView));
    }

    /**
     * Initializes the {@link #receiveCardActionTab} with the given {@link ReceiveCardAction}.
     * Fills the {@link #receivablePlayerCardsListView} with all player cards that can be received.
     * Also assigns an action listener to every card within {@link #receivablePlayerCardsListView}
     * to either send the {@code receiveCardAction} directly with the only player the card can be received from
     * or select a target player from {@link #receivingTargetPlayersListView} after clicking on it.
     *
     * @param receiveCardAction a {@link ReceiveCardAction} the {@link Player} may execute, may be {@code null}
     * @see #initializeShareKnowledgeAction(ShareKnowledgeAction, Tab, ListView, BiConsumer, Runnable)
     * @see #sendActionAndCloseStage(ShareKnowledgeAction, Player)
     * @see #setPlayerSelectionActionListener(List, ListView, ShareKnowledgeAction)
     */
    private void initializeReceiveCardAction(final ReceiveCardAction receiveCardAction) {
        initializeShareKnowledgeAction(receiveCardAction, receiveCardActionTab, receivablePlayerCardsListView, (cityCard, players) -> {
            receiveCardAction.setTransferredCard(cityCard);
            if (players.size() == 1) {
                sendActionAndCloseStage(receiveCardAction, players.get(0));
            } else {
                setPlayerSelectionActionListener(players, receivingTargetPlayersListView, receiveCardAction);
            }
        }, () -> clearListView(receivingTargetPlayersListView));
    }

    /**
     * Initializes a given {@link ShareKnowledgeAction} and its associated {@link Tab}
     * by filling the {@code cityCardListView} with the available city cards and giving it
     * the {@code cityCardListViewCellSelectionListener} as a listener for the case that a {@link CityCard} is selected and
     * the {@code cityCardListViewCellUnselectionListener} as a listener for the case that nothing is selected.
     *
     * @param shareKnowledgeAction {@link ShareKnowledgeAction} to initialize
     * @param tab {@link Tab} to initialize the {@link ShareKnowledgeAction} in
     * @param cityCardListView {@link ListView} of city cards that are transferable
     * @param cityCardListViewCellSelectionListener function that is executing when a {@link CityCard} is selected in {@code cityCardListView}
     * @param cityCardListViewCellUnselectionListener function that is executing when a {@link CityCard} is unselected in {@code cityCardListView}
     * @see #initializeShareKnowledgeActionCityCardListView(ShareKnowledgeAction, ListView, BiConsumer, Runnable)
     */
    private void initializeShareKnowledgeAction(final ShareKnowledgeAction shareKnowledgeAction,
                                                final Tab tab,
                                                final ListView<CityCard> cityCardListView,
                                                final BiConsumer<CityCard, List<Player>> cityCardListViewCellSelectionListener,
                                                final Runnable cityCardListViewCellUnselectionListener) {
        if (shareKnowledgeAction == null) {
            tab.setDisable(true);
            return;
        }

        tab.setText(shareKnowledgeAction.toString());

        initializeShareKnowledgeActionCityCardListView(
                shareKnowledgeAction,
                cityCardListView,
                cityCardListViewCellSelectionListener,
                cityCardListViewCellUnselectionListener
        );
    }

    /**
     * Initializes a {@code cityCardListView} with the given {@link ShareKnowledgeAction} and
     * binds selection and unselection action listeners to it.
     *
     * @param shareKnowledgeAction {@link ShareKnowledgeAction} containing transferable city cards
     * @param cityCardListView {@link ListView} of city cards that are transferable
     * @param cityCardListViewCellSelectionListener function that is executing when a {@link CityCard} is selected in {@code cityCardListView}
     * @param cityCardListViewCellUnselectionListener function that is executing when a {@link CityCard} is unselected in {@code cityCardListView}
     * @see #invertPlayersAndCityCardsAssociation(Map)
     * @see #setClickListenerToListViewWithItemSelectionCheck(ListView, Consumer, Runnable)
     */
    private void initializeShareKnowledgeActionCityCardListView(final ShareKnowledgeAction shareKnowledgeAction,
                                                                final ListView<CityCard> cityCardListView,
                                                                final BiConsumer<CityCard, List<Player>> cityCardListViewCellSelectionListener,
                                                                final Runnable cityCardListViewCellUnselectionListener) {
        final Map<CityCard, List<Player>> cityCardsAndPlayersAssociation =
                invertPlayersAndCityCardsAssociation(shareKnowledgeAction.getTargetPlayersWithAvailableCardsAssociation());

        Platform.runLater(() -> cityCardListView.setItems(FXCollections.observableArrayList(cityCardsAndPlayersAssociation.keySet())));

        setClickListenerToListViewWithItemSelectionCheck(
                cityCardListView,
                cityCard -> cityCardListViewCellSelectionListener.accept(cityCard, cityCardsAndPlayersAssociation.get(cityCard)),
                cityCardListViewCellUnselectionListener
        );
    }

    /**
     * <p>
     * Inverts the given players and city cards association from a {@link ShareKnowledgeAction}
     * from {@link Player} with {@link List} of {@link CityCard}
     * to {@link CityCard} to {@link List} of {@link Player}.
     * </p>
     *
     * <p>
     * Every unique {@link CityCard} value is taken as a key of a new {@link Map} and
     * every previous key {@link Player} is assigned to those new keys of type {@link CityCard} that it previously held as value.
     * </p>
     *
     * @param playersAndCityCardsAssociation players and city cards association to invert
     * @return {@link Map} of {@link CityCard} with {@link List} of {@link Player}
     */
    private Map<CityCard, List<Player>> invertPlayersAndCityCardsAssociation(final Map<Player, List<CityCard>> playersAndCityCardsAssociation) {
        final Map<CityCard, List<Player>> cityCardsAndPlayersAssociation = new HashMap<>();
        for (final Map.Entry<Player, List<CityCard>> playerWithCityCards : playersAndCityCardsAssociation.entrySet()) {
            for (final CityCard cityCard : playerWithCityCards.getValue()) {
                final List<Player> players = cityCardsAndPlayersAssociation.computeIfAbsent(cityCard, cc -> new LinkedList<>());
                players.add(playerWithCityCards.getKey());
            }
        }
        return cityCardsAndPlayersAssociation;
    }

    /**
     * Sets click listeners to the given {@link ListView},
     * one for the case that an item is selected,
     * one for the case that an item is unselected.
     *
     * @param listView {@link ListView} to assign click listeners to
     * @param selected callback to execute with selected item if one is selected
     * @param unselected callback to execute if an item is unselected
     * @param <T> type of the given {@code listView}
     */
    private <T> void setClickListenerToListViewWithItemSelectionCheck(final ListView<T> listView, final Consumer<T> selected, final Runnable unselected) {
        Platform.runLater(() -> listView.setOnMouseClicked(event -> {
            final T t = listView.getSelectionModel().getSelectedItem();
            if (t != null) {
                selected.accept(t);
            } else {
                unselected.run();
            }
        }));
    }

    /**
     * Sets a {@link List} of {@link Player} to a {@link ListView} and
     * assigns an action listener to send a {@link ShareKnowledgeAction}.
     *
     * @param players {@link List} of {@link Player} to set
     * @param playerListView {@link ListView} to assign {@code players} to
     * @param shareKnowledgeAction {@link ShareKnowledgeAction} to send if a {@link Player} is selected
     * @see #setClickListenerToListViewWithItemSelectionCheck(ListView, Consumer, Runnable)
     */
    private void setPlayerSelectionActionListener(final List<Player> players, final ListView<Player> playerListView, final ShareKnowledgeAction shareKnowledgeAction) {
        playerListView.setItems(FXCollections.observableList(players));
        setClickListenerToListViewWithItemSelectionCheck(
                playerListView,
                player -> sendActionAndCloseStage(shareKnowledgeAction, player),
                () -> {}
        );
    }

    /**
     * Clears the given {@link ListView}
     *
     * @param listView {@link ListView} to clear
     */
    private void clearListView(final ListView<?> listView) {
        Platform.runLater(() -> listView.setItems(FXCollections.emptyObservableList()));
    }

    /**
     * Sends the given {@link ShareKnowledgeAction} to be approved via the {@link #approvableService}
     * after setting the target {@link Player} to given {@code player}.
     * After sending, closes the stage the associated view is opened in.
     *
     * @param shareKnowledgeAction {@link ShareKnowledgeAction} to send
     * @param player target {@link Player} of the {@link ShareKnowledgeAction}
     * @see ShareKnowledgeAction#setTargetPlayer(Player)
     * @see ApprovableService#sendApprovableAction(Approvable)
     */
    private void sendActionAndCloseStage(final ShareKnowledgeAction shareKnowledgeAction, final Player player) {
        shareKnowledgeAction.setTargetPlayer(player);
        approvableService.sendApprovableAction(shareKnowledgeAction);
        closeStage();
    }
}
