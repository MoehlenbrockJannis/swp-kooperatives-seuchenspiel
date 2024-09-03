package de.uol.swp.common.map;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.exception.StartingFieldNotFoundException;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.plague.PlagueCube;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Instantiation of a map defined by {@link MapType}
 *
 * <p>
 *     Objects of this class are used in running games to store information about the map using fields.
 * </p>
 *
 * @see Field
 * @see Game
 * @see MapType
 * @author Tom Weelborg
 * @since 2024-09-02
 */
public class GameMap {
    private Game game;
    private MapType type;
    @Getter
    private List<Field> fields;

    /**
     * Constructor
     *
     * @param game Game this {@link GameMap} is attached to
     * @param mapType MapType this {@link GameMap} is an instantiation of
     */
    public GameMap(final Game game, final MapType mapType) {
        this.game = game;
        this.type = mapType;
        createFields();
    }

    /**
     * Creates one {@link Field} for every {@link MapSlot} stored on the {@link MapType} of this {@link GameMap}
     *
     * @see Field
     * @see MapSlot
     * @see MapType
     */
    private void createFields() {
        this.fields = new ArrayList<>();

        final List<MapSlot> mapSlots = type.getMap();
        for (final MapSlot mapSlot : mapSlots) {
            final Field field = new Field(this, mapSlot);
            this.fields.add(field);
        }
    }

    /**
     * Returns the field all players start on by searching the {@link Field} with the {@link City} specified by {@link #type} as the starting city.
     *
     * @return Field all players start on
     * @throws StartingFieldNotFoundException when the starting field cannot be found
     */
    public Field getStartingField() throws StartingFieldNotFoundException {
        final City startingCity = type.getStartingCity();
        for (final Field field : this.fields) {
            if (field.getCity().equals(startingCity)) {
                return field;
            }
        }

        throw new StartingFieldNotFoundException(startingCity.getName());
    }

    /**
     * @return Max amount of {@link PlagueCube} allowed per {@link Field} as specified by the {@link Game} options
     * @see Game#getMaxNumberOfPlagueCubesPerField()
     * @see PlagueCube
     */
    public int getMaxNumberOfPlagueCubesPerField() {
        return game.getMaxNumberOfPlagueCubesPerField();
    }

    /**
     * Returns one {@link PlagueCube} stored on the attached {@link #game}
     *
     * @param plague Plague to search PlagueCube for
     * @return PlagueCube of given plague
     * @see Game#getPlagueCubeOfPlague(Plague)
     * @see Plague
     * @see PlagueCube
     */
    public PlagueCube getPlagueCubeOfPlague(final Plague plague) {
        return game.getPlagueCubeOfPlague(plague);
    }

    /**
     * Starts an outbreak on the given {@link Field}
     *
     * <p>
     *     Calls the {@link Game} method to start an outbreak.
     *     Infects all neighboring fields with a {@link PlagueCube} of the associated {@link Plague}.
     *     Handles outbreaks on neighboring fields.
     *     Prevents the same field from breaking out more than once.
     * </p>
     *
     * @param field Field of the outbreak
     * @param plague Outbreaking plague
     * @see Field
     * @see Game#startOutbreak()
     * @see Plague
     * @see PlagueCube
     */
    public void startOutbreak(final Field field, final Plague plague) {
        final List<Field> outbreakingFields = new ArrayList<>();
        outbreakingFields.add(field);

        for (int i = 0; i < outbreakingFields.size(); i++) {
            final Field outbreakingField = outbreakingFields.get(i);

            game.startOutbreak();

            final List<Field> neighborFields = getNeighborFieldsExcludingFields(outbreakingField, outbreakingFields);
            for (final Field neighborField : neighborFields) {
                if (!neighborField.isInfectable(plague)) {
                    outbreakingFields.add(neighborField);
                } else {
                    final PlagueCube plagueCube = game.getPlagueCubeOfPlague(plague);
                    neighborField.infect(plagueCube);
                }
            }
        }
    }

    /**
     * Returns a list of all neighboring fields of a given field excluding all fields in excludedFields
     *
     * @param field Field the neighbors are searched for
     * @param excludedFields Fields not in returned list
     * @return List of all neighboring fields of field excluding excludedFields
     * @see Field
     */
    public List<Field> getNeighborFieldsExcludingFields(final Field field, final List<Field> excludedFields) {
        final List<Field> neighborFields = new ArrayList<>();

        final List<City> neighborCities = field.getConnectedCities();

        for (final Field f : this.fields) {
            if (neighborCities.contains(f.getCity())) {
                neighborFields.add(f);
            }
        }

        return neighborFields.stream()
                .filter(neighborField -> !excludedFields.contains(neighborField))
                .toList();
    }
}
