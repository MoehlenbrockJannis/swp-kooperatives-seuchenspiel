package de.uol.swp.common.action.simple.shuttle_flight;

import de.uol.swp.common.action.simple.AbstractMoveActionTest;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.map.GameMap;
import de.uol.swp.common.map.MapSlot;
import de.uol.swp.common.map.research_laboratory.ResearchLaboratory;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.Player;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ShuttleFlightActionTest extends AbstractMoveActionTest {
    @Getter
    private ShuttleFlightAction action;
    private Player player;
    private Field currentField;

    @BeforeEach
    void setUp() {
        player = new AIPlayer("space shuttle");

        final GameMap gameMap = mock(GameMap.class);
        final MapSlot mapSlot = mock(MapSlot.class);
        currentField = new Field(gameMap, mapSlot);
        player.setCurrentField(currentField);

        action = new ShuttleFlightAction();
        action.setExecutingPlayer(player);

        setUpFields();
    }

    @Test
    @DisplayName("Should return executing player")
    void getMovedPlayer() {
        assertThat(action.getMovedPlayer())
                .usingRecursiveComparison()
                .isEqualTo(player);
    }

    @Test
    @DisplayName("Should return list of fields with research laboratories on them if current field has research laboratory")
    void getAvailableFields() {
        currentField.buildResearchLaboratory(new ResearchLaboratory());
        allFields.add(currentField);

        final List<Field> availableFields = allFields.subList(0, allFields.size() / 2);
        for (final Field availableField : availableFields) {
            availableField.buildResearchLaboratory(new ResearchLaboratory());
        }

        assertThat(action.getAvailableFields())
                .usingRecursiveComparison()
                .isEqualTo(availableFields);
    }

    @Test
    @DisplayName("Should return empty list if current field does not have research laboratory")
    void getAvailableFields_unavailable() {
        assertThat(action.getAvailableFields())
                .isEmpty();
    }
}