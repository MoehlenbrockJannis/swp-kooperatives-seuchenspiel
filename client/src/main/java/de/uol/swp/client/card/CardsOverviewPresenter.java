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
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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
    protected VBox stackVBox;
    @FXML
    protected HBox drawStackHBox;
    @FXML
    protected HBox discardStackHBox;
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
        this.drawStackHBox.setOnMouseClicked(mouseEvent -> drawCard());
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
        stackVBox.prefWidthProperty().bind(parent.widthProperty());
        stackVBox.prefHeightProperty().bind(parent.heightProperty());
        stackVBox.setStyle(parent.getStyle());
        this.drawStackHBox.setDisable(true);
        this.discardStackHBox.setDisable(true);
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
     * Reduces the number of cards to draw and disables the draw stack if no cards are left to draw.
     *
     * <p>
     * This method decrements the number of cards to draw by one. If the number of cards to draw
     * reaches zero, it disables the first child of the stack VBox.
     * </p>
     */
    protected void reduceNumberOfCardsToDraw() {
        this.numberOfCardsToDraw--;
        if (this.numberOfCardsToDraw == 0) {
            this.drawStackHBox.setDisable(true);
        }
    }
    protected void reduceNumberOfCardsToDiscard() {
        this.numberOfCardsToDiscard--;
        if (this.numberOfCardsToDiscard == 0) {
            this.discardStackHBox.setDisable(true);
        }
    }

    /**
     * Subscribes to a {@link RetrieveUpdatedGameServerMessage} on the {@link #eventBus} and
     * disables the {@link #drawStackHBox} if no cards need to be drawn.
     *
     * @param retrieveUpdatedGameServerMessage the {@link RetrieveUpdatedGameServerMessage} on the {@link #eventBus}
     * @see #isGameInCorrectDrawPhase()
     */
    @Subscribe
    public void onRetrieveUpdatedGameServerMessage(final RetrieveUpdatedGameServerMessage retrieveUpdatedGameServerMessage) {
        if (!isGameInCorrectDrawPhase()) {
            this.drawStackHBox.setDisable(true);
        }
    }
}
