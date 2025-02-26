package de.uol.swp.client.card;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.common.card.InfectionCard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.List;

public class SortInfectionCardsDialogPresenter extends AbstractPresenter {

    @FXML
    private ListView<InfectionCard> listView;

    private ObservableList<InfectionCard> items;

    private boolean confirmedSelection = false;

    @FXML
    public void initialize() {
        items = FXCollections.observableArrayList();
        listView.setItems(items);
        listView.setCellFactory(listView -> createDragAndDropCell());
    }

    /**
     * Creates a {@link ListCell} for infection cards with drag-and-drop functionality.
     *
     * @return A new {@link ListCell} configured for drag-and-drop operations.
     *
     * @author Marvin Tischer
     * @since 2025-02-17
     */
    private ListCell<InfectionCard> createDragAndDropCell() {
        ListCell<InfectionCard> cell = new ListCell<>();
        cell.itemProperty().addListener(
                (observableItem, previousCard, currentCard) ->
                        updateCellText(cell, currentCard));

        cell.setOnDragDetected(event -> startDrag(event, cell));
        cell.setOnDragOver(event -> acceptDrag(event, cell));
        cell.setOnDragDropped(event -> handleDrop(event, cell));

        return cell;
    }

    /**
     * Updates the text of a given cell in the list view based on the provided infection card.
     * If the item is {@code null}, the cell text is cleared; otherwise, it displays the card's title.
     *
     * @param cell The list cell to update.
     * @param item The infection card whose title should be displayed in the cell.
     *
     * @author Marvin Tischer
     * @since 2025-02-17
     */
    private void updateCellText(ListCell<InfectionCard> cell, InfectionCard item) {
        cell.setText(item == null ? null : item.getTitle());
    }

    /**
     * Initiates a drag-and-drop operation for an infection card from the given list cell.
     *
     * @param event The mouse event that triggered the drag action.
     * @param cell  The list cell containing the infection card to be dragged.
     *
     * @author Marvin Tischer
     * @since 2025-02-17
     */
    private void startDrag(javafx.scene.input.MouseEvent event, ListCell<InfectionCard> cell) {
        if (cell.getItem() == null) return;

        Dragboard dragboard = cell.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();
        InfectionCard cellItem = cell.getItem();
        content.putString(cellItem.toString());
        dragboard.setContent(content);
        event.consume();
    }

    /**
     * Handles the acceptance of a drag-and-drop operation on a list cell.
     *
     * @param event The drag event that triggered the acceptance check.
     * @param cell  The list cell where the item may be dropped.
     *
     * @author Marvin Tischer
     * @since 2025-02-17
     */
    private void acceptDrag(javafx.scene.input.DragEvent event, ListCell<InfectionCard> cell) {
        Object gestureSource = event.getGestureSource();
        Dragboard dragboard = event.getDragboard();
        if (gestureSource != cell && dragboard.hasString()) {
            event.acceptTransferModes(TransferMode.MOVE);
        }
        event.consume();
    }

    /**
     * Handles the drop event for a dragged infection card onto a list cell.
     *
     * @param event The drag event that triggered the drop action.
     * @param cell  The list cell where the item is dropped.
     *
     * @author Marvin Tischer
     * @since 2025-02-17
     */
    private void handleDrop(javafx.scene.input.DragEvent event, ListCell<InfectionCard> cell) {
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasString()) {
            String draggedItemString = dragboard.getString();
            items.stream()
                    .filter(card -> card.toString().equals(draggedItemString))
                    .findFirst()
                    .ifPresent(draggedItem -> swapItems(draggedItem, cell));

            event.setDropCompleted(true);
            event.consume();
        }
    }

    /**
     * Swaps the positions of the dragged infection card and the target card within the list.
     *
     * @param draggedItem The infection card that was dragged.
     * @param cell        The list cell where the dragged item is dropped.
     *
     * @author Marvin Tischer
     * @since 2025-02-17
     */
    private void swapItems(InfectionCard draggedItem, ListCell<InfectionCard> cell) {
        InfectionCard targetItem = cell.getItem();
        int draggedIndex = items.indexOf(draggedItem);
        int targetIndex = items.indexOf(targetItem);

        if (draggedIndex >= 0 && targetIndex >= 0 && draggedIndex != targetIndex) {
            items.remove(draggedItem);
            items.add(targetIndex, draggedItem);
            listView.getSelectionModel().select(targetIndex);
        }
    }

    /**
     * Initializes the list of infection cards by setting up an observable list
     * and assigning it to the list view if available.
     *
     * @param cardStack The initial stack of infection cards to be displayed.
     *
     * @author Marvin Tischer
     * @since 2025-02-17
     */
    public void initialize(List<InfectionCard> cardStack) {
        this.items = FXCollections.observableArrayList(cardStack);
        if (listView != null) {
            listView.setItems(items);
        }
    }

    /**
     * Confirms the selection and closes the current stage.
     *
     * @author Marvin Tischer
     * @since 2025-02-17
     */
    @FXML
    private void confirmSelectedInfectionCards() {
        confirmedSelection = true;

        Scene scene = listView.getScene();
        Window window = scene.getWindow();

        Stage stage = (Stage) window;
        stage.close();
    }

    /**
     * Returns whether the action has been confirmed.
     *
     * @return {@code true} if the action was confirmed, {@code false} otherwise.
     *
     * @author Marvin Tischer
     * @since 2025-02-17
     */
    public boolean wasConfirmed() {
        return confirmedSelection;
    }

    /**
     * Returns a copy of the current list of infection cards.
     *
     * @return A new {@link List} containing the infection cards.
     *
     * @author Marvin Tischer
     * @since 2025-02-17
     */
    public List<InfectionCard> getUpdatedList() {
        return new ArrayList<>(items);
    }

    @Override
    public String getFXMLFilePath() {
        return "/fxml/card/SortInfectionCardsDialog.fxml";
    }
}
