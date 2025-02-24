package de.uol.swp.client.card;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.common.card.Card;
import de.uol.swp.common.card.stack.CardStack;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.server_message.RetrieveUpdatedGameServerMessage;
import de.uol.swp.common.player.Player;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Abstract presenter class for handling the overview of cards.
 * Provides common functionality for managing card stacks and user interactions.
 */
public abstract class CardsOverviewPresenter extends AbstractPresenter {

    /**
     * Creates and initializes the presenter.
     *
     * @param presenter the presenter to be initialized
     */
    private static void createPresenter(CardsOverviewPresenter presenter) {
        initializePresenter(presenter,true);
    }

    @FXML
    protected Label drawStackNumberOfCardsLabel;
    @FXML
    protected Label discardStackNumberOfCardsLabel;
    @FXML
    @Getter
    protected HBox stackHBox;
    @FXML
    protected VBox labelVBox;
    @FXML
    protected StackPane cardIcon;
    protected Tooltip drawStackTooltip;
    protected Tooltip discardStackTooltip;
    protected final CardService cardService;
    @Inject
    protected LoggedInUserProvider loggedInUserProvider;
    protected Supplier<Game> gameSupplier;
    protected Function<Game, CardStack<? extends Card>> drawCardStackFunction;
    protected Function<Game, CardStack<? extends Card>> discardCardStackFunction;
    @Setter
    protected Stage window;
    protected Pane parent;

    protected int numberOfCardsToDraw;
    protected int numberOfCardsToDiscard;

    /**
     * Returns the file path of the FXML file.
     *
     * @return the FXML file path
     */
    @Override
    public String getFXMLFilePath() {
        return "/fxml/game/CardOverviewComponent.fxml";
    }

    /**
     * Constructor for CardsOverviewPresenter.
     *
     * @param service  the CardService to be used for card operations
     * @param eventBus the EventBus for handling events
     */
    protected CardsOverviewPresenter(CardService service, EventBus eventBus) {
        this.cardService = service;
        this.eventBus = eventBus;
        createPresenter(this);
    }

    /**
     * Initializes the presenter with the given parameters.
     *
     * @param gameSupplier       the supplier for the current game
     * @param drawStackFunction  the function to get the draw card stack
     * @param discardStackFunction the function to get the discard card stack
     * @param parent             the parent pane
     */
    public void initialize(
            Supplier<Game> gameSupplier,
            Function<Game, CardStack<? extends Card>> drawStackFunction,
            Function<Game, CardStack<? extends Card>> discardStackFunction,
            Pane parent
    ) {
        this.gameSupplier = gameSupplier;
        this.drawCardStackFunction = drawStackFunction;
        this.discardCardStackFunction = discardStackFunction;
        this.parent = parent;
        this.drawStackNumberOfCardsLabel.setOnMouseClicked(mouseEvent -> drawCard());
        updateLabels();
        setupDesign();
    }

    /**
     * Abstract method to draw a card.
     */
    abstract void drawCard();

    /**
     * Abstract method to discard a card.
     */
    abstract void discardCard();

    /**
     * Returns whether the {@link Game} is in the correct phase to draw a {@link Card}
     *
     * @return {@code true} if {@link Game} is in correct phase, {@code false} otherwise
     */
    protected abstract boolean isGameInCorrectDrawPhase();

    /**
     * Updates the labels for the number of cards in the draw and discard stacks.
     */
    public void updateLabels() {
        Platform.runLater(() -> {
            Game game = this.gameSupplier.get();
            drawStackNumberOfCardsLabel.setText(String.valueOf(this.drawCardStackFunction.apply(game).size()));
            discardStackNumberOfCardsLabel.setText(String.valueOf(this.discardCardStackFunction.apply(game).size()));
        });
    }

    /**
     * Sets up the design of the card overview component.
     */
    private void setupDesign() {
        this.stackHBox.setStyle(parent.getStyle());
        Platform.runLater(this::bindSizes);
        setupDesignForLabels();
    }

    /**
     * Sets up the labels for the card overview component.
     */
    private void setupDesignForLabels() {
        this.drawStackTooltip = new Tooltip();
        this.discardStackTooltip = new Tooltip();
        Tooltip.install(this.drawStackNumberOfCardsLabel, this.drawStackTooltip);
        Tooltip.install(this.discardStackNumberOfCardsLabel, this.discardStackTooltip);
        this.drawStackNumberOfCardsLabel.setDisable(true);
        this.discardStackNumberOfCardsLabel.setDisable(true);

    }

    /**
     * Binds the sizes of the components in the card overview component.
     *
     * <p>
     * This method binds the preferred width and height properties of the stackHBox,
     * cardIcon, drawStackNumberOfCardsLabel, and discardStackNumberOfCardsLabel
     * to the corresponding properties of their parent components.
     * </p>
     */
    private void bindSizes() {
        int stackHBoxChildrenSize = this.stackHBox.getChildren().size();
        int labelVBoxChildrenSize = this.labelVBox.getChildren().size();
        DoubleProperty stackHBoxWidth = this.stackHBox.prefWidthProperty();
        DoubleProperty stackHBoxHeight = this.stackHBox.prefHeightProperty();

        this.stackHBox.prefWidthProperty().bind(this.parent.widthProperty());
        this.stackHBox.prefHeightProperty().bind(this.parent.heightProperty());

        this.cardIcon.prefWidthProperty().bind(stackHBoxWidth.divide(stackHBoxChildrenSize));
        this.cardIcon.prefHeightProperty().bind(stackHBoxHeight);

        this.drawStackNumberOfCardsLabel.prefWidthProperty().bind(stackHBoxWidth.divide(stackHBoxChildrenSize));
        this.drawStackNumberOfCardsLabel.prefHeightProperty().bind(stackHBoxHeight.divide(stackHBoxChildrenSize));

        this.discardStackNumberOfCardsLabel.prefWidthProperty().bind(stackHBoxWidth.divide(labelVBoxChildrenSize));
        this.discardStackNumberOfCardsLabel.prefHeightProperty().bind(stackHBoxHeight.divide(labelVBoxChildrenSize));
    }

    /**
     * Creates a rectangle icon for the CardStack with the specified color and adds it to the given StackPane.
     *
     * @param color     the color of the rectangle
     * @param stackPane the StackPane to which the rectangle will be added
     */
    protected void createCardStackIcon(Color color, StackPane stackPane) {
        Rectangle rectangle = new CardIcon(color, stackPane);
        stackPane.getChildren().add(rectangle);
    }

    /**
     * Creates a dialog for discarding a card from the player's hand.
     *
     * <p>
     * This method creates a dialog that displays the player's hand cards in a list view.
     * The player can select a card to discard and confirm the action by clicking the "Ablegen" button.
     * </p>
     *
     * @param currentPlayer the player whose hand cards are to be displayed in the dialog
     * @return a ListView containing the cards in the player's hand
     */
    protected Card createDiscardDialog(Player currentPlayer) {
        DiscardCardDialog<? extends Card> discardCardDialog = new DiscardCardDialog<>(currentPlayer.getHandCards(), "Ablegen");
        return discardCardDialog.showAndWait().orElse(null);
    }

    /**
     * Handles the display of a card popup.
     *
     * @param gameID the ID of the game
     * @param card   the card to be displayed in the popup
     */
    protected void handleCardPopup(int gameID, Card card) {
        if (this.gameSupplier.get().getId() == gameID) {
            Platform.runLater(() -> {
                CardPopup cardPopup = new CardPopup(card, window);
                cardPopup.generatePopup();
            });
        }

    }

    /**
     * Reduces the number of cards to draw by one.
     * <p>
     * This method decrements the number of cards to draw and disables the draw stack label
     * if no more cards need to be drawn.
     * </p>
     */
    protected void reduceNumberOfCardsToDraw() {
        this.numberOfCardsToDraw--;
        if (this.numberOfCardsToDraw == 0) {
            this.drawStackNumberOfCardsLabel.setDisable(true);
        }
    }

    /**
     * Reduces the number of cards to discard by one.
     * <p>
     * This method decrements the number of cards to discard and disables the discard stack label
     * if no more cards need to be discarded.
     * </p>
     */
    protected void reduceNumberOfCardsToDiscard() {
        this.numberOfCardsToDiscard--;
        if (this.numberOfCardsToDiscard == 0) {
            this.discardStackNumberOfCardsLabel.setDisable(true);
        }
    }

    /**
     * Subscribes to a {@link RetrieveUpdatedGameServerMessage} on the {@link #eventBus} and
     * disables the {@link #drawStackNumberOfCardsLabel} if no cards need to be drawn.
     *
     * @param retrieveUpdatedGameServerMessage the {@link RetrieveUpdatedGameServerMessage} on the {@link #eventBus}
     * @see #isGameInCorrectDrawPhase()
     */
    @Subscribe
    public void onRetrieveUpdatedGameServerMessage(final RetrieveUpdatedGameServerMessage retrieveUpdatedGameServerMessage) {
        if (!isGameInCorrectDrawPhase()) {
            this.drawStackNumberOfCardsLabel.setDisable(true);
        }
    }
}
