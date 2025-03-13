package de.uol.swp.common.map;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.exception.FieldOfCityNotFoundException;
import de.uol.swp.common.map.exception.StartingFieldNotFoundException;
import de.uol.swp.common.marker.AntidoteMarker;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.plague.PlagueCube;
import de.uol.swp.common.plague.exception.NoPlagueCubesFoundException;
import de.uol.swp.common.player.Player;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

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
 */
@EqualsAndHashCode
public class GameMap implements Serializable {
    private Game game;
    @Getter
    private MapType type;
    @EqualsAndHashCode.Exclude
    @Getter
    private List<Field> fields;
    @Setter
    private transient BiConsumer<Game, Field> outbreakCallback;

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
     * Adds given {@link PlagueCube} back to {@link #game}
     *
     * @param plagueCube {@link PlagueCube} to add to {@link #game}
     */
    public void addPlagueCube(final PlagueCube plagueCube) {
        game.addPlagueCube(plagueCube);
    }

    /**
     * Starts an outbreak chain on the given field with the specified plague.
     * This is the entry point for outbreak handling.
     *
     * @param field Field where the outbreak starts
     * @param plague Type of plague causing the outbreak
     * @param infectedFields List to track all fields that get infected during this outbreak chain
     */
    public void startOutbreak(final Field field, final Plague plague, List<Field> infectedFields) {
        List<Field> fieldsWithOutbreak = new ArrayList<>();
        startOutbreakRecursive(field, plague, infectedFields, fieldsWithOutbreak);
    }

    /**
     * Recursively handles the outbreak chain, tracking both infected and outbreak fields.
     * Prevents infinite loops by ensuring each field only outbreaks once per chain.
     *
     * @param field Current field experiencing an outbreak
     * @param plague Type of plague causing the outbreak
     * @param infectedFields Tracks all fields that get infected during this outbreak chain
     * @param fieldsWithOutbreak Tracks fields that have already had an outbreak to prevent cycles
     */
    private void startOutbreakRecursive(Field field, Plague plague, List<Field> infectedFields, List<Field> fieldsWithOutbreak) {
        if (fieldsWithOutbreak.contains(field)) {
            return;
        }

        if (game.getOutbreakMarker().isAtMaximumLevel()) {
            return;
        }

        if (outbreakCallback != null) {
            outbreakCallback.accept(game, field);
        }

        fieldsWithOutbreak.add(field);
        infectedFields.add(field);

        game.startOutbreak();

        if (game.isGameLost()) {
            return;
        }

        List<Field> neighborFields = getNeighborFields(field);
        processNeighborFieldsForOutbreak(neighborFields, plague, infectedFields, fieldsWithOutbreak);
    }

    /**
     * Processes the neighboring fields of a field during an outbreak, infecting them if possible or handling recursive outbreaks.
     * <p>
     * For each neighboring field, this method checks if the field can be infected with the given plague. If infectable,
     * the field is infected with a plague cube. If the field cannot be infected and is not already in the list of
     * ongoing outbreaks, a recursive outbreak is started. If there are no plague cubes available, the processing stops.
     * </p>
     *
     * @param neighborFields    The list of neighboring fields to process.
     * @param plague            The plague spreading during the outbreak.
     * @param infectedFields    The list of fields that have already been infected.
     * @param fieldsWithOutbreak The list of fields where outbreaks are already occurring.
     */
    private void processNeighborFieldsForOutbreak(List<Field> neighborFields, Plague plague, List<Field> infectedFields, List<Field> fieldsWithOutbreak) {
        for (Field neighborField : neighborFields) {
            try {
                if (neighborField.isInfectable(plague)) {
                    PlagueCube plagueCube = game.getPlagueCubeOfPlague(plague);
                    neighborField.infectField(plagueCube, infectedFields);
                } else if (!fieldsWithOutbreak.contains(neighborField)) {
                    startOutbreakRecursive(neighborField, plague, infectedFields, fieldsWithOutbreak);
                }
            } catch (NoPlagueCubesFoundException e) {
                return;
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
        return getNeighborFields(field).stream()
                .filter(neighborField -> !excludedFields.contains(neighborField))
                .toList();
    }

    /**
     * Returns a {@link List} of all fields neighboring the given {@link Field}
     *
     * @param field {@link Field} to search neighbors for
     * @return {@link List} of fields neighboring the given {@link Field}
     * @see Field#getConnectedCities()
     */
    public List<Field> getNeighborFields(final Field field) {
        final List<Field> neighborFields = new ArrayList<>();

        final List<City> neighborCities = field.getConnectedCities();

        for (final Field f : this.fields) {
            if (neighborCities.contains(f.getCity())) {
                neighborFields.add(f);
            }
        }

        return neighborFields;
    }

    /**
     * <p>
     *     Checks whether the given {@link Plague} has been exterminated.
     *     Calls {@link Plague#exterminate()} if it has been exterminated.
     * </p>
     *
     * <p>
     *     A {@link Plague} is exterminated if
     *      no {@link Field} has a {@link PlagueCube} of it and
     *      the {@link Game} has an {@link AntidoteMarker} to it.
     * </p>
     *
     * @param plague {@link Plague} to check extermination status for
     * @return {@code true} if the given {@link Plague} is exterminated, {@code false} otherwise
     */
    public boolean isPlagueExterminated(final Plague plague) {
        if (!game.hasAntidoteMarkerForPlague(plague)) {
            return false;
        }

        for (final Field field : fields) {
            if (field.isCurable(plague)) {
                return false;
            }
        }

        plague.exterminate();
        return true;
    }

    /**
     * Returns the {@link Field} of the {@link GameMap} that has the given {@link City} in its {@link MapSlot}
     *
     * @param city the {@link City} to get the associated {@link Field} for
     * @return the {@link Field} associated with the given {@link City}
     * @throws FieldOfCityNotFoundException {@link Field} for given {@link City} has not been found
     */
    public Field getFieldOfCity(City city){
        for (Field field : fields) {
            if (field.hasCity(city)) {
                return field;
            }
        }
        throw new FieldOfCityNotFoundException(city.getName());
    }

    /**
     * Returns a {@link List} of all players on given {@link Field}.
     *
     * @param field {@link Field} for which the players are searched
     * @return {@link List} of all players on given {@link Field}
     * @see Game#getPlayersInTurnOrder()
     */
    public List<Player> getPlayersOnField(final Field field) {
        return this.game.getPlayersInTurnOrder().stream()
                .filter(player -> Objects.equals(player.getCurrentField(), field))
                .toList();
    }
}
