package de.uol.swp.common.util;

import de.uol.swp.common.map.City;
import de.uol.swp.common.map.MapSlot;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.plague.Plague;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class that creates objects used for tests
 */
public class TestUtils {

    /**
     * Creates and returns a MapType for testing purposes.
     *
     * @return {@link MapType} with 15 cities and 1 plague
     */
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

        return new MapType(mapSlotList, mapSlotList.get(0).getCity());
    }
}
