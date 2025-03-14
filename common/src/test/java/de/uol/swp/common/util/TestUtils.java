package de.uol.swp.common.util;

import de.uol.swp.common.action.ActionFactory;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.turn.PlayerTurn;
import de.uol.swp.common.map.City;
import de.uol.swp.common.map.MapSlot;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.player.Player;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TestUtils {

    public static MapType createMapType() {
        final Plague plague = new Plague("plague", new Color(1, 2, 3));

        final City city0 = new City("city0", "info0");
        final List<City> connectedCities = new ArrayList<>();
        final List<MapSlot> mapSlotList = new ArrayList<>();

        for (int i = 1; i < 15; i++) {
            final City city = new City("city"+i, "info"+i);
            connectedCities.add(city);

            final MapSlot mapSlot = new MapSlot(city, List.of(city0), plague, i, i);
            mapSlotList.add(mapSlot);
        }
        MapSlot mapSlot = new MapSlot(city0, connectedCities, plague, 0, 0);
        mapSlotList.add(mapSlot);

        return new MapType("name", mapSlotList, mapSlotList.get(0).getCity());
    }

    public static PlayerTurn createPlayerTurn(final Game game,
                                              final Player player,
                                              final int numberOfActionsToDo,
                                              final int numberOfPlayerCardsToDraw,
                                              final int numberOfInfectionCardsToDraw) {
        try (MockedConstruction<ActionFactory> mockActionFactory = Mockito.mockConstruction(ActionFactory.class, (mock, context) -> {
            when(mock.createAllGeneralActionsExcludingSomeAndIncludingSomeRoleActions(any(), any()))
                    .thenReturn(List.of());
        })) {
            return new PlayerTurn(game, player, numberOfActionsToDo, numberOfPlayerCardsToDraw, numberOfInfectionCardsToDraw);
        }
    }
}
