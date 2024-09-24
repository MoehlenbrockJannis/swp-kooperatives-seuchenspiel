package de.uol.swp.client.game;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.card.CardPopup;
import de.uol.swp.client.card.CardService;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.common.card.Card;
import de.uol.swp.common.card.InfectionCard;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.card.response.DrawPlayerCardResponse;
import de.uol.swp.common.card.stack.CardStack;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.server_message.RetrieveUpdatedGameMessage;
import de.uol.swp.common.player.Player;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Setter;
import org.greenrobot.eventbus.Subscribe;


public class CardOverviewPresenter extends AbstractPresenter {

    @FXML
    private Label drawStackNumberOfCardsLabel;
    @FXML
    private Label discardStackNumberOfCardsLabel;
    @FXML
    private VBox stackVBox;
    private Game game;
    @Inject
    private CardService cardService;
    private CardStack<? extends Card> drawCardStack;
    private CardStack<? extends Card> discardCardStack;
    @Inject
    private LoggedInUserProvider loggedInUserProvider;
    @Setter
    private Stage window;
    private Pane parent;

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

    public void initialize(Game game, CardStack<? extends Card> drawStack, CardStack<? extends Card> discardStack, Pane parent) {
        this.game = game;
        this.drawCardStack = drawStack;
        this.discardCardStack = discardStack;
        this.parent = parent;
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
        Player currentPlayer = game.getLobby().getPlayerForUser(loggedInUserProvider.get());
        if(drawCardStack.stream().allMatch(PlayerCard.class::isInstance)){
            cardService.sendDrawPlayerCardRequest(game, currentPlayer);
        }
        else if(drawCardStack.stream().allMatch(InfectionCard.class::isInstance)){
            //TODO: Implement draw infection card
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
    private void updateLabels() {
        Platform.runLater(() -> {
            drawStackNumberOfCardsLabel.setText(String.valueOf(this.drawCardStack.size()));
            discardStackNumberOfCardsLabel.setText(String.valueOf(this.discardCardStack.size()));
        });
    }

    /**
     * Handles the reception of an updated game message.
     *
     * <p>
     * This method is called when a {@link RetrieveUpdatedGameMessage} is received. It updates the
     * current game state, draw card stack, and discard card stack with the information from the message.
     * It also updates the labels to reflect the new state.
     * </p>
     *
     * @param message the message containing the updated game information
     */
    @Subscribe
    public void onReceiveUpdatedGameMessage(RetrieveUpdatedGameMessage message) {
        this.game = message.getGame();
        this.drawCardStack = message.getGame().getPlayerDrawStack();
        this.discardCardStack = message.getGame().getPlayerDiscardStack();
        updateLabels();
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
        Platform.runLater(() -> {
            CardPopup cardPopup = new CardPopup(drawPlayerCardResponse.getPlayerCard(), window);
            cardPopup.generatePopup();
        });

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


}
