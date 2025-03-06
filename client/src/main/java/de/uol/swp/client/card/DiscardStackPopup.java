package de.uol.swp.client.card;

import de.uol.swp.common.card.Card;
import de.uol.swp.common.card.stack.CardStack;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import javafx.stage.Window;

/**
 * A popup that displays a stack of discarded cards.
 *
 * @param <T> the type of cards in the discard stack
 */
public class DiscardStackPopup<T extends Card> extends Popup {
    public static final double CELL_SIZE = 24.00;
    public static final int DEFAULT_CELL_NUMBER = 8;
    public static final String PLACEHOLDER_STRING = "Keine Karten verfügbar";
    public static final double CELL_OFFSET = 1.02;

    private CardStack<T> discardStack;
    private final Pane root;
    private ListView<T> cardListView;

    /**
     * Constructs a DiscardStackPopup with the specified discard stack and root pane.
     *
     * @param discardStack the stack of cards to be displayed
     * @param root the root pane to which the popup is relative
     */
    public DiscardStackPopup(CardStack<T> discardStack, Pane root) {
        this.discardStack = discardStack;
        this.root = root;
        createListView();
        addCards();
        setPosition();
    }

    /**
     * Creates and configures the ListView for displaying cards.
     */
    private void createListView() {
        this.cardListView = new ListView<>();
        Label label = new Label(PLACEHOLDER_STRING);

        this.cardListView.setPlaceholder(label);
        this.cardListView.prefWidthProperty().bind(root.widthProperty());
        this.cardListView.setFocusTraversable(false);
        setCellSize();
        setPrefHeight();

        this.getContent().add(cardListView);
    }

    /**
     * Updates the discard stack and refreshes the card list view.
     *
     * @param cardStack the new discard stack
     */
    public void updateCards(CardStack<? extends Card> cardStack) {
        this.discardStack = (CardStack<T>) cardStack;
        this.cardListView.getItems().clear();
        setPrefHeight();
        setPosition();
        addCards();
    }

    /**
     * Sets the preferred height of the card list view.
     * <p>
     * This method adjusts the preferred height of the cardListView based on the size of the discard stack.
     * If the discard stack is not empty, the height is set to the number of cards multiplied by the cell size and offset.
     * If the discard stack is empty, the height is set to a default value.
     * </p>
     */
    private void setPrefHeight() {
        if (!discardStack.isEmpty()) {
            cardListView.setPrefHeight(discardStack.size() * (CELL_SIZE * CELL_OFFSET));
        }else {
            cardListView.setPrefHeight(DEFAULT_CELL_NUMBER * CELL_SIZE);
        }
    }

    /**
     * Adds all cards from the discard stack to the card list view.
     */
    private void addCards() {
        this.cardListView.getItems().addAll(discardStack);
    }

    /**
     * Sets the position of the popup relative to the root pane.
     * This method calculates the position of the popup based on the window's
     * layout coordinates and the root pane's layout and preferred width.
     */
    private void setPosition() {
        Window window = root.getScene().getWindow();
        double windowLayoutX = window.getX();
        double windowLayoutY = window.getY();

        this.setX(windowLayoutX + root.getLayoutX() + root.getPrefWidth());
        this.setY(windowLayoutY + root.getLayoutY() - (cardListView.getPrefHeight()));
    }

    /**
     * Sets the size of the cells in the ListView.
     */
    private void setCellSize() {
        this.cardListView.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item.toString());
                    }
                    setPrefHeight(CELL_SIZE);
                }
            }
        );
    }
}
