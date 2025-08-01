package de.uol.swp.common.action.simple.direct_flight;

import de.uol.swp.common.action.simple.AbstractMoveActionTest;
import de.uol.swp.common.card.CityCard;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.map.GameMap;
import de.uol.swp.common.map.MapSlot;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.util.Color;
import lombok.Getter;
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

class DirectFlightActionTest extends AbstractMoveActionTest {
    @Getter
    private DirectFlightAction action;
    private Player player;
    private Field targetField;

    @BeforeEach
    void setUp() {
        player = new AIPlayer("mr robot");

        final Set<Plague> plagueSet = new HashSet<>();
        plagueSet.add(new Plague("testPlague", new Color(1, 2, 3)));

        final MapType mapType = mock(MapType.class);
        when(mapType.getUniquePlagues())
                .thenReturn(plagueSet);

        final GameMap gameMap = mock(GameMap.class);
        when(gameMap.getType())
                .thenReturn(mapType);
        final MapSlot mapSlot = mock(MapSlot.class);
        targetField = new Field(gameMap, mapSlot);

        action = new DirectFlightAction();
        action.setExecutingPlayer(player);
        action.setTargetField(targetField);

        setUpFields();
    }

    @Test
    @DisplayName("Should return list of city card with target field as associated field if executing player has it on hand")
    void getDiscardedCard() {
        final CityCard cityCard = new CityCard(targetField);
        player.addHandCard(cityCard);

        assertThat(action.getDiscardedCards())
                .containsExactlyInAnyOrderElementsOf(List.of(cityCard));
    }

    @Test
    @DisplayName("Should throw exception if executing player does not have city card with target field as associated field on hand")
    void getDiscardedCard_error() {
        final Set<Plague> plagueSet = new HashSet<>();
        plagueSet.add(new Plague("testPlague", new Color(1, 2, 3)));

        final MapType mapType = mock(MapType.class);
        when(mapType.getUniquePlagues())
                .thenReturn(plagueSet);

        final GameMap map = mock(GameMap.class);
        when(map.getType())
                .thenReturn(mapType);

        final Field fieldForHand = new Field(map, mock(MapSlot.class));

        final CityCard cityCard = new CityCard(fieldForHand);
        player.addHandCard(cityCard);

        assertThatThrownBy(() -> action.getDiscardedCards())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Should return executing player")
    void getMovedPlayer() {
        assertThat(action.getMovedPlayer())
                .usingRecursiveComparison()
                .isEqualTo(player);
    }

    @Test
    @DisplayName("Should return a list of fields the executing player has hand cards for")
    void getAvailableFields() {
        final List<Field> availableFields = neighboringFields;
        for (final Field availableField : availableFields) {
            final CityCard cityCard = new CityCard(availableField);
            player.addHandCard(cityCard);
        }

        assertThat(action.getAvailableFields())
                .usingRecursiveComparison()
                .isEqualTo(availableFields);
    }
}