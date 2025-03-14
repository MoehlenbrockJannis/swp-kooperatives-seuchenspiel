package de.uol.swp.common.card.event_card;

import de.uol.swp.common.card.InfectionCard;
import de.uol.swp.common.card.stack.CardStack;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.GameDifficulty;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.map.*;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.util.Color;
import de.uol.swp.common.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class ForecastEventCardTest {
    private ForecastEventCard forecastEventCard;
    private Game game;
    private CardStack<InfectionCard> infectionCardDrawStack;
    private GameMap gameMap;

    @BeforeEach
    void setup() {
        final Lobby lobby = new LobbyDTO("name", new UserDTO("user", "", ""));
        lobby.addPlayer(new AIPlayer("ai"));
        GameDifficulty difficulty = GameDifficulty.getDefault();

        game = new Game(lobby, TestUtils.createMapType(), new ArrayList<>(lobby.getPlayers()), List.of(), difficulty);

        forecastEventCard = new ForecastEventCard();
        forecastEventCard.setGame(game);
        gameMap = mock(GameMap.class);
        MapType mockGameType = mock(MapType.class);
        when(gameMap.getType()).thenReturn(mockGameType);
        prepareInfectionCardDrawStack("San Francisco", "Eine Stadt an der Westküste der USA.");
        prepareInfectionCardDrawStack("Chicago", "Eine wichtige Stadt im Mittleren Westen der USA.");
        prepareInfectionCardDrawStack("Atlanta", "Die Hauptstadt des Bundesstaates Georgia, USA.");
        prepareInfectionCardDrawStack("Montreal", "Eine Stadt in der Provinz Québec, Kanada.");
        prepareInfectionCardDrawStack("New York", "Eine Metropole an der Ostküste der USA.");
        prepareInfectionCardDrawStack("Washington", "Die Hauptstadt der USA.");
        prepareInfectionCardDrawStack("London", "Die Hauptstadt des Vereinigten Königreichs.");
        prepareInfectionCardDrawStack("Essen", "Eine Stadt in Deutschland, bekannt für ihre Industriekultur.");
        prepareInfectionCardDrawStack("Mailand", "Eine Stadt in Norditalien, bekannt für Mode und Design.");
        prepareInfectionCardDrawStack("Algier", "Die Hauptstadt von Algerien.");
        prepareInfectionCardDrawStack("Istanbul", "Eine große Stadt in der Türkei, die Europa und Asien verbindet.");
        prepareInfectionCardDrawStack("Kairo", "Die Hauptstadt von Ägypten.");
        prepareInfectionCardDrawStack("Bagdad", "Die Hauptstadt des Irak.");
        prepareInfectionCardDrawStack("Moskau", "Die Hauptstadt von Russland.");

    }

    private void prepareInfectionCardDrawStack(String name, String info) {
        game.getInfectionDrawStack().add(createInfectionCard(name, info));
    }
    private InfectionCard createInfectionCard(String name, String info) {
        return new InfectionCard(new Color(66, 104, 55), new Field(gameMap, new MapSlot(new City(name, info), null, null, 0, 0)));
    }
    @Test
    @DisplayName("Test if the trigger method is swapping the top cards of the infection draw stack")
    void trigger() {
        List<InfectionCard> reorderdInfectionCards = new ArrayList<>();
        reorderdInfectionCards.add(createInfectionCard("Kairo", "Die Hauptstadt von Ägypten."));
        reorderdInfectionCards.add(createInfectionCard("Mailand", "Eine Stadt in Norditalien, bekannt für Mode und Design."));
        reorderdInfectionCards.add(createInfectionCard("Istanbul", "Eine große Stadt in der Türkei, die Europa und Asien verbindet."));
        reorderdInfectionCards.add(createInfectionCard("Bagdad", "Die Hauptstadt des Irak."));
        reorderdInfectionCards.add(createInfectionCard("Moskau", "Die Hauptstadt von Russland."));
        reorderdInfectionCards.add(createInfectionCard("Algier", "Die Hauptstadt von Algerien."));

        forecastEventCard.setReorderedInfectionCards(reorderdInfectionCards);
        forecastEventCard.trigger();
        CardStack<InfectionCard> infectionCardCardStack = game.getInfectionDrawStack();
        assertThat(infectionCardCardStack.get(infectionCardCardStack.size()-1).getCity().getName()).isEqualTo("Kairo");

    }

    @Test
    @DisplayName("Test if the trigger method does not place 'Moskau' at the top of the infection draw stack")
    void triggerShouldFail() {
        List<InfectionCard> reorderedInfectionCards = new ArrayList<>();
        reorderedInfectionCards.add(createInfectionCard("Kairo", "Die Hauptstadt von Ägypten."));
        reorderedInfectionCards.add(createInfectionCard("Mailand", "Eine Stadt in Norditalien, bekannt für Mode und Design."));
        reorderedInfectionCards.add(createInfectionCard("Istanbul", "Eine große Stadt in der Türkei, die Europa und Asien verbindet."));
        reorderedInfectionCards.add(createInfectionCard("Bagdad", "Die Hauptstadt des Irak."));
        reorderedInfectionCards.add(createInfectionCard("Moskau", "Die Hauptstadt von Russland."));
        reorderedInfectionCards.add(createInfectionCard("Algier", "Die Hauptstadt von Algerien."));

        forecastEventCard.setReorderedInfectionCards(reorderedInfectionCards);
        forecastEventCard.trigger();
        CardStack<InfectionCard> infectionCardStack = game.getInfectionDrawStack();

        assertThat(infectionCardStack.get(infectionCardStack.size() - 1).getCity().getName()).isNotEqualTo("Moskau");
    }

}
