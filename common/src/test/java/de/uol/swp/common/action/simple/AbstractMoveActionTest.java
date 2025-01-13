package de.uol.swp.common.action.simple;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.map.GameMap;
import de.uol.swp.common.map.MapSlot;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.util.Color;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractMoveActionTest {
    protected List<Field> allFields;
    protected List<Field> neighboringFields;
    protected Field currentField;

    protected abstract MoveAction getAction();

    protected void setUpFields() {
        allFields = new ArrayList<>();
        neighboringFields = new ArrayList<>();

        final Set<Plague> plagueSet = new HashSet<>();
        plagueSet.add(new Plague("testPlague", new Color(1, 2, 3)));

        final MapType mapType = mock(MapType.class);
        when(mapType.getUniquePlagues())
                .thenReturn(plagueSet);

        final GameMap gameMap = mock(GameMap.class);
        when(gameMap.getType())
                .thenReturn(mapType);

        for (int i = 0; i < 10; i++) {
            final Field field = new Field(gameMap, mock(MapSlot.class));
            allFields.add(field);
            if (i == 1) {
                currentField = field;
            }
            if (i % 3 == 0) {
                neighboringFields.add(field);
            }
        }

        when(gameMap.getNeighborFields(currentField))
                .thenReturn(neighboringFields);

        final Game game = mock(Game.class);
        when(game.getMap())
                .thenReturn(gameMap);
        when(game.getFields())
                .thenReturn(allFields);
        when(game.getFields())
                .thenReturn(allFields);
        getAction().setGame(game);
    }
}
