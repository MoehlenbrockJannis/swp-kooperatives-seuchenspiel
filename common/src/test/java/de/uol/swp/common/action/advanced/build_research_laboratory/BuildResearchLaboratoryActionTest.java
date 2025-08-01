package de.uol.swp.common.action.advanced.build_research_laboratory;

import de.uol.swp.common.card.CityCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.map.GameMap;
import de.uol.swp.common.map.MapSlot;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.map.research_laboratory.ResearchLaboratory;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.util.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BuildResearchLaboratoryActionTest {

    private BuildResearchLaboratoryAction action;
    private Player player;
    private Field field1;
    private Field field2;
    private Game game;

    @BeforeEach
    void setUp() {
        final Set<Plague> plagueSet = new HashSet<>();
        plagueSet.add(new Plague("testPlague", new Color(1, 2, 3)));

        final MapType mapType = mock(MapType.class);
        when(mapType.getUniquePlagues()).thenReturn(plagueSet);

        final GameMap map = mock(GameMap.class);
        when(map.getType()).thenReturn(mapType);

        game = mock(Game.class);

        final MapSlot mapSlot1 = mock(MapSlot.class);
        field1 = new Field(map, mapSlot1);

        final MapSlot mapSlot2 = mock(MapSlot.class);
        field2 = new Field(map, mapSlot2);

        player = new AIPlayer("builder bot");
        player.setCurrentField(field1);

        action = new BuildResearchLaboratoryAction();
        action.setExecutingPlayer(player);
    }

    @Test
    @DisplayName("Should return the specified research laboratory origin field")
    void getResearchLaboratoryOriginField() {
        field1.buildResearchLaboratory(new ResearchLaboratory());
        action.setResearchLaboratoryOriginField(field1);

        assertThat(action.getResearchLaboratoryOriginField())
                .usingRecursiveComparison()
                .isEqualTo(field1);
    }

    @Test
    @DisplayName("Should set given field as specified research laboratory origin field")
    void setResearchLaboratoryOriginField() {
        assertThat(action.getResearchLaboratoryOriginField()).isNull();

        field1.buildResearchLaboratory(new ResearchLaboratory());
        action.setResearchLaboratoryOriginField(field1);

        assertThat(action.getResearchLaboratoryOriginField())
                .usingRecursiveComparison()
                .isEqualTo(field1);
    }

    @Test
    @DisplayName("Should throw an exception if given field does not have a research laboratory")
    void setResearchLaboratoryOriginField_noLab() {
        assertThatThrownBy(() -> action.setResearchLaboratoryOriginField(field1))
                .isInstanceOf(IllegalStateException.class);
    }


    @Test
    @DisplayName("Should return true if executing player has a city card of current field on hand")
    void isAvailable_true() {
        final CityCard card = new CityCard(field1);
        player.addHandCard(card);

        assertThat(action.isAvailable()).isTrue();
    }

    @Test
    @DisplayName("Should return false if there is already a research laboratory on current field")
    void isAvailable_falseAlreadyALab() {
        field1.buildResearchLaboratory(new ResearchLaboratory());

        assertThat(action.isAvailable()).isFalse();
    }

    @Test
    @DisplayName("Should return false if executing player does not have a city card of current field on hand")
    void isAvailable_falseNoCorrectCard() {
        assertThat(action.isAvailable()).isFalse();
    }

    @Test
    @DisplayName("Should return true if action is available and there is a research laboratory on game")
    void isExecutable_true() {
        final CityCard card = new CityCard(field1);
        player.addHandCard(card);

        when(game.hasResearchLaboratory()).thenReturn(true);
        action.setGame(game);

        assertThat(action.isExecutable()).isTrue();
    }

    @Test
    @DisplayName("Should return false if action is unavailable")
    void isExecutable_falseUnavailable() {
        action.setGame(game);
        assertThat(action.isExecutable()).isFalse();
    }

    @Test
    @DisplayName("Should return false if there is no research laboratory on game")
    void isExecutable_falseRequiresMovingAndNotSet() {
        action.setGame(game);
        final CityCard card = new CityCard(field1);
        player.addHandCard(card);

        assertThat(game.getResearchLaboratories()).isEmpty();
    }

    @Test
    @DisplayName("Should add a research laboratory to the current field by getting it from game")
    void execute_withoutMoving() {
        final CityCard card = new CityCard(field1);
        player.addHandCard(card);

        when(game.hasResearchLaboratory()).thenReturn(true);
        when(game.getResearchLaboratory()).thenReturn(new ResearchLaboratory());
        action.setGame(game);

        assertThat(field1.hasResearchLaboratory()).isFalse();

        action.execute();

        assertThat(field1.hasResearchLaboratory()).isTrue();
    }


    @Test
    @DisplayName("Should throw an exception if action is not executable")
    void execute_unavailable() {
        action.setGame(game);
        assertThatThrownBy(() -> action.execute())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Should return a list of cards with a city card of the current field if the executing player has it on hand")
    void getDiscardedCard() {
        final CityCard card = new CityCard(field1);
        player.addHandCard(card);

        assertThat(action.getDiscardedCards())
                .containsExactlyInAnyOrderElementsOf(List.of(card));
    }

    @Test
    @DisplayName("Should throw an exception if the executing player does not have a city card of the current field on hand")
    void getDiscardedCard_error() {
        assertThatThrownBy(() -> action.getDiscardedCards())
                .isInstanceOf(IllegalStateException.class);
    }
}
