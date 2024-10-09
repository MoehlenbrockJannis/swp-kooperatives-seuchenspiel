package de.uol.swp.common.action.simple;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.map.GameMap;
import de.uol.swp.common.map.MapSlot;

import java.util.ArrayList;
import java.util.List;

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

        final GameMap gameMap = mock(GameMap.class);

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
