package de.uol.swp.client.game.instructions;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.card.InfectionCardsOverviewPresenter;
import de.uol.swp.client.card.PlayerCardsOverviewPresenter;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * Presents the player cards tab within the {@link GameInstructionsPresenter}.
 *
 */
public class PlayerCardsTabPresenter extends AbstractPresenter {

    @FXML
    private StackPane playerCardStackPane;
    @FXML
    private StackPane blueCityCardStackPane;
    @FXML
    private StackPane yellowCityCardStackPane;
    @FXML
    private StackPane redCityCardStackPane;
    @FXML
    private StackPane blackCityCardStackPane;
    @FXML
    private StackPane eventCardStackPane;
    @FXML
    private StackPane epidemicCardStackPane;
    @FXML
    private StackPane infectionCardStackPane;

    private static final Color BLUE_CITY_CARD_COLOR = Color.rgb(40, 138, 204);
    private static final Color YELLOW_CITY_CARD_COLOR = Color.rgb(255, 235, 61);
    private static final Color RED_CITY_CARD_COLOR = Color.rgb(255, 46, 23);
    private static final Color BLACK_CITY_CARD_COLOR = Color.rgb(89, 91, 97);
    private static final Color EVENT_CARD_COLOR = Color.rgb(255, 165, 0);
    private static final Color EPIDEMIC_CARD_COLOR = Color.rgb(0, 100, 0);


    /**
     * Initializes the {@link de.uol.swp.client.card.CardIcon} {@link StackPane}s of the {@link PlayerCardsTabPresenter}.
     */
    @FXML
    public void initialize() {
        GameInstructionIconUtil.initializeCardIconStackPane(playerCardStackPane, PlayerCardsOverviewPresenter.ICON_COLOR);
        GameInstructionIconUtil.initializeCardIconStackPane(blueCityCardStackPane, BLUE_CITY_CARD_COLOR);
        GameInstructionIconUtil.initializeCardIconStackPane(yellowCityCardStackPane, YELLOW_CITY_CARD_COLOR);
        GameInstructionIconUtil.initializeCardIconStackPane(redCityCardStackPane, RED_CITY_CARD_COLOR);
        GameInstructionIconUtil.initializeCardIconStackPane(blackCityCardStackPane, BLACK_CITY_CARD_COLOR);
        GameInstructionIconUtil.initializeCardIconStackPane(eventCardStackPane, EVENT_CARD_COLOR);
        GameInstructionIconUtil.initializeCardIconStackPane(epidemicCardStackPane, EPIDEMIC_CARD_COLOR);
        GameInstructionIconUtil.initializeCardIconStackPane(infectionCardStackPane, InfectionCardsOverviewPresenter.ICON_COLOR);
    }
}
