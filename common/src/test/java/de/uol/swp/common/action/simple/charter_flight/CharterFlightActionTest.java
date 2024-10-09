package de.uol.swp.common.action.simple.charter_flight;

import de.uol.swp.common.action.simple.AbstractMoveActionTest;
import de.uol.swp.common.card.CityCard;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.map.GameMap;
import de.uol.swp.common.map.MapSlot;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.Player;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class CharterFlightActionTest extends AbstractMoveActionTest {
    @Getter
    private CharterFlightAction action;
    private Player player;
    private Field currentField;

    @BeforeEach
    void setUp() {
        player = new AIPlayer("mr robot");

        final GameMap gameMap = mock(GameMap.class);
        final MapSlot mapSlot = mock(MapSlot.class);
        currentField = new Field(gameMap, mapSlot);
        player.setCurrentField(currentField);

        action = new CharterFlightAction();
        action.setExecutingPlayer(player);

        setUpFields();
    }

    @Test
    @DisplayName("Should return list of city card with current field as associated field if executing player has it on hand")
    void getDiscardedCard() {
        final CityCard cityCard = new CityCard(currentField);
        player.addHandCard(cityCard);

        assertThat(action.getDiscardedCards())
                .containsExactlyInAnyOrderElementsOf(List.of(cityCard));
    }

    @Test
    @DisplayName("Should throw exception if executing player does not have city card with current field as associated field on hand")
    void getDiscardedCard_error() {
        final Field fieldForHand = new Field(null, null);

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
    @DisplayName("Should return a list of all fields except the current one if the executing player has a hand card for the current field")
    void getAvailableFields_allExceptCurrent() {
        final List<Field> allFieldsWithoutCurrent = new ArrayList<>(allFields);

        final CityCard cityCard = new CityCard(currentField);
        player.addHandCard(cityCard);

        allFields.add(currentField);

        assertThat(action.getAvailableFields())
                .usingRecursiveComparison()
                .isEqualTo(allFieldsWithoutCurrent);
    }

    @Test
    @DisplayName("Should return an empty list if the executing player does not have a hand card for the current field")
    void getAvailableFields_none() {
        assertThat(action.getAvailableFields())
                .isEmpty();
    }
}