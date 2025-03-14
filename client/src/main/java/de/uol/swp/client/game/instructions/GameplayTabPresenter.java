package de.uol.swp.client.game.instructions;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.card.InfectionCardsOverviewPresenter;
import de.uol.swp.client.card.PlayerCardsOverviewPresenter;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

/**
 * Presents the gameplayF tab within the {@link GameInstructionsPresenter}.
 *
 */
public class GameplayTabPresenter extends AbstractPresenter {

    @FXML
    private StackPane playerCardStackPane;
    @FXML
    private StackPane infectionCardStackPane;

    /**
     * Initializes the {@link de.uol.swp.client.card.CardIcon} {@link StackPane}s of the {@link GameplayTabPresenter}.
     */
    @FXML
    public void initialize() {
        GameInstructionIconUtil.initializeCardIconStackPane(playerCardStackPane, PlayerCardsOverviewPresenter.ICON_COLOR);
        GameInstructionIconUtil.initializeCardIconStackPane(infectionCardStackPane, InfectionCardsOverviewPresenter.ICON_COLOR);
    }
}
