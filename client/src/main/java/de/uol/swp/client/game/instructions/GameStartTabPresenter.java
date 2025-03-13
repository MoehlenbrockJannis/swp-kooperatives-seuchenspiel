package de.uol.swp.client.game.instructions;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.card.InfectionCardsOverviewPresenter;
import de.uol.swp.client.card.PlayerCardsOverviewPresenter;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

/**
 * Presents the game start tab within the {@link GameInstructionsPresenter}.
 *
 */
public class GameStartTabPresenter extends AbstractPresenter {

    @FXML
    private StackPane playerCardStackPane;
    @FXML
    private StackPane infectionCardStackPane;

    /**
     * Initializes the {@link de.uol.swp.client.card.CardIcon} {@link StackPane}s of the {@link GameStartTabPresenter}.
     */
    @FXML
    public void initialize() {
        GameInstructionIconUtil.initializeCardIconStackPane(playerCardStackPane, PlayerCardsOverviewPresenter.ICON_COLOR);
        GameInstructionIconUtil.initializeCardIconStackPane(infectionCardStackPane, InfectionCardsOverviewPresenter.ICON_COLOR);
    }
}
