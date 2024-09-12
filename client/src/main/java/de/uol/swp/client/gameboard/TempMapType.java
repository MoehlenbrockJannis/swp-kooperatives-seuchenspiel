package de.uol.swp.client.gameboard;

import javafx.scene.paint.Color;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

// TODO replace client.gameboard.TempMapType with common.map.MapType

/**
 * Temporary class to test functionality of {@link WorldMapPresenter} and {@link CityMarker}.
 * This class defines all cites found in the original game and imitates the functionality of
 * {@link de.uol.swp.common.map.MapType} and should be replaced with it. The coordinates defined
 * for each city only apply to the gameboard-world-map.svg in /resources/images.
 *
 * @author David Scheffler
 * @see de.uol.swp.common.map.MapType
 * @since 2024-09-10
 */
public class TempMapType {
    private final Object[][] CITIES = {
            {"San Francisco", Color.BLUE, 170, 330},
            {"Chicago", Color.BLUE, 340, 290},
            {"Atlanta", Color.BLUE, 390, 380},
            {"Montreal", Color.BLUE, 460, 290},
            {"New York", Color.BLUE, 570, 300},
            {"Washington", Color.BLUE, 500, 380},
            {"London", Color.BLUE, 910, 210},
            {"Essen", Color.BLUE, 1045, 190},
            {"St. Petersburg", Color.BLUE, 1180, 180},
            {"Paris", Color.BLUE, 1000, 270},
            {"Madrid", Color.BLUE, 890, 320},
            {"Algier", Color.BLACK, 1040, 400},
            {"Istanbul", Color.BLACK, 1165, 325},
            {"Kairo", Color.BLACK, 1140, 420},
            {"Bagdad", Color.BLACK, 1280, 370},
            {"Moskau", Color.BLACK, 1280, 250},
            {"Teheran", Color.BLACK, 1390, 300},
            {"Riad", Color.BLACK, 1310, 500},
            {"Karatschi", Color.BLACK, 1430, 420},
            {"Mumbai", Color.BLACK, 1470, 510},
            {"Chennai", Color.BLACK, 1590, 580},
            {"Delhi", Color.BLACK, 1550, 390},
            {"Kalkuta", Color.BLACK, 1640, 430},
            {"Jakarta", Color.RED, 1680, 710},
            {"Bangkok", Color.RED, 1695, 520},
            {"Ho-Chi-Minh-Stadt", Color.RED, 1790, 620},
            {"Hongkong", Color.RED, 1780, 460},
            {"Shanghai", Color.RED, 1770, 360},
            {"Peking", Color.RED, 1760, 280},
            {"Seoul", Color.RED, 1860, 280},
            {"Tokio", Color.RED, 1930, 330},
            {"Osaka", Color.RED, 1970, 410},
            {"Taipeh", Color.RED, 1880, 450},
            {"Manila", Color.RED, 1940, 600},
            {"Sydney", Color.RED, 2010, 870},
            {"Los Angeles", Color.YELLOW, 170, 450},
            {"Mexiko Stadt", Color.YELLOW, 290, 490},
            {"Miami", Color.YELLOW, 470, 465},
            {"Bogotá", Color.YELLOW, 440, 610},
            {"Lima", Color.YELLOW, 380, 760},
            {"Santiago", Color.YELLOW, 420, 940},
            {"Buenos-Aires", Color.YELLOW, 590, 910},
            {"São Paulo", Color.YELLOW, 650, 810},
            {"Lagos", Color.YELLOW, 1000, 585},
            {"Kinshasa", Color.YELLOW, 1090, 690},
            {"Johannesburg", Color.YELLOW, 1180, 850},
            {"Khartum", Color.YELLOW, 1200, 550}
    };

    @Getter
    private final List<TempMapSlot> MAP_SLOT_LIST = new ArrayList<>();

    public TempMapType() {
        for (Object[] city : CITIES) {
            MAP_SLOT_LIST.add(new TempMapSlot((String) city[0], (Color) city[1], (Integer) city[2], (Integer) city[3]));
        }
    }
}
