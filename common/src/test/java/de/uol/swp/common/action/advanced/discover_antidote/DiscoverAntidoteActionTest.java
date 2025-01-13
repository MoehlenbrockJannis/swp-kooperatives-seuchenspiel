package de.uol.swp.common.action.advanced.discover_antidote;

import de.uol.swp.common.card.CityCard;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.*;
import de.uol.swp.common.map.research_laboratory.ResearchLaboratory;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.util.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DiscoverAntidoteActionTest {
    private DiscoverAntidoteAction action;
    private Player player;
    private Field field;

    private List<Plague> plagues;
    private Plague plague1;
    private Plague plague2;
    private Plague plague3;

    private List<CityCard> cityCardsPlague1;
    private List<CityCard> cityCardsPlague2;
    private List<CityCard> cityCardsPlague3;

    @BeforeEach
    void setUp() {
        plague1 = new Plague("tick", new Color());
        plague2 = new Plague("trick", new Color());
        plague3 = new Plague("track", new Color());

        plagues = new ArrayList<>();
        plagues.addAll(List.of(
                plague1,
                plague2,
                plague3
        ));

        cityCardsPlague1 = new ArrayList<>();
        addCityCardsToList(cityCardsPlague1, 6, plague1);

        cityCardsPlague2 = new ArrayList<>();
        addCityCardsToList(cityCardsPlague2, 5, plague2);

        cityCardsPlague3 = new ArrayList<>();
        addCityCardsToList(cityCardsPlague3, 1, plague3);

        final Set<Plague> plagueSet = new HashSet<>();
        plagueSet.add(plague1);

        final MapType mapType = mock(MapType.class);
        when(mapType.getUniquePlagues())
                .thenReturn(plagueSet);

        final GameMap map = mock(GameMap.class);
        when(map.getType())
                .thenReturn(mapType);
        final MapSlot mapSlot = mock(MapSlot.class);
        field = new Field(map, mapSlot);

        player = new AIPlayer("jeff");
        player.setCurrentField(field);

        final Game game = mock(Game.class);
        when(game.getPlagues())
                .thenReturn(plagues);

        action = new DiscoverAntidoteAction();
        action.setExecutingPlayer(player);
        action.setGame(game);
    }

    private void addCityCardsToList(final List<CityCard> cityCards, final int amount, final Plague plague) {
        for (int i = 0; i < amount; i++) {
            final City city = new City("city" + i, "");
            final Field f = mock(Field.class);
            when(f.getPlague())
                    .thenReturn(plague);
            when(f.hasPlague(any()))
                    .thenAnswer(invocation -> plague.equals(invocation.getArgument(0)));
            when(f.getCity())
                    .thenReturn(city);
            final CityCard card = new CityCard(f);
            cityCards.add(card);
        }
    }

    @Test
    @DisplayName("Should add the given card to the list of discarded cards")
    void addDiscardedCard() {
        assertThat(action.getDiscardedCards())
                .isEmpty();

        action.setPlague(plague1);

        final CityCard card = cityCardsPlague1.get(0);

        action.addDiscardedCard(card);

        assertThat(action.getDiscardedCards())
                .contains(card);
    }

    @Test
    @DisplayName("Should throw an exception if given card is already in discarded cards")
    void addDiscardedCard_alreadyContained() {
        final CityCard card = cityCardsPlague1.get(0);

        action.setPlague(plague1);
        action.addDiscardedCard(card);

        assertThatThrownBy(() -> action.addDiscardedCard(card))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Should throw an exception if given card does not have the same plague as specified")
    void addDiscardedCard_wrongPlague() {
        action.setPlague(plague1);

        final CityCard card = cityCardsPlague2.get(0);

        assertThatThrownBy(() -> action.addDiscardedCard(card))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Should throw an exception if there are already enough discarded cards")
    void addDiscardedCard_tooMany() {
        action.setPlague(plague1);

        for (int i = 1; i < cityCardsPlague1.size(); i++) {
            action.addDiscardedCard(cityCardsPlague1.get(i));
        }

        final CityCard card = cityCardsPlague1.get(0);

        assertThatThrownBy(() -> action.addDiscardedCard(card))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Should return true if current field has a research laboratory and there are enough discarded cards")
    void isAvailable_true() {
        this.field.buildResearchLaboratory(new ResearchLaboratory());

        for (final PlayerCard card : cityCardsPlague1) {
            player.addHandCard(card);
        }

        assertThat(action.isAvailable())
                .isTrue();
    }

    @Test
    @DisplayName("Should return false if current field does not have a research laboratory")
    void isAvailable_falseNoLab() {
        assertThat(action.isAvailable())
                .isFalse();
    }

    @Test
    @DisplayName("Should return false if player has not enough fitting cards to discard")
    void isAvailable_falseNoCards() {
        this.field.buildResearchLaboratory(new ResearchLaboratory());

        assertThat(action.isAvailable())
                .isFalse();
    }

    @Test
    @DisplayName("Should return false if antidote marker to specified plague has already been discovered")
    void isAvailable_falseAllAntidotesDiscovered() {
        this.field.buildResearchLaboratory(new ResearchLaboratory());

        when(action.getGame().hasAntidoteMarkerForPlague(any()))
                .thenReturn(true);

        assertThat(action.isAvailable())
                .isFalse();
    }

    @Test
    @DisplayName("Should return true if action is available and there are enough discarded cards and there is no antidote marker to specified plague on game")
    void isExecutable_true() {
        this.field.buildResearchLaboratory(new ResearchLaboratory());

        action.setPlague(plague2);

        for (final CityCard card : cityCardsPlague2) {
            action.addDiscardedCard(card);
            player.addHandCard(card);
        }

        assertThat(action.isExecutable())
                .isTrue();
    }

    @Test
    @DisplayName("Should return false if action is unavailable")
    void isExecutable_falseUnavailable() {
        assertThat(action.isExecutable())
                .isFalse();
    }

    @Test
    @DisplayName("Should return false if not enough cards have been discarded")
    void isExecutable_falseNotEnoughCards() {
        this.field.buildResearchLaboratory(new ResearchLaboratory());

        assertThat(action.isExecutable())
                .isFalse();
    }

    @Test
    @DisplayName("Should return false if antidote to specified plague has already been discovered")
    void isExecutable_falseAntidoteDiscovered() {
        this.field.buildResearchLaboratory(new ResearchLaboratory());

        when(action.getGame().hasAntidoteMarkerForPlague(any()))
                .thenReturn(true);

        action.setPlague(plague2);

        for (final CityCard card : cityCardsPlague2) {
            action.addDiscardedCard(card);
            player.addHandCard(card);
        }

        assertThat(action.isExecutable())
                .isFalse();
    }

    @Test
    @DisplayName("Should add an antidote marker to specified plague to the associated game")
    void execute() {
        this.field.buildResearchLaboratory(new ResearchLaboratory());

        action.setPlague(plague2);

        for (final CityCard card : cityCardsPlague2) {
            action.addDiscardedCard(card);
            player.addHandCard(card);
        }

        action.execute();

        verify(action.getGame(), times(1))
                .addAntidoteMarker(any());
    }

    @Test
    @DisplayName("Should throw an exception if action is not executable")
    void execute_errorUnavailable() {
        assertThatThrownBy(() -> action.execute())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Should return 5")
    void getRequiredAmountOfDiscardedCards() {
        assertThat(action.getRequiredAmountOfDiscardedCards())
                .isEqualTo(5);
    }

    @Test
    @DisplayName("Should return an empty list if no cards have been discarded")
    void getDiscardedCards_empty() {
        assertThat(action.getDiscardedCards())
                .isEmpty();
    }

    @Test
    @DisplayName("Should return an the list of discarded cards if cards have been discarded")
    void getDiscardedCards_filled() {
        action.setPlague(plague2);

        for (final CityCard card : cityCardsPlague2) {
            action.addDiscardedCard(card);
            player.addHandCard(card);
        }

        final List<CityCard> expected = cityCardsPlague2;

        assertThat(action.getDiscardedCards())
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Should return specified plague")
    void getPlague() {
        action.setPlague(plague1);

        assertThat(action.getPlague())
                .usingRecursiveComparison()
                .isEqualTo(plague1);
    }

    @Test
    @DisplayName("Should set given plague as specified plague")
    void setPlague() {
        assertThat(action.getPlague())
                .isNull();

        action.setPlague(plague1);

        assertThat(action.getPlague())
                .usingRecursiveComparison()
                .isEqualTo(plague1);
    }
}