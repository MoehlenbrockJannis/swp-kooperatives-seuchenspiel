package de.uol.swp.common.map;

import de.uol.swp.common.map.exception.NoPlagueCubesOfPlagueOnFieldException;
import de.uol.swp.common.map.exception.ResearchLaboratoryAlreadyBuiltOnFieldException;
import de.uol.swp.common.map.research_laboratory.ResearchLaboratory;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.plague.PlagueCube;
import de.uol.swp.common.util.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class FieldTest {
    private Field field;
    private GameMap map;
    private MapSlot mapSlot;
    private City city;
    private String cityName;
    private List<City> connectedCities;
    private Plague plague;
    private Set<Plague> plagueSet;
    private int maxNumberOfPlagueCubes;
    private List<Field> infectedFields;

    @BeforeEach
    void setUp() {
        cityName = "city";
        city = new City(cityName, "");
        connectedCities = List.of(
                new City("a", ""),
                new City("b", ""),
                new City("c", "")
        );
        plague = new Plague("tdd", new Color(255, 255, 255));

        plagueSet = new HashSet<>();
        plagueSet.add(plague);

        maxNumberOfPlagueCubes = 3;

        final MapType mapType = mock(MapType.class);
        when(mapType.getUniquePlagues())
                .thenReturn(plagueSet);
        map = mock(GameMap.class);
        when(map.getMaxNumberOfPlagueCubesPerField())
                .thenReturn(maxNumberOfPlagueCubes);
        when(map.getType())
                .thenReturn(mapType);
        mapSlot = new MapSlot(city, connectedCities, plague, 0, 0);

        field = new Field(map, mapSlot);

        infectedFields = new ArrayList<>();
    }

    @Test
    void testToString() {
        assertThat(field)
                .hasToString(cityName);
    }

    @Test
    @DisplayName("Should return specified city of associated map slot")
    void getCity() {
        assertThat(field.getCity())
                .usingRecursiveComparison()
                .isEqualTo(city);
    }

    @Test
    @DisplayName("Should return specified connected cities of associated map slot")
    void getConnectedCities() {
        assertThat(field.getConnectedCities())
                .usingRecursiveComparison()
                .isEqualTo(connectedCities);
    }

    @Test
    @DisplayName("Should return specified plague of associated map slot")
    void getPlague() {
        assertThat(field.getPlague())
                .usingRecursiveComparison()
                .isEqualTo(plague);
    }

    @Test
    @DisplayName("Should infectField field with a plague cube of associated plague from map")
    void infectField() {
        assertThat(field.isCurable(plague))
                .isFalse();

        when(map.getPlagueCubeOfPlague(plague))
                .thenReturn(new PlagueCube(plague));

        field.infectField();

        assertThat(field.isCurable(plague))
                .isTrue();
    }


    @Test
    @DisplayName("Should infect field with a plague cube of associated plague from map and add infected field to list")
    void infect_Field_and_add_to_list() {
        assertThat(infectedFields)
                .isEmpty();

        PlagueCube plagueCube = new PlagueCube(plague);
        field.infectField(plagueCube, infectedFields);

        assertThat(field.isCurable(plague))
                .isTrue();

        assertThat(infectedFields)
                .contains(field);
    }

    @Test
    @DisplayName("Should infectField field with given plague cube")
    void infect_Field_PlagueCube_infectable() {
        assertThat(field.isCurable(plague))
                .isFalse();

        field.infectField(new PlagueCube(plague), infectedFields);

        assertThat(field.isCurable(plague))
                .isTrue();
    }

    @Test
    @DisplayName("Should infectField field with given plague cube and add infected field to list")
    void infect_Field_PlagueCube_infectable_and_add_to_list() {
        assertThat(field.isCurable(plague))
                .isFalse();

        assertThat(infectedFields).
                isEmpty();

        field.infectField(new PlagueCube(plague), infectedFields);

        assertThat(field.isCurable(plague))
                .isTrue();

        assertThat(infectedFields)
                .contains(field);
    }

    @Test
    @DisplayName("Should start an outbreak if field is not infectable with plague of given plague cube")
    void infect_Field_PlagueCube_outbreak() {
        for (int i = 0; i < maxNumberOfPlagueCubes; i++) {
            field.infectField(new PlagueCube(plague), infectedFields);
        }

        assertThat(field.isInfectable(plague))
                .isFalse();

        field.infectField(new PlagueCube(plague), infectedFields);

        verify(map, times(1))
                .startOutbreak(field, plague, infectedFields);
    }

    @Test
    @DisplayName("Should be infectable if given plague is not stored on field")
    void isInfectable_truePlagueNotStored() {
        assertThat(field.isInfectable(plague))
                .isTrue();
    }

    @Test
    @DisplayName("Should be infectable if there are less than the maximum amount of plague cubes of given plague stored on field")
    void isInfectable_truePlagueUnderLimit() {
        for (int i = 0; i < maxNumberOfPlagueCubes - 1; i++) {
            field.infectField(new PlagueCube(plague), infectedFields);
        }

        assertThat(field.isInfectable(plague))
                .isTrue();
    }

    @Test
    @DisplayName("Should not be infectable if there are the maximum amount of plague cubes of given plague stored on field")
    void isInfectable_false() {
        for (int i = 0; i < maxNumberOfPlagueCubes; i++) {
            field.infectField(new PlagueCube(plague), infectedFields);
        }

        assertThat(field.isInfectable(plague))
                .isFalse();
    }

    @Test
    @DisplayName("Should reduce the number of plague cubes of given plague by one")
    void cure() {
        field.infectField(new PlagueCube(plague), infectedFields);

        assertThat(field.isCurable(plague))
                .isTrue();

        field.cure(plague);

        assertThat(field.isCurable(plague))
                .isFalse();
    }

    @Test
    @DisplayName("Should throw an exception if there are no plague cubes of given plague on field")
    void cure_error() {
        assertThatThrownBy(() -> field.cure(plague))
                .isInstanceOf(NoPlagueCubesOfPlagueOnFieldException.class);
    }

    @Test
    @DisplayName("Should return true if there is at least one plague cube of given plague on field")
    void isCurable_true() {
        field.infectField(new PlagueCube(plague), infectedFields);

        assertThat(field.isCurable(plague))
                .isTrue();
    }

    @Test
    @DisplayName("Should return false if there is no plague cube of given plague on field")
    void isCurable_false() {
        assertThat(field.isCurable(plague))
                .isFalse();
    }

    @Test
    @DisplayName("Should add given research laboratory to field if it does not have one")
    void buildResearchLaboratory() {
        assertThat(field.hasResearchLaboratory())
                .isFalse();

        field.buildResearchLaboratory(new ResearchLaboratory());

        assertThat(field.hasResearchLaboratory())
                .isTrue();
    }

    @Test
    @DisplayName("Should throw exception if field already has a research laboratory")
    void buildResearchLaboratory_error() {
        field.buildResearchLaboratory(new ResearchLaboratory());

        final ResearchLaboratory researchLaboratory = new ResearchLaboratory();

        assertThatThrownBy(() -> field.buildResearchLaboratory(researchLaboratory))
                .isInstanceOf(ResearchLaboratoryAlreadyBuiltOnFieldException.class);
    }

    @Test
    @DisplayName("Should remove and return research laboratory of field if it has one")
    void removeResearchLaboratory() {
        final ResearchLaboratory researchLaboratory = new ResearchLaboratory();

        field.buildResearchLaboratory(researchLaboratory);

        assertThat(field.removeResearchLaboratory())
                .usingRecursiveComparison()
                .isEqualTo(researchLaboratory);
    }

    @Test
    @DisplayName("Should throw exception if field does not have a research laboratory")
    void removeResearchLaboratory_error() {
        assertThatThrownBy(() -> field.removeResearchLaboratory())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Should return true if there is a research laboratory on field")
    void hasResearchLaboratory_true() {
        field.buildResearchLaboratory(new ResearchLaboratory());

        assertThat(field.hasResearchLaboratory())
                .isTrue();
    }

    @Test
    @DisplayName("Should return false if there is no research laboratory on field")
    void hasResearchLaboratory_false() {
        assertThat(field.hasResearchLaboratory())
                .isFalse();
    }

    @Test
    @DisplayName("Should return true if given city is equal to city of the field")
    void hasCity() {
        assertThat(field.hasCity(city))
                .isTrue();
    }

    @Test
    @DisplayName("Should return the same as equivalent method on associated map slot")
    void hasPlague() {
        final boolean result = true;

        assertThat(field.hasPlague(plague))
                .isEqualTo(result);
    }

    @Test
    @DisplayName("Should return neighboring fields of this field")
    void getNeighborFields() {
        final List<Field> neighbors = List.of(
                new Field(map, mock(MapSlot.class)),
                new Field(map, mock(MapSlot.class)),
                new Field(map, mock(MapSlot.class))
        );

        when(map.getNeighborFields(field))
                .thenReturn(neighbors);

        assertThat(field.getNeighborFields())
                .containsExactlyInAnyOrderElementsOf(neighbors);
    }

    @Test
    @DisplayName("Should return all plague cubes of the given plague")
    void getPlagueCubesOfPlague() {
        PlagueCube plagueCube1 = new PlagueCube(plague);
        PlagueCube plagueCube2 = new PlagueCube(plague);

        field.infectField(plagueCube1, infectedFields);
        field.infectField(plagueCube2, infectedFields);

        List<PlagueCube> plagueCubes = field.getPlagueCubesOfPlague(plague);

        assertThat(plagueCubes)
                .containsExactlyInAnyOrder(plagueCube1, plagueCube2);
    }

    @Test
    @DisplayName("Should return the number of foreign plague cube types on the field")
    void getNumberOfForeignPlagueCubeTypes() {
        Plague foreignPlague1 = new Plague("ForeignPlague1", new Color(1, 2, 3));
        Plague foreignPlague2 = new Plague("ForeignPlague2", new Color(10, 20, 30));

        field.infectField(new PlagueCube(foreignPlague1), infectedFields);
        field.infectField(new PlagueCube(foreignPlague2), infectedFields);
        field.infectField(new PlagueCube(plague), infectedFields);

        int numberOfForeignPlagueTypes = field.getNumberOfForeignPlagueCubeTypes();

        assertThat(numberOfForeignPlagueTypes)
                .isEqualTo(2);
    }

    @Test
    @DisplayName("Should return the number of plague cubes for each plague on the field")
    void getPlagueCubeAmounts() {
        Plague foreignPlague = new Plague("ForeignPlague", new Color(1, 2, 3));

        field.infectField(new PlagueCube(plague), infectedFields);
        field.infectField(new PlagueCube(plague), infectedFields);
        field.infectField(new PlagueCube(foreignPlague), infectedFields);

        Map<Plague, Integer> plagueCubeAmounts = field.getPlagueCubeAmounts();

        assertThat(plagueCubeAmounts)
                .containsEntry(plague, 2)
                .containsEntry(foreignPlague, 1);
    }

    @Test
    @DisplayName("Should return true if given field has equal map and map slot")
    void testEquals_true() {
        final Field equal = new Field(map, mapSlot);

        assertThat(field.equals(equal))
                .isTrue();
    }

    @Test
    @DisplayName("Should return false if given field does not have equal map or map slot")
    void testEquals_falseNotEqual() {
        final MapType newMapType = mock(MapType.class);
        when(newMapType.getUniquePlagues())
                .thenReturn(plagueSet);
        final GameMap newMap = mock(GameMap.class);
        when(newMap.getType())
                .thenReturn(newMapType);
        final MapSlot newMapSlot = mock(MapSlot.class);
        final Field notEqual = new Field(newMap, newMapSlot);

        assertThat(field.equals(notEqual))
                .isFalse();
    }

    @Test
    @DisplayName("Should return false if given object is not a field")
    void testEquals_falseWrongType() {
        final Object notEqual = new Object();

        assertThat(field.equals(notEqual))
                .isFalse();
    }

    @Test
    @DisplayName("Should return the same hash code for equal objects")
    void testHashCode() {
        final Field equal = new Field(map, mapSlot);

        assertThat(field)
                .hasSameHashCodeAs(equal);
    }
}