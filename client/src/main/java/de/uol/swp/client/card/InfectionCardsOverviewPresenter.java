package de.uol.swp.client.card;

import com.google.inject.Inject;
import de.uol.swp.common.card.response.ReleaseToDrawInfectionCardResponse;
import de.uol.swp.common.card.server_message.DrawInfectionCardServerMessage;
import de.uol.swp.common.player.Player;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Presenter class for handling the overview of infection cards.
 * Extends the CardsOverviewPresenter to provide specific functionality for infection cards.
 */
public class InfectionCardsOverviewPresenter extends CardsOverviewPresenter {

    /**
     * Constructor for InfectionCardsOverviewPresenter.
     *
     * @param service  the CardService to be used for card operations
     * @param eventBus the EventBus for handling events
     */
    @Inject
    public InfectionCardsOverviewPresenter(CardService service, EventBus eventBus) {
        super(service, eventBus);
    }

    /**
     * Draws an infection card for the current player.
     * Sends a request to draw an infection card using the CardService.
     */
    @Override
    void drawCard() {
        Player currentPlayer = this.gameSupplier.get().getLobby().getPlayerForUser(loggedInUserProvider.get());
        cardService.sendDrawInfectionCardRequest(gameSupplier.get(), currentPlayer);
    }

    /**
     * Discards an infection card.
     * Currently not implemented.
     */
    @Override
    void discardCard() {
    }

    @Override
    protected boolean isGameInCorrectDrawPhase() {
        return gameSupplier.get().getCurrentTurn().isInfectionCardDrawExecutable();
    }

    /**
     * Handles the DrawInfectionCardServerMessage event.
     * Displays a popup with the drawn infection card.
     *
     * @param drawInfectionCardServerMessage the message containing the drawn infection card information
     */
    @Subscribe
    public void onDrawInfectionCardServerMessage(DrawInfectionCardServerMessage drawInfectionCardServerMessage) {
        handleCardPopup(drawInfectionCardServerMessage.getGame().getId(), drawInfectionCardServerMessage.getInfectionCard());
        reduceNumberOfCardsToDraw();
    }

    /**
     * Handles the response to release the infection card draw.
     *
     * <p>
     * This method is called when a {@link ReleaseToDrawInfectionCardResponse} response is received. It enables the draw stack
     * and sets the number of infection cards to draw.
     * </p>
     *
     * @param response the response containing the number of infection cards to draw
     */
    @Subscribe
    public void onReceiveReleaseToDrawInfectionCardResponse(ReleaseToDrawInfectionCardResponse response) {
        if (response.getGame().getId() == this.gameSupplier.get().getId()) {
            this.drawStackHBox.setDisable(false);
            this.numberOfCardsToDraw = response.getNumberOfInfectionCardsToDraw();
        }
    }

}
