package de.uol.swp.common.action.advanced.cure_plague;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.map.GameMap;
import de.uol.swp.common.map.MapSlot;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.plague.PlagueCube;
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
import static org.mockito.Mockito.*;

class CurePlagueActionTest {
    private CurePlagueAction action;
    private Player player;
    private Field field;
    private GameMap map;
    private Plague plague;
    private List<Plague> plagues;
    private List<Field> infectedFields;

    @BeforeEach
    void setUp() {
        plague = new Plague("dto pattern", new Color());

        plagues = new ArrayList<>();
        plagues.add(plague);

        final Set<Plague> plagueSet = new HashSet<>();
        plagueSet.add(plague);

        final MapType mapType = mock(MapType.class);
        when(mapType.getUniquePlagues())
                .thenReturn(plagueSet);

        map = mock(GameMap.class);
        when(map.getMaxNumberOfPlagueCubesPerField())
                .thenReturn(3);
        when(map.getType())
                .thenReturn(mapType);
        final MapSlot mapSlot = mock(MapSlot.class);
        field = new Field(map, mapSlot);
        when(map.getFields())
                .thenReturn(List.of(field));

        player = new AIPlayer("t");
        player.setCurrentField(field);

        final Game game = mock(Game.class);
        when(game.getPlagues())
                .thenReturn(plagues);
        when(game.getMap())
                .thenReturn(map);

        action = new CurePlagueAction();
        action.setExecutingPlayer(player);
        action.setGame(game);
        action.setPlague(plague);

        infectedFields = new ArrayList<>();
    }

    @Test
    @DisplayName("Should remove one plague cube from current field")
    void removeOnePlagueCube() {
        field.infectField(new PlagueCube(plague), infectedFields);
        field.infectField(new PlagueCube(plague), infectedFields);

        action.removeOnePlagueCube();

        assertThat(field.isCurable(plague))
                .isTrue();
        verify(action.getGame(), times(1))
                .addPlagueCube(any());
    }


    @Test
    @DisplayName("Should remove all plague cubes from current field")
    void removeAllPlagueCubes() {
        field.infectField(new PlagueCube(plague), infectedFields);
        field.infectField(new PlagueCube(plague), infectedFields);

        action.removeAllPlagueCubes();

        assertThat(field.isCurable(plague))
                .isFalse();
        verify(action.getGame(), times(2))
                .addPlagueCube(any());
    }

    @Test
    @DisplayName("Should return true if there is an antidote marker for specified plague on game")
    void isRemoveAllPlagueCubesAvailable_true() {
        when(action.getGame().hasAntidoteMarkerForPlague(plague))
                .thenReturn(true);

        assertThat(action.isRemoveAllPlagueCubesAvailable())
                .isTrue();
    }

    @Test
    @DisplayName("Should return false if there is no antidote marker for specified plague on game")
    void isRemoveAllPlagueCubesAvailable_false() {
        assertThat(action.isRemoveAllPlagueCubesAvailable())
                .isFalse();
    }

    @Test
    @DisplayName("Should return true if there is at least one plague cube on current field")
    void isAvailable_true() {
        final Plague plague2 = new Plague("plague 2 eletric boogaloo", new Color());

        plagues.add(plague2);

        field.infectField(new PlagueCube(plague), infectedFields);

        assertThat(action.isAvailable())
                .isTrue();
    }

    @Test
    @DisplayName("Should return false if there are no plague cubes on current field")
    void isAvailable_falseNoCubes() {
        final Plague plague2 = new Plague("plague 2 eletric boogaloo", new Color());

        plagues.add(plague2);

        assertThat(action.isAvailable())
                .isFalse();
    }

    @Test
    @DisplayName("Should return false if there are no plagues on game")
    void isAvailable_falseNoPlagues() {
        plagues.clear();

        assertThat(action.isAvailable())
                .isFalse();
    }

    @Test
    @DisplayName("Should return true if action is available")
    void isExecutable_true() {
        final Plague plague2 = new Plague("plague 2 eletric boogaloo", new Color());

        plagues.add(plague2);

        field.infectField(new PlagueCube(plague), infectedFields);

        assertThat(action.isExecutable())
                .isTrue();
    }

    @Test
    @DisplayName("Should return false if action is unavailable")
    void isExecutable_false() {
        assertThat(action.isExecutable())
                .isFalse();
    }

    @Test
    @DisplayName("Should throw an exception is action is not executable")
    void execute_unavailable() {
        assertThatThrownBy(() -> action.execute())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Should remove one plague cube from current field if action is executable and the option to remove all plague cubes is unavailable")
    void execute_oneCube() {
        field.infectField(new PlagueCube(plague), infectedFields);
        field.infectField(new PlagueCube(plague), infectedFields);

        action.execute();

        assertThat(field.isCurable(plague))
                .isTrue();
        verify(action.getGame(), times(1))
                .addPlagueCube(any());
    }

    @Test
    @DisplayName("Should remove all plague cubes from current field if action is executable and the option to remove all plague cubes is available")
    void execute_allCubes() {
        when(action.getGame().hasAntidoteMarkerForPlague(plague))
                .thenReturn(true);

        field.infectField(new PlagueCube(plague), infectedFields);
        field.infectField(new PlagueCube(plague), infectedFields);

        action.execute();

        assertThat(field.isCurable(plague))
                .isFalse();
        verify(action.getGame(), times(2))
                .addPlagueCube(any());
    }

    @Test
    @DisplayName("Should return specified plague")
    void getPlague() {
        assertThat(action.getPlague())
                .usingRecursiveComparison()
                .isEqualTo(plague);
    }

    @Test
    @DisplayName("Should set given plague as specified plague")
    void setPlague() {
        action.setPlague(null);

        assertThat(action.getPlague())
                .isNull();

        action.setPlague(plague);

        assertThat(action.getPlague())
                .usingRecursiveComparison()
                .isEqualTo(plague);
    }
}