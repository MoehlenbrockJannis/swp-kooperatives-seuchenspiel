package de.uol.swp.client.card;

import de.uol.swp.common.card.Card;
import javafx.scene.control.*;

import java.util.List;

/**
 * Dialog for discarding a card from a card stack.
 *
 * @param <T> the type of card that can be discarded
 */
public  class DiscardCardDialog<T extends Card> extends Dialog<T> {

    public static final double PREF_WIDTH = 350.00;
    private final List<T> cardStack;

    private final ListView<T> cardListView = new ListView<>();
    private final ButtonType button;
    private final String description;

    /**
     * Constructs a new DiscardCardDialog with the given card stack and button title.
     *
     * @param cardStack the card stack to discard a card from
     * @param titleButton the title of the button
     */
    public DiscardCardDialog(List<T> cardStack, String titleButton) {
        this(cardStack, titleButton, null);
    }

    public DiscardCardDialog(List<T> cardStack, String titleButton, String description) {
        super();
        this.cardStack = cardStack;
        this.button = new ButtonType(titleButton, ButtonBar.ButtonData.OK_DONE);
        this.description = description;
        generateDialog();
    }

    /**
     * Generates the dialog with the card stack and button.
     */
    private void generateDialog() {
        addCardsToListView();
        getDialogPane().setPrefWidth(PREF_WIDTH);
        setHeader();
        getDialogPane().setContent(cardListView);
        getDialogPane().getButtonTypes().add(button);
        setResult();

    }

    /**
     * Adds the cards from the card stack to the list view.
     */
    private void addCardsToListView() {
        cardListView.getItems().addAll(cardStack);
    }

    /**
     * Sets the result of the dialog to the selected card.
     */
    private void setResult() {
        setResultConverter(dialogButton -> {
            if (dialogButton == button) {
                return cardListView.getSelectionModel().getSelectedItem();
            }
            return null;
        });
    }

    /**
     * Sets the header of the dialog.
     */
    private void setHeader() {
        Label label = new Label(description);
        label.setWrapText(true);
        label.setPrefWidth(getDialogPane().getPrefWidth());
        getDialogPane().setHeader(label);
    }
}


