package de.uol.swp.client.card;

import com.google.inject.Inject;
import de.uol.swp.common.card.Card;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.card.response.DrawPlayerCardResponse;
import de.uol.swp.common.card.response.ReleaseToDiscardPlayerCardResponse;
import de.uol.swp.common.card.response.ReleaseToDrawPlayerCardResponse;
import de.uol.swp.common.card.stack.CardStack;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.player.Player;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Presenter class for handling the overview of player cards.
 */
public class PlayerCardsOverviewPresenter extends CardsOverviewPresenter {
    public static final Color ICON_COLOR = Color.TEAL;

    /**
     * Constructor for PlayerCardsOverviewPresenter.
     *
     * @param cardService the CardService to be used for card operations
     * @param eventBus    the EventBus for handling events
     */
    @Inject
    public PlayerCardsOverviewPresenter(CardService cardService, EventBus eventBus){
        super(cardService, eventBus);
    }

    /**
     * Initializes the presenter with the given parameters.
     *
     * @param gameSupplier        the supplier for the current game
     * @param drawStackFunction   the function to get the draw card stack
     * @param discardStackFunction the function to get the discard card stack
     * @param parent              the parent pane
     */
    @Override
    public void initialize(
            Supplier<Game> gameSupplier,
            Function<Game, CardStack<? extends Card>> drawStackFunction,
            Function<Game, CardStack<? extends Card>> discardStackFunction,
            Pane parent
    ) {
        super.initialize(gameSupplier,drawStackFunction,discardStackFunction,parent);
        createCardStackIcon(ICON_COLOR, cardIcon);
        drawStackTooltipText = "Spielerkarten-Zugstapel";
        discardStackTooltipText = "Spielerkarten-Ablagestapel ansehen";
        drawStackTooltip.setText(drawStackTooltipText);
        discardStackTooltip.setText(discardStackTooltipText);
    }

    /**
     * Draws a player card for the current player.
     * <p>
     * Sends a request to draw a player card using the CardService.
     * </p>
     *
     */
    @Override
    void drawCard() {
        Player currentPlayer = this.gameSupplier.get().getLobby().getPlayerForUser(loggedInUserProvider.get());
        cardService.sendDrawPlayerCardRequest(gameSupplier.get(),currentPlayer);
    }

    /**
     * Discards a player card.
     *
     * <p>
     * Opens a dialog to select a card to discard and sends a request to discard the selected card.
     * </p>
     *
     */
    @Override
    void discardCard() {
        Player currentPlayer = this.gameSupplier.get().getLobby().getPlayerForUser(loggedInUserProvider.get());
        Card selectedCard = createDiscardDialog(currentPlayer);
        if (selectedCard != null) {
            cardService.sendDiscardPlayerCardRequest(gameSupplier.get(),currentPlayer, (PlayerCard) selectedCard);
            reduceNumberOfCardsToDiscard();
        }
    }

    @Override
    protected boolean isGameInCorrectDrawPhase() {
        return gameSupplier.get().getCurrentTurn().isPlayerCardDrawExecutable();
    }

    @Override
    protected void updateToolTips() {
        reinstallTooltip(drawStackNumberOfCardsLabel, drawStackTooltip, drawStackTooltipText);
        reinstallTooltip(discardStackNumberOfCardsLabel, discardStackTooltip, discardStackTooltipText);
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
            reduceNumberOfCardsToDraw();
    }

    /**
     * Handles the response to release the player card draw.
     *
     * <p>
     * This method is called when a {@link ReleaseToDrawPlayerCardResponse} is received. It enables the draw stack
     * and sets the number of player cards to draw.
     * </p>
     *
     * @param response the response containing the number of player cards to draw
     */
    @Subscribe
    public void onReceiveReleaseToDrawPlayerCardResponse(ReleaseToDrawPlayerCardResponse response) {
        if (response.getGame().getId() == this.gameSupplier.get().getId()) {
            this.drawStackNumberOfCardsLabel.setDisable(false);
            this.numberOfCardsToDraw = response.getNumberOfPlayerCardsToDraw();
        }
    }

    /**
     * Handles the response to release the player card discard.
     *
     * <p>
     * This method is called when a {@link ReleaseToDiscardPlayerCardResponse} is received. It enables the discard stack
     * and sets the number of player cards to discard.
     * </p>
     *
     * @param response the response containing the number of player cards to discard
     */
    @Subscribe
    public void onReceiveReleaseToDiscardPlayerCardResponse(ReleaseToDiscardPlayerCardResponse response) {
        if (response.getGame().getId() == this.gameSupplier.get().getId()) {
            this.drawStackNumberOfCardsLabel.setDisable(true);
            this.numberOfCardsToDiscard = response.getNumberOfCardsToDiscard();

            Platform.runLater(this::discardCard);
        }
    }
}
