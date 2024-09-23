package de.uol.swp.server.map;

import de.uol.swp.common.map.City;
import de.uol.swp.common.map.MapSlot;
import de.uol.swp.common.map.MapType;
import de.uol.swp.server.plague.Plagues;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * A utility class that provides a MapType instance based on the original Pandemic game.
 *
 * @see MapType
 * @see MapSlot
 * @see City
 * @author David Scheffler
 * @since 2024-09-22
 */
public final class OriginalGameMapType {

    /**
     * Private constructor to prevent instantiation
     *
     * @author David Scheffler
     * @since 2024-09-22
     */
    private OriginalGameMapType() {
    }

    /**
     * Creates and returns a {@link MapType} object based on the original game.
     *
     * @return The MapType based on the original game
     * @see MapType
     * @author David Scheffler
     * @since 2024-09-22
     */
    public static MapType getMapType() {
        return new MapType(createMapSlotList(), Cities.ATLANTA);
    }

    /**
     * Creates and returns a list of {@link MapSlot} objects with all cities from the original game.
     *
     * @return A List of MapSlots including all cities from the original game
     * @see MapSlot
     * @author David Scheffler
     * @since 2024-09-22
     */
    private static List<MapSlot> createMapSlotList() {
        List<MapSlot> mapSlotList = new ArrayList<>();

        // Blue Plague
        mapSlotList.add(new MapSlot(Cities.SAN_FRANCISCO,
                new ArrayList<>(
                        Arrays.asList(Cities.TOKYO, Cities.MANILA, Cities.CHICAGO, Cities.LOS_ANGELES)
                ),
                Plagues.BLUE_PLAGUE, 170, 330));

        mapSlotList.add(new MapSlot(Cities.CHICAGO,
                new ArrayList<>(
                        Arrays.asList(Cities.SAN_FRANCISCO, Cities.LOS_ANGELES, Cities.MEXICO_CITY, Cities.ATLANTA, Cities.MONTREAL)
                ),
                Plagues.BLUE_PLAGUE, 340, 290));

        mapSlotList.add(new MapSlot(Cities.ATLANTA,
                new ArrayList<>(
                        Arrays.asList(Cities.CHICAGO, Cities.WASHINGTON, Cities.MIAMI)
                ),
                Plagues.BLUE_PLAGUE, 390, 380));

        mapSlotList.add(new MapSlot(Cities.MONTREAL,
                new ArrayList<>(
                        Arrays.asList(Cities.CHICAGO, Cities.NEW_YORK, Cities.WASHINGTON)
                ),
                Plagues.BLUE_PLAGUE, 460, 290));

        mapSlotList.add(new MapSlot(Cities.NEW_YORK,
                new ArrayList<>(
                        Arrays.asList(Cities.MONTREAL, Cities.WASHINGTON, Cities.LONDON, Cities.MADRID)
                ),
                Plagues.BLUE_PLAGUE, 570, 300));

        mapSlotList.add(new MapSlot(Cities.WASHINGTON,
                new ArrayList<>(
                        Arrays.asList(Cities.MIAMI, Cities.ATLANTA, Cities.MONTREAL, Cities.NEW_YORK)
                ),
                Plagues.BLUE_PLAGUE, 500, 380));

        mapSlotList.add(new MapSlot(Cities.LONDON,
                new ArrayList<>(
                        Arrays.asList(Cities.NEW_YORK, Cities.ESSEN, Cities.PARIS, Cities.MADRID)
                ),
                Plagues.BLUE_PLAGUE, 910, 210));

        mapSlotList.add(new MapSlot(Cities.ESSEN,
                new ArrayList<>(
                        Arrays.asList(Cities.LONDON, Cities.ST_PETERSBURG, Cities.MILAN, Cities.PARIS)
                ),
                Plagues.BLUE_PLAGUE, 1045, 190));

        mapSlotList.add(new MapSlot(Cities.ST_PETERSBURG,
                new ArrayList<>(
                        Arrays.asList(Cities.ESSEN, Cities.MOSCOW, Cities.ISTANBUL)
                ),
                Plagues.BLUE_PLAGUE, 1180, 180));

        mapSlotList.add(new MapSlot(Cities.PARIS,
                new ArrayList<>(
                        Arrays.asList(Cities.LONDON, Cities.ESSEN, Cities.MILAN, Cities.ALGIERS, Cities.MADRID)
                ),
                Plagues.BLUE_PLAGUE, 1000, 270));

        mapSlotList.add(new MapSlot(Cities.MADRID,
                new ArrayList<>(
                        Arrays.asList(Cities.NEW_YORK, Cities.LONDON, Cities.PARIS, Cities.ALGIERS, Cities.SAO_PAULO)
                ),
                Plagues.BLUE_PLAGUE, 890, 320));

        mapSlotList.add(new MapSlot(Cities.MILAN,
                new ArrayList<>(
                        Arrays.asList(Cities.ESSEN, Cities.ISTANBUL, Cities.PARIS)
                ),
                Plagues.BLUE_PLAGUE, 1100, 255));

        // Black Plague
        mapSlotList.add(new MapSlot(Cities.ALGIERS,
                new ArrayList<>(
                        Arrays.asList(Cities.MADRID, Cities.PARIS, Cities.ISTANBUL, Cities.CAIRO)
                ),
                Plagues.BLACK_PLAGUE, 1040, 400));

        mapSlotList.add(new MapSlot(Cities.ISTANBUL,
                new ArrayList<>(
                        Arrays.asList(Cities.MILAN, Cities.ST_PETERSBURG, Cities.MOSCOW, Cities.BAGHDAD, Cities.CAIRO, Cities.ALGIERS)
                ),
                Plagues.BLACK_PLAGUE, 1165, 325));

        mapSlotList.add(new MapSlot(Cities.CAIRO,
                new ArrayList<>(
                        Arrays.asList(Cities.ALGIERS, Cities.ISTANBUL, Cities.BAGHDAD, Cities.RIYADH, Cities.KHARTOUM)
                ),
                Plagues.BLACK_PLAGUE, 1140, 420));

        mapSlotList.add(new MapSlot(Cities.BAGHDAD,
                new ArrayList<>(
                        Arrays.asList(Cities.CAIRO, Cities.ISTANBUL, Cities.TEHRAN, Cities.KARACHI, Cities.RIYADH)
                ),
                Plagues.BLACK_PLAGUE, 1280, 370));

        mapSlotList.add(new MapSlot(Cities.MOSCOW,
                new ArrayList<>(
                        Arrays.asList(Cities.ST_PETERSBURG, Cities.TEHRAN, Cities.ISTANBUL)
                ),
                Plagues.BLACK_PLAGUE, 1280, 250));

        mapSlotList.add(new MapSlot(Cities.TEHRAN,
                new ArrayList<>(
                        Arrays.asList(Cities.MOSCOW, Cities.DELHI, Cities.KARACHI, Cities.BAGHDAD)
                ),
                Plagues.BLACK_PLAGUE, 1390, 300));

        mapSlotList.add(new MapSlot(Cities.RIYADH,
                new ArrayList<>(
                        Arrays.asList(Cities.CAIRO, Cities.BAGHDAD, Cities.KARACHI)
                ),
                Plagues.BLACK_PLAGUE, 1310, 500));

        mapSlotList.add(new MapSlot(Cities.KARACHI,
                new ArrayList<>(
                        Arrays.asList(Cities.RIYADH, Cities.BAGHDAD, Cities.TEHRAN, Cities.DELHI, Cities.MUMBAI)
                ),
                Plagues.BLACK_PLAGUE, 1430, 420));

        mapSlotList.add(new MapSlot(Cities.MUMBAI,
                new ArrayList<>(
                        Arrays.asList(Cities.KARACHI, Cities.DELHI, Cities.CHENNAI)
                ),
                Plagues.BLACK_PLAGUE, 1470, 510));

        mapSlotList.add(new MapSlot(Cities.CHENNAI,
                new ArrayList<>(
                        Arrays.asList(Cities.MUMBAI, Cities.DELHI, Cities.KOLKATA, Cities.BANGKOK, Cities.JAKARTA)
                ),
                Plagues.BLACK_PLAGUE, 1590, 580));

        mapSlotList.add(new MapSlot(Cities.DELHI,
                new ArrayList<>(
                        Arrays.asList(Cities.KARACHI, Cities.TEHRAN, Cities.KOLKATA, Cities.CHENNAI, Cities.MUMBAI)
                ),
                Plagues.BLACK_PLAGUE, 1550, 390));

        mapSlotList.add(new MapSlot(Cities.KOLKATA,
                new ArrayList<>(
                        Arrays.asList(Cities.DELHI, Cities.HONG_KONG, Cities.BANGKOK, Cities.CHENNAI)
                ),
                Plagues.BLACK_PLAGUE, 1640, 430));

        // Red Plague
        mapSlotList.add(new MapSlot(Cities.JAKARTA,
                new ArrayList<>(
                        Arrays.asList(Cities.CHENNAI, Cities.BANGKOK, Cities.HO_CHI_MINH_CITY, Cities.SYDNEY)
                ),
                Plagues.RED_PLAGUE, 1680, 710));

        mapSlotList.add(new MapSlot(Cities.BANGKOK,
                new ArrayList<>(
                        Arrays.asList(Cities.CHENNAI, Cities.KOLKATA, Cities.HONG_KONG, Cities.HO_CHI_MINH_CITY, Cities.JAKARTA)
                ),
                Plagues.RED_PLAGUE, 1695, 520));

        mapSlotList.add(new MapSlot(Cities.HO_CHI_MINH_CITY,
                new ArrayList<>(
                        Arrays.asList(Cities.BANGKOK, Cities.HONG_KONG, Cities.MANILA, Cities.JAKARTA)
                ),
                Plagues.RED_PLAGUE, 1790, 620));

        mapSlotList.add(new MapSlot(Cities.HONG_KONG,
                new ArrayList<>(
                        Arrays.asList(Cities.KOLKATA, Cities.SHANGHAI, Cities.TAIPEI, Cities.MANILA, Cities.HO_CHI_MINH_CITY, Cities.BANGKOK)
                ),
                Plagues.RED_PLAGUE, 1780, 460));

        mapSlotList.add(new MapSlot(Cities.SHANGHAI,
                new ArrayList<>(
                        Arrays.asList(Cities.BEIJING, Cities.SEOUL, Cities.TOKYO, Cities.TAIPEI, Cities.HONG_KONG)
                ),
                Plagues.RED_PLAGUE, 1770, 360));

        mapSlotList.add(new MapSlot(Cities.BEIJING,
                new ArrayList<>(
                        Arrays.asList(Cities.SEOUL, Cities.SHANGHAI)
                ),
                Plagues.RED_PLAGUE, 1760, 280));

        mapSlotList.add(new MapSlot(Cities.SEOUL,
                new ArrayList<>(
                        Arrays.asList(Cities.BEIJING, Cities.TOKYO, Cities.SHANGHAI)
                ),
                Plagues.RED_PLAGUE, 1860, 280));

        mapSlotList.add(new MapSlot(Cities.TOKYO,
                new ArrayList<>(
                        Arrays.asList(Cities.SEOUL, Cities.SAN_FRANCISCO, Cities.OSAKA, Cities.SHANGHAI)
                ),
                Plagues.RED_PLAGUE, 1930, 330));

        mapSlotList.add(new MapSlot(Cities.OSAKA,
                new ArrayList<>(
                        Arrays.asList(Cities.TOKYO, Cities.TAIPEI)
                ),
                Plagues.RED_PLAGUE, 1970, 410));

        mapSlotList.add(new MapSlot(Cities.TAIPEI,
                new ArrayList<>(
                        Arrays.asList(Cities.SHANGHAI, Cities.OSAKA, Cities.MANILA, Cities.HONG_KONG)
                ),
                Plagues.RED_PLAGUE, 1880, 450));

        mapSlotList.add(new MapSlot(Cities.MANILA,
                new ArrayList<>(
                        Arrays.asList(Cities.HONG_KONG, Cities.TAIPEI, Cities.SAN_FRANCISCO, Cities.SYDNEY, Cities.HO_CHI_MINH_CITY)
                ),
                Plagues.RED_PLAGUE, 1940, 600));

        mapSlotList.add(new MapSlot(Cities.SYDNEY,
                new ArrayList<>(
                        Arrays.asList(Cities.JAKARTA, Cities.MANILA, Cities.LOS_ANGELES)
                ),
                Plagues.RED_PLAGUE, 2010, 870));

        // Yellow Plague
        mapSlotList.add(new MapSlot(Cities.LOS_ANGELES,
                new ArrayList<>(
                        Arrays.asList(Cities.SYDNEY, Cities.SAN_FRANCISCO, Cities.CHICAGO, Cities.MEXICO_CITY)
                ),
                Plagues.YELLOW_PLAGUE, 170, 450));

        mapSlotList.add(new MapSlot(Cities.MEXICO_CITY,
                new ArrayList<>(
                        Arrays.asList(Cities.LOS_ANGELES, Cities.CHICAGO, Cities.MIAMI, Cities.BOGOTA, Cities.LIMA)
                ),
                Plagues.YELLOW_PLAGUE, 290, 490));

        mapSlotList.add(new MapSlot(Cities.MIAMI,
                new ArrayList<>(
                        Arrays.asList(Cities.ATLANTA, Cities.WASHINGTON, Cities.BOGOTA, Cities.MEXICO_CITY)
                ),
                Plagues.YELLOW_PLAGUE, 470, 465));

        mapSlotList.add(new MapSlot(Cities.BOGOTA,
                new ArrayList<>(
                        Arrays.asList(Cities.MEXICO_CITY, Cities.MIAMI, Cities.SAO_PAULO, Cities.BUENOS_AIRES, Cities.LIMA)
                ),
                Plagues.YELLOW_PLAGUE, 440, 610));

        mapSlotList.add(new MapSlot(Cities.LIMA,
                new ArrayList<>(
                        Arrays.asList(Cities.MEXICO_CITY, Cities.BOGOTA, Cities.SANTIAGO)
                ),
                Plagues.YELLOW_PLAGUE, 380, 760));

        mapSlotList.add(new MapSlot(Cities.SANTIAGO,
                new ArrayList<>(
                        List.of(Cities.LIMA)
                ),
                Plagues.YELLOW_PLAGUE, 420, 940));

        mapSlotList.add(new MapSlot(Cities.BUENOS_AIRES,
                new ArrayList<>(
                        Arrays.asList(Cities.BOGOTA, Cities.SAO_PAULO)
                ),
                Plagues.YELLOW_PLAGUE, 590, 910));

        mapSlotList.add(new MapSlot(Cities.SAO_PAULO,
                new ArrayList<>(
                        Arrays.asList(Cities.BOGOTA, Cities.MADRID, Cities.LAGOS, Cities.BUENOS_AIRES)
                ),
                Plagues.YELLOW_PLAGUE, 650, 810));

        mapSlotList.add(new MapSlot(Cities.LAGOS,
                new ArrayList<>(
                        Arrays.asList(Cities.KHARTOUM, Cities.KINSHASA, Cities.SAO_PAULO)
                ),
                Plagues.YELLOW_PLAGUE, 1000, 585));

        mapSlotList.add(new MapSlot(Cities.KINSHASA,
                new ArrayList<>(
                        Arrays.asList(Cities.LAGOS, Cities.KHARTOUM, Cities.JOHANNESBURG)
                ),
                Plagues.YELLOW_PLAGUE, 1090, 690));

        mapSlotList.add(new MapSlot(Cities.JOHANNESBURG,
                new ArrayList<>(
                        Arrays.asList(Cities.KINSHASA, Cities.KHARTOUM)
                ),
                Plagues.YELLOW_PLAGUE, 1180, 850));

        mapSlotList.add(new MapSlot(Cities.KHARTOUM,
                new ArrayList<>(
                        Arrays.asList(Cities.CAIRO, Cities.JOHANNESBURG, Cities.KINSHASA, Cities.LAGOS)
                ),
                Plagues.YELLOW_PLAGUE, 1200, 550));

        return mapSlotList;
    }

    /**
     * Returns a set of all available MapSlots.
     *
     * @return a set containing all {@link MapSlot} objects
     * @see MapSlot
     * @author David Scheffler
     * @since 2024-09-22
     */
    public Set<MapSlot> getAllMapSlots() {
        return Set.copyOf(createMapSlotList());
    }
}
