package de.uol.swp.client.game;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.action.ActionService;
import de.uol.swp.common.action.advanced.discover_antidote.DiscoverAntidoteAction;
import de.uol.swp.common.card.CityCard;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.plague.Plague;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Setter;

import java.util.List;

/**
 * Presenter for selecting city cards required for antidote research.
 * Allows players to choose the required number of city cards matching
 * the plague type for discovering an antidote.
 *
 * @author Marvin Tischer
 * @since 2025-02-05
 */
public class SelectCityCardsForAntidoteResearchPresenter extends AbstractPresenter {

    @FXML
    private ListView<CityCard> listView;

    @FXML
    private Button confirmButton;

    @FXML
    private Button cancelButton;

    private ObservableList<CityCard> selectedItems = FXCollections.observableArrayList();

    private DiscoverAntidoteAction discoverAntidoteAction;

    @Setter(onMethod_ = @Inject)
    private ActionService actionService;

    private int requiredCardsForDiscoveringAntidote;

    /**
     * Initializes the UI components for the view.
     *
     * @author Marvin Tischer
     * @since 2025-02-05
     */
    @FXML
    public void initialize() {
        setupListView();
        setupButtons();
        Platform.runLater(this::updateConfirmButtonState);
    }

    /**
     * Sets up the ListView for selecting multiple {@link CityCard}s.
     *
     * @author Marvin Tischer
     * @since 2025-02-17
     */
    private void setupListView() {
        MultipleSelectionModel<CityCard> selectionModel = listView.getSelectionModel();

        selectionModel.setSelectionMode(SelectionMode.MULTIPLE);

        ObservableList<CityCard> selectedItems = selectionModel.getSelectedItems();

        selectedItems.addListener((ListChangeListener<? super CityCard>) change -> updateConfirmButtonState());
    }

    /**
     * Sets up the action handlers for the confirm and cancel buttons..
     *
     * @author Marvin Tischer
     * @since 2025-02-17
     */
    private void setupButtons() {
        confirmButton.setOnAction(event -> handleConfirmButton());
        cancelButton.setOnAction(event -> handleCancelButton());
        confirmButton.setDisable(true);
    }

    /**
     * Updates the state of the confirm button based on the number of selected city cards.
     *
     * @author Marvin Tischer
     * @since 2025-02-17
     */
    private void updateConfirmButtonState() {
        confirmButton.setDisable(listView.getSelectionModel().getSelectedItems().size() != requiredCardsForDiscoveringAntidote);
    }

    /**
     * Handles the confirm button action by validating the number of selected items.
     *
     * @author Marvin Tischer
     * @since 2025-02-07
     */
    private void handleConfirmButton() {
        selectedItems = listView.getSelectionModel().getSelectedItems();
        closeStage(confirmButton);

    }

    /**
     * Handles the cancel button action by clearing the selected items
     * and closing the current window.
     *
     * @author Marvin Tischer
     * @since 2025-02-07
     */
    private void handleCancelButton() {
        selectedItems.clear();
        closeStage(cancelButton);
    }

    /**
     * Closes the stage (window) associated with the given button.
     *
     * @param button The button whose stage should be closed.
     * @author Marvin Tischer
     * @since 2025-02-07
     */
    private void closeStage(Button button) {
        Scene buttonScene = button.getScene();
        Stage stage = (Stage) buttonScene.getWindow();
        stage.close();
    }

    /**
     * Initializes the presenter with the given action and plague.
     * Filters the player's hand to show only valid city cards.
     *
     * @param action The antidote discovery action.
     * @param plague The plague for which the antidote is being researched.
     *
     * @author Marvin Tischer
     * @since 2025-02-05
     */
    public void initialize(DiscoverAntidoteAction action, Plague plague) {
        this.discoverAntidoteAction = action;
        this.discoverAntidoteAction.setPlague(plague);
        this.requiredCardsForDiscoveringAntidote = discoverAntidoteAction.getRequiredAmountOfDiscardedCards();

        List<PlayerCard> handCardsFromExecutingPlayer = action.getExecutingPlayer().getHandCards();

        List<CityCard> cityCardsWithPlague = handCardsFromExecutingPlayer.stream()
                .filter(card -> isCityCardWithPlague(card, plague))
                .map(CityCard.class::cast)
                .toList();

        setHandCards(cityCardsWithPlague);
    }

    /**
     * Checks if the given player card is a {@code CityCard} and if it has the specified plague.
     *
     * @param handCard The player card to check.
     * @param plague The plague to check for.
     * @return {@code true} if the card is a {@code CityCard} and has the specified plague, otherwise {@code false}.
     * @author Marvin
     * @since 2025-02-09
     */
    private boolean isCityCardWithPlague(PlayerCard handCard, Plague plague) {
        return handCard instanceof CityCard cityCard && cityCard.hasPlague(plague);
    }

    /**
     * Sends the action after selecting city cards and disables the antidote button.
     *
     * @author Marvin Tischer
     * @since 2025-02-05
     */
    public void sendDiscoverAntidoteAction() {
        for (CityCard cityCard : selectedItems) {
            discoverAntidoteAction.addDiscardedCard(cityCard);
        }
        actionService.sendAction(this.discoverAntidoteAction.getGame(), this.discoverAntidoteAction);
    }

    /**
     * Updates the list view with the given city cards.
     *
     * @param handCards The list of city cards that can be selected.
     *
     * @author Marvin Tischer
     * @since 2025-02-05
     */
    public void setHandCards(List<CityCard> handCards) {
        ObservableList<CityCard> listViewItems = this.listView.getItems();
        listViewItems.clear();
        listView.getItems().addAll(handCards);
    }

    @Override
    public String getFXMLFilePath() {
        return "/fxml/game/SelectCityCardsForAntidoteResearch.fxml";
    }
}
