package de.uol.swp.client.card;

import com.google.inject.Inject;
import de.uol.swp.common.card.Card;
import de.uol.swp.common.card.response.ReleaseToDrawInfectionCardResponse;
import de.uol.swp.common.card.server_message.DrawInfectionCardServerMessage;
import de.uol.swp.common.card.stack.CardStack;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.marker.InfectionMarker;
import de.uol.swp.common.player.Player;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Presenter class for handling the overview of infection cards.
 * Extends the CardsOverviewPresenter to provide specific functionality for infection cards.
 */
public class InfectionCardsOverviewPresenter extends CardsOverviewPresenter {
    public static final Color ICON_COLOR = Color.DARKOLIVEGREEN;
    public static final int INFECTION_MARKER_FONT_SIZE = 20;

    private Label infectionMarkerLabel;

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

    @Override
    public void initialize(
            Supplier<Game> gameSupplier,
            Function<Game, CardStack<? extends Card>> drawStackFunction,
            Function<Game, CardStack<? extends Card>> discardStackFunction,
            Pane parent
    ) {
        super.initialize(gameSupplier,drawStackFunction,discardStackFunction,parent);
        createCardStackIcon(ICON_COLOR, cardIcon);
        drawStackTooltipText = "Infektions-Zugstapel";
        discardStackTooltipText = "Infektions-Ablagestapel ansehen";
        drawStackTooltip.setText(drawStackTooltipText);
        discardStackTooltip.setText(discardStackTooltipText);
        setupInfectionMarkerLabel();
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
     * Empty implementation required for inheritance.
     * This functionality is only used in sibling classes.
     */
    @Override
    void discardCard() {
    }

    @Override
    protected boolean isGameInCorrectDrawPhase() {
        return gameSupplier.get().getCurrentTurn().isInfectionCardDrawExecutable();
    }

    @Override
    protected void updateToolTips() {
        reinstallTooltip(drawStackNumberOfCardsLabel, drawStackTooltip, drawStackTooltipText);
        reinstallTooltip(discardStackNumberOfCardsLabel, discardStackTooltip, discardStackTooltipText);
    }

    /**
    * Sets up the infection marker label.
    * Initializes the label and adds it to the card icon.
    * Updates the label with the current infection level.
    */
    private void setupInfectionMarkerLabel() {
    this.infectionMarkerLabel = new Label();
    Font labelFont = new Font(INFECTION_MARKER_FONT_SIZE);
    this.infectionMarkerLabel.setFont(labelFont);
    this.cardIcon.getChildren().add(infectionMarkerLabel);
    updateInfectionMarkerLabel();
    }

    /**
     * Updates the infection marker label with the current infection level.
     */
    public void updateInfectionMarkerLabel() {
        Game game = gameSupplier.get();
        InfectionMarker infectionMarker = game.getInfectionMarker();
        int infectionLevel = infectionMarker.getLevelValue();
        Platform.runLater(() -> {
            this.infectionMarkerLabel.setText(Integer.toString(infectionLevel));
            setColorForInfectionMarkerLabel();
        });
    }

    /**
     * Sets the color of the infection marker label based on the current infection level.
     */
    private void setColorForInfectionMarkerLabel() {
        Game game = gameSupplier.get();
        InfectionMarker infectionMarker = game.getInfectionMarker();
        int infectionLevel = infectionMarker.getLevelValue();

        switch (infectionLevel) {
            case 3 -> this.infectionMarkerLabel.setTextFill(Color.ORANGE);
            case 4 -> this.infectionMarkerLabel.setTextFill(Color.RED);
            default -> this.infectionMarkerLabel.setTextFill(Color.WHITE);
        }
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
            this.drawStackNumberOfCardsLabel.setDisable(false);
            this.numberOfCardsToDraw = response.getNumberOfInfectionCardsToDraw();
        }
    }

    /**
     * Sets the {@link Runnable} to be performed when the {@link #cardIcon} is entered by the mouse.
     *
     * @param runnable the action to be performed
     */
    public void setCardIconOnMouseEntered(Runnable runnable) {
        cardIcon.setOnMouseEntered(event -> runnable.run());
    }

    /**
     * Sets the {@link Runnable} to be performed when the {@link #cardIcon} is exited by the mouse.
     *
     * @param runnable the action to be performed
     */
    public void setCardIconOnMouseExited(Runnable runnable) {
        cardIcon.setOnMouseExited(event -> runnable.run());
    }
}
