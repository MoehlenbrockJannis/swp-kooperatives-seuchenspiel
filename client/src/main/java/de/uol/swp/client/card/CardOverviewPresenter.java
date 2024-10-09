package de.uol.swp.client.card;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.game.GamePresenter;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.common.card.Card;
import de.uol.swp.common.card.InfectionCard;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.card.response.DrawPlayerCardResponse;
import de.uol.swp.common.card.server_message.DrawInfectionCardServerMessage;
import de.uol.swp.common.card.stack.CardStack;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.player.Player;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Setter;
import org.apache.logging.log4j.util.TriConsumer;
import org.greenrobot.eventbus.Subscribe;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;


public class CardOverviewPresenter extends AbstractPresenter {

    @FXML
    private Label drawStackNumberOfCardsLabel;
    @FXML
    private Label discardStackNumberOfCardsLabel;
    @FXML
    private VBox stackVBox;
    private Supplier<Game> gameSupplier;
    @Inject
    private CardService cardService;
    private Function<Game, CardStack<? extends Card>> drawCardStackFunction;
    private Function<Game, CardStack<? extends Card>> discardCardStackFunction;
    @Inject
    private LoggedInUserProvider loggedInUserProvider;
    @Setter
    private Stage window;
    private Pane parent;
    private BiConsumer<Player, CardService> drawCardConsumer;
    private TriConsumer<Player, CardService, Card> discardCardConsumer;

    /**
     * <p>
     *     Return {@value #DEFAULT_FXML_FOLDER_PATH}+{@value GamePresenter#GAME_FXML_FOLDER_PATH}
     * </p>
     *
     * {@inheritDoc}
     */
    @Override
    public String getFXMLFolderPath() {
        return DEFAULT_FXML_FOLDER_PATH + GamePresenter.GAME_FXML_FOLDER_PATH;
    }

    @Override
    public String getFXMLFilePath() {
        return "/fxml/game/CardOverviewComponent.fxml";
    }

    public void initialize(
            Supplier<Game> gameSupplier,
            Function<Game, CardStack<? extends Card>> drawStackFunction,
            Function<Game, CardStack<? extends Card>> discardStackFunction,
            Pane parent,
            BiConsumer<Player, CardService> drawCardConsumer,
            TriConsumer<Player, CardService, Card> discardCardConsumer
    ) {
        this.gameSupplier = gameSupplier;
        this.drawCardStackFunction = drawStackFunction;
        this.discardCardStackFunction = discardStackFunction;
        this.parent = parent;
        this.drawCardConsumer = drawCardConsumer;
        this.discardCardConsumer = discardCardConsumer;
        updateLabels();
        setupDesign();
    }

    /**
     * Draws a card from the draw stack.
     *
     * <p>
     * This method determines the type of cards in the draw stack and sends a request to draw a player card
     * if all cards are of type {@link PlayerCard}. If all cards are of type {@link InfectionCard}, it prints
     * a message indicating that an infection card should be drawn.
     * </p>
     *
     * @see PlayerCard
     * @see InfectionCard
     *
     */
    @FXML
    public void drawCard() {
        Player currentPlayer = this.gameSupplier.get().getLobby().getPlayerForUser(loggedInUserProvider.get());
        this.drawCardConsumer.accept(currentPlayer, cardService);
    }

    /**
     * Discards a card from the discard stack.
     *
     * <p>
     * This method is intended to handle the logic for discarding a card from the discard stack.
     * </p>
     */
    @FXML
    public void discardCard() {
        Player currentPlayer = this.gameSupplier.get().getLobby().getPlayerForUser(loggedInUserProvider.get());
        Card selectedCard = createDiscardDialog(currentPlayer).getSelectionModel().getSelectedItem();
        if (selectedCard != null) {
            this.discardCardConsumer.accept(currentPlayer, cardService, selectedCard);
        }
    }

    /**
     * Updates the labels for the draw and discard stacks.
     *
     * <p>
     * This method updates the text of the labels that display the number of cards
     * in the draw and discard stacks. It runs the update on the JavaFX Application Thread
     * using {@link Platform#runLater(Runnable)} to ensure thread safety.
     * </p>
     *
     */
    public void updateLabels() {
        Platform.runLater(() -> {
            Game game = this.gameSupplier.get();
            drawStackNumberOfCardsLabel.setText(String.valueOf(this.drawCardStackFunction.apply(game).size()));
            discardStackNumberOfCardsLabel.setText(String.valueOf(this.discardCardStackFunction.apply(game).size()));
        });
    }

    /**
     * Handles the response when a player card is drawn.
     *
     * <p>
     * This method is called when a {@link DrawPlayerCardResponse} is received. It creates a new {@link CardPopup}
     * to display the drawn player card and generates the popup on the JavaFX Application Thread using
     * {@link Platform#runLater(Runnable)} to ensure thread safety.
     * </p>
     *
     * @param drawPlayerCardResponse the response containing the drawn player card
     */
    @Subscribe
    public void onPlayerCardDrawnResponse(DrawPlayerCardResponse drawPlayerCardResponse) {
        handleCardPopup(drawPlayerCardResponse.getGame().getId(), drawPlayerCardResponse.getPlayerCard());
    }

    @Subscribe
    public void onDrawInfectionCardServerMessage(DrawInfectionCardServerMessage drawInfectionCardServerMessage) {
        handleCardPopup(drawInfectionCardServerMessage.getGame().getId(), drawInfectionCardServerMessage.getInfectionCard());
    }

    /**
     * Sets up the design of the stack VBox.
     *
     * <p>
     * This method binds the width and height properties of the stack VBox to the parent pane's
     * width and height properties. It also sets the style of the stack VBox to match the parent pane's style.
     * </p>
     */
    private void setupDesign() {
        stackVBox.prefWidthProperty().bind(parent.widthProperty());
        stackVBox.prefHeightProperty().bind(parent.heightProperty());
        stackVBox.setStyle(parent.getStyle());
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
    private ListView<? extends Card> createDiscardDialog(Player currentPlayer) {
        Dialog<Card> dialog = new Dialog<>();
        ListView<Card> cardListView = new ListView<>();
        cardListView.getItems().addAll(currentPlayer.getHandCards());
        dialog.getDialogPane().setContent(cardListView);
        ButtonType loginButtonType = new ButtonType("Ablegen", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType);
        dialog.showAndWait();
        return cardListView;
    }

    private void handleCardPopup(int gameID, Card card) {
        if (this.gameSupplier.get().getId() == gameID) {
            Platform.runLater(() -> {
                CardPopup cardPopup = new CardPopup(card, window);
                cardPopup.generatePopup();
            });
        }

    }
}
