package de.uol.swp.common.action.simple;

import de.uol.swp.common.action.simple.car.CarAction;
import de.uol.swp.common.action.simple.car.CarActionForAlly;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.map.GameMap;
import de.uol.swp.common.map.MapSlot;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.UserPlayer;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.util.Color;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MoveActionTest extends AbstractMoveActionTest {
    @Getter
    private MoveAction action;
    private Player player;
    private Field field;

    @BeforeEach
    void setUp() {
        player = new AIPlayer("robots in disguise");

        final Set<Plague> plagueSet = new HashSet<>();
        plagueSet.add(new Plague("testPlague", new Color(1, 2, 3)));

        final MapType mapType = mock(MapType.class);
        when(mapType.getUniquePlagues())
                .thenReturn(plagueSet);

        final GameMap gameMap = mock(GameMap.class);
        when(gameMap.getType())
                .thenReturn(mapType);
        final MapSlot mapSlot = mock(MapSlot.class);
        field = new Field(gameMap, mapSlot);

        action = new CarAction();
        action.setExecutingPlayer(player);

        setUpFields();

        player.setCurrentField(currentField);
    }

    @Test
    @DisplayName("Should return specified current field of moved player")
    void getCurrentField() {
        assertThat(action.getCurrentField())
                .usingRecursiveComparison()
                .isEqualTo(currentField);
    }

    @Test
    @DisplayName("Should return specified target field")
    void getTargetField() {
        action.setTargetField(field);

        assertThat(action.getTargetField())
                .usingRecursiveComparison()
                .isEqualTo(field);
    }

    @Test
    @DisplayName("Should set given field as target field")
    void setTargetField() {
        assertThat(action.getTargetField())
                .isNull();

        action.setTargetField(field);

        assertThat(action.getTargetField())
                .usingRecursiveComparison()
                .isEqualTo(field);
    }

    @Test
    @DisplayName("Should return true if there are available fields")
    void isAvailable_true() {
        assertThat(action.isAvailable())
                .isTrue();
    }

    @Test
    @DisplayName("Should return false if there are no available fields")
    void isAvailable_false() {
        neighboringFields.clear();

        assertThat(action.isAvailable())
                .isFalse();
    }

    @Test
    @DisplayName("Should return true if action is available and target field is in available fields")
    void isExecutable_true() {
        final Field targetField = action.getAvailableFields().get(0);
        action.setTargetField(targetField);

        assertThat(action.isExecutable())
                .isTrue();
    }

    @Test
    @DisplayName("Should return false if action is not available")
    void isExecutable_falseNoFields() {
        neighboringFields.clear();

        assertThat(action.isExecutable())
                .isFalse();
    }

    @Test
    @DisplayName("Should return false if target field is not in available fields (e.g. null)")
    void isExecutable_falseInvalidFields() {
        assertThat(action.isExecutable())
                .isFalse();
    }

    @Test
    @DisplayName("Should return true if criteria are met and the action is a MoveAllyAction and it is approved")
    void isExecutable_trueMoveAllyActionApproved() {
        final Player ally = new UserPlayer(new UserDTO("truth", "", ""));
        ally.setCurrentField(currentField);

        final CarActionForAlly allyAction = new CarActionForAlly();
        allyAction.approve();
        allyAction.setMovedAlly(ally);

        action = allyAction;
        action.setExecutingPlayer(player);

        setUpFields();

        final Field targetField = action.getAvailableFields().get(0);
        action.setTargetField(targetField);

        assertThat(action.isExecutable())
                .isTrue();
    }

    @Test
    @DisplayName("Should return false if criteria are met and the action is a MoveAllyAction and it is not approved")
    void isExecutable_falseMoveAllyActionNotApproved() {
        final Player ally = new UserPlayer(new UserDTO("truth", "", ""));
        ally.setCurrentField(currentField);

        final CarActionForAlly allyAction = new CarActionForAlly();
        allyAction.setMovedAlly(ally);

        action = allyAction;
        action.setExecutingPlayer(player);

        setUpFields();

        final Field targetField = action.getAvailableFields().get(0);
        action.setTargetField(targetField);

        assertThat(action.isExecutable())
                .isFalse();
    }

    @Test
    @DisplayName("Should set the current field of moved player to target field if action is executable")
    void execute() {
        final Field targetField = action.getAvailableFields().get(0);
        action.setTargetField(targetField);

        action.execute();

        assertThat(player.getCurrentField())
                .usingRecursiveComparison()
                .isEqualTo(targetField);
    }

    @Test
    @DisplayName("Should throw exception if action is not executable")
    void execute_unavailable() {
        neighboringFields.clear();

        assertThatThrownBy(() -> action.execute())
                .isInstanceOf(IllegalStateException.class);
    }
}