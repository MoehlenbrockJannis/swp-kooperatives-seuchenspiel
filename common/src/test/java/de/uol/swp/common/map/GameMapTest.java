package de.uol.swp.common.map;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.exception.FieldOfCityNotFoundException;
import de.uol.swp.common.map.exception.StartingFieldNotFoundException;
import de.uol.swp.common.marker.OutbreakMarker;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.plague.PlagueCube;
import de.uol.swp.common.util.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class GameMapTest {
    private GameMap map;
    private Game game;
    private MapType mapType;
    private List<MapSlot> mapSlots;
    private MapSlot mapSlot1;
    private MapSlot mapSlot2;
    private MapSlot mapSlot3;
    private City city1;
    private City city2;
    private City city3;
    private Plague plague;
    private int numberOfPlagueCubes;
    private List<Field> infectedFields;

    @BeforeEach
    void setUp() {
        plague = new Plague("plague", new Color(10, 10, 10));

        city1 = new City("1", "");
        city2 = new City("2", "");
        city3 = new City("3", "");

        mapSlot1 = new MapSlot(city1, List.of(city2, city3), plague, 0, 2);
        mapSlot2 = new MapSlot(city2, List.of(city1, city3), plague, 1, 1);
        mapSlot3 = new MapSlot(city3, List.of(city1, city2), plague, 2, 0);

        mapSlots = List.of(
                mapSlot1,
                mapSlot2,
                mapSlot3
        );


        numberOfPlagueCubes = 3;

        game = mock(Game.class);

        OutbreakMarker outbreakMarker = mock(OutbreakMarker.class);
        when(outbreakMarker.isAtMaximumLevel()).thenReturn(false);
        when(game.getOutbreakMarker()).thenReturn(outbreakMarker);

        when(game.getPlagueCubeOfPlague(plague))
                .thenReturn(new PlagueCube(plague));
        when(game.getMaxNumberOfPlagueCubesPerField())
                .thenReturn(numberOfPlagueCubes);
        mapType = mock(MapType.class);
        when(mapType.getMap())
                .thenReturn(mapSlots);

        map = new GameMap(game, mapType);

        infectedFields = new ArrayList<>();
    }

    @Test
    @DisplayName("Should return field with specified starting city of map type")
    void getStartingField() {
        when(mapType.getStartingCity())
                .thenReturn(city1);

        final Field equalField = new Field(map, mapSlot1);

        assertThat(map.getStartingField())
                .usingRecursiveComparison()
                .isEqualTo(equalField);
    }

    @Test
    @DisplayName("Should throw an exception if no field with starting city is found")
    void getStartingField_error() {
        final City city = new City("city", "");

        when(mapType.getStartingCity())
                .thenReturn(city);

        assertThatThrownBy(() -> map.getStartingField())
                .isInstanceOf(StartingFieldNotFoundException.class);
    }

    @Test
    @DisplayName("Should return specified number of plague cubes per field")
    void getMaxNumberOfPlagueCubesPerField() {
        assertThat(map.getMaxNumberOfPlagueCubesPerField())
                .isEqualTo(numberOfPlagueCubes);
    }

    @Test
    @DisplayName("Should return a plague cube of given plague")
    void getPlagueCubeOfPlague() {
        final PlagueCube plagueCube = new PlagueCube(plague);

        when(game.getPlagueCubeOfPlague(plague))
                .thenReturn(plagueCube);

        assertThat(map.getPlagueCubeOfPlague(plague))
                .usingRecursiveComparison()
                .isEqualTo(plagueCube);
    }

    @Test
    @DisplayName("Should start one outbreak on game and place plague cubes on neighboring fields")
    void startOutbreak_1() {
        final List<Field> fields = map.getFields();
        final Field field1 = fields.get(0);
        final Field field2 = fields.get(1);
        final Field field3 = fields.get(2);

        assertThat(field2.isCurable(plague))
                .isFalse();
        assertThat(field3.isCurable(plague))
                .isFalse();

        map.startOutbreak(field1, plague, new ArrayList<>());

        verify(game, times(1))
                .startOutbreak();
        assertThat(field2.isCurable(plague))
                .isTrue();
        assertThat(field3.isCurable(plague))
                .isTrue();
    }

    @Test
    @DisplayName("Should start two outbreaks on game and place plague cubes on neighboring fields")
    void startOutbreak_2() {
        final List<Field> fields = map.getFields();
        final Field field1 = fields.get(0);
        final Field field2 = fields.get(1);
        final Field field3 = fields.get(2);

        for (int i = 0; i < game.getMaxNumberOfPlagueCubesPerField(); i++) {
            field2.infectField(new PlagueCube(plague), new ArrayList<>());
        }

        assertThat(field3.isCurable(plague))
                .isFalse();

        map.startOutbreak(field1, plague, new ArrayList<>());

        verify(game, times(2))
                .startOutbreak();
        assertThat(field3.isCurable(plague))
                .isTrue();
    }

    @Test
    @DisplayName("Should start two outbreaks on game and place plague cubes on neighboring fields as well as add infected fields to list")
    void start_two_outbreaks_with_infectedFields_list() {
        final List<Field> fields = map.getFields();
        final Field field1 = fields.get(0);
        final Field field2 = fields.get(1);
        final Field field3 = fields.get(2);

        for (int i = 0; i < game.getMaxNumberOfPlagueCubesPerField(); i++) {
            field2.infectField(new PlagueCube(plague), infectedFields);
        }

        assertThat(field3.isCurable(plague))
                .isFalse();

        assertThat(infectedFields.contains(field2))
                .isTrue();

        map.startOutbreak(field1, plague, infectedFields);

        verify(game, times(2))
                .startOutbreak();
        assertThat(field3.isCurable(plague))
                .isTrue();

        assertThat(infectedFields.contains(field1))
                .isTrue();

    }

    @Test
    @DisplayName("Should return a list of fields neighboring the given field excluding the fields that are given")
    void getNeighborFieldsExcludingFields() {
        final Field field = new Field(map, mapSlot1);
        final List<Field> neighbors = List.of(
                new Field(map, mapSlot2)
        );
        final List<Field> excluded = List.of(
                new Field(map, mapSlot3)
        );

        assertThat(map.getNeighborFieldsExcludingFields(field, excluded))
                .usingRecursiveComparison()
                .isEqualTo(neighbors);
    }

    @Test
    @DisplayName("Should return a list of fields neighboring the given field")
    void getNeighborFields() {
        final Field field = new Field(map, mapSlot1);
        final List<Field> neighbors = List.of(
                new Field(map, mapSlot2),
                new Field(map, mapSlot3)
        );

        assertThat(map.getNeighborFields(field))
                .usingRecursiveComparison()
                .isEqualTo(neighbors);
    }

    @Test
    @DisplayName("Should return true if there is an antidote marker on game for given plague and there are no plague cubes of given plague on map")
    void isPlagueExterminated_true() {
        when(game.hasAntidoteMarkerForPlague(plague))
                .thenReturn(true);

        assertThat(map.isPlagueExterminated(plague))
                .isTrue();
    }

    @Test
    @DisplayName("Should return false if there is no antidote marker on game for given plague")
    void isPlagueExterminated_falseNoAntidote() {
        assertThat(map.isPlagueExterminated(plague))
                .isFalse();
    }

    @Test
    @DisplayName("Should return false if there is an antidote marker on game for given plague but there are plague cubes of given plague on map")
    void isPlagueExterminated_falseStillCubes() {
        when(game.hasAntidoteMarkerForPlague(plague))
                .thenReturn(true);

        map.getFields().get(0).infectField(new PlagueCube(plague), new ArrayList<>());

        assertThat(map.isPlagueExterminated(plague))
                .isFalse();
    }

    @Test
    @DisplayName("Should return specified map type")
    void getType() {
        assertThat(map.getType())
                .usingRecursiveComparison()
                .isEqualTo(mapType);
    }

    @Test
    @DisplayName("Should return the field of the given city")
    void getFieldOfCity() {
        final Field expectedField = new Field(map, mapSlot1);

        assertThat(map.getFieldOfCity(city1))
                .usingRecursiveComparison()
                .isEqualTo(expectedField);
    }

    @Test
    @DisplayName("Should throw an exception if no field for the given city is found")
    void getFieldOfCity_error() {
        final City nonExistentCity = new City("non-existent", "");

        assertThatThrownBy(() -> map.getFieldOfCity(nonExistentCity))
                .isInstanceOf(FieldOfCityNotFoundException.class)
                .hasMessageContaining(nonExistentCity.getName());
    }

    @Test
    @DisplayName("Should return list of fields")
    void getFields() {
        final List<Field> fields = new ArrayList<>();
        for (final MapSlot mapSlot : mapSlots) {
            fields.add(new Field(map, mapSlot));
        }

        assertThat(map.getFields())
                .usingRecursiveComparison()
                .isEqualTo(fields);
    }

    @Test
    @DisplayName("Should add given PlagueCube back to game")
    void addPlagueCube() {
        final PlagueCube plagueCube = new PlagueCube(plague);

        final Map<Plague, List<PlagueCube>> plagueCubes = Map.ofEntries(Map.entry(plague, new ArrayList<>()));
        when(game.getPlagueCubes())
                .thenReturn(plagueCubes);
        doAnswer(invocationOnMock -> {
            final PlagueCube argument = invocationOnMock.getArgument(0);
            plagueCubes.get(argument.getPlague()).add(argument);
            return null;
        }).when(game).addPlagueCube(any(PlagueCube.class));

        final List<PlagueCube> plagueCubesOnGame = game.getPlagueCubes().get(plague);
        final int numberOfPlagueCubesOfPlagueOnGame = plagueCubesOnGame.size();

        map.addPlagueCube(plagueCube);

        assertThat(plagueCubesOnGame)
                .hasSize(numberOfPlagueCubesOfPlagueOnGame + 1);
    }
}