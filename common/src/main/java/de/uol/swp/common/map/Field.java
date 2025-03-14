package de.uol.swp.common.map;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.exception.NoPlagueCubesOfPlagueOnFieldException;
import de.uol.swp.common.map.exception.ResearchLaboratoryAlreadyBuiltOnFieldException;
import de.uol.swp.common.map.research_laboratory.ResearchLaboratory;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.plague.PlagueCube;
import de.uol.swp.common.player.Player;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Instantiation of a city defined by {@link MapSlot}
 *
 * <p>
 *     Objects of this class are used in running games to store information.
 *     There may be a {@link ResearchLaboratory} or
 * </p>
 *
 * @see GameMap
 * @see MapSlot
 * @see Plague
 * @see PlagueCube
 * @see ResearchLaboratory
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Field implements Serializable {
    @EqualsAndHashCode.Include
    private GameMap map;
    @EqualsAndHashCode.Include
    @Getter
    private MapSlot mapSlot;
    private ResearchLaboratory researchLaboratory;
    @Getter
    @Setter
    private Map<Plague, List<PlagueCube>> plagueCubes;

    /**
     * Constructor
     *
     * @param map Associated {@link GameMap}
     * @param mapSlot Associated {@link MapSlot}
     */
    public Field(final GameMap map, final MapSlot mapSlot) {
        this.map = map;
        this.mapSlot = mapSlot;
        this.plagueCubes = new HashMap<>();

        initPlagueCubes();
    }

    @Override
    public String toString() {
        return mapSlot.toString();
    }

    /**
     * Initializes plagueCubes Map
     */
    private void initPlagueCubes() {
        for (Plague plague : map.getType().getUniquePlagues()) {
            List<PlagueCube> plagueList = new ArrayList<>();
            plagueCubes.put(plague, plagueList);
        }
    }

    /**
     * @return X coordinate of the {@link #mapSlot}
     * @see MapSlot#getXCoordinate()
     */
    public int getXCoordinate() {
        return mapSlot.getXCoordinate();
    }

    /**
     * @return Y coordinate of the {@link #mapSlot}
     * @see MapSlot#getYCoordinate()
     */
    public int getYCoordinate() {
        return mapSlot.getYCoordinate();
    }

    /**
     * @return City of the {@link #mapSlot}
     * @see MapSlot#getCity()
     */
    public City getCity() {
        return mapSlot.getCity();
    }

    /**
     * @return Cities connected to this field as specified by the {@link #mapSlot}
     * @see MapSlot#getConnectedCities()
     */
    public List<City> getConnectedCities() {
        return mapSlot.getConnectedCities();
    }

    /**
     * @return Plague associated with the {@link #mapSlot}
     * @see MapSlot#getPlague()
     */
    public Plague getPlague() {
        return mapSlot.getPlague();
    }

    /**
     * Infects this field with its default plague type.
     * Creates a new list to track infected fields.
     */
    public void infectField() {
        final PlagueCube plagueCube = map.getPlagueCubeOfPlague(getPlague());
        final List<Field> infectedFields = new ArrayList<>();
        infectField(plagueCube, infectedFields);
    }


    /**
     * Attempts to infect this field with the given plague cube.
     * If the field cannot be infected (already has maximum cubes), triggers an outbreak instead.
     *
     * @param plagueCube The plague cube to add to this field
     * @param infectedFields List tracking all fields that get infected in this infection chain
     */
    public void infectField(final PlagueCube plagueCube, List<Field> infectedFields) {
        final Plague plague = plagueCube.getPlague();
        if (isInfectable(plague)) {
            final List<PlagueCube> plagueCubeList = plagueCubes.get(plague);
            plagueCubeList.add(plagueCube);
            infectedFields.add(this);
        } else {
            map.addPlagueCube(plagueCube);
            map.startOutbreak(this, plague, infectedFields);
        }
    }

    /**
     * Checks whether this field can be infected with the given plague
     *
     * <p>
     *     The number of {@link PlagueCube} of the given {@link Plague} in {@link #plagueCubes} must be smaller
     *     than the maximally allowed number as specified by {@link GameMap#getMaxNumberOfPlagueCubesPerField()}.
     * </p>
     *
     * @param plague Plague to check infectability for
     * @return True if this field is infectable, false otherwise
     * @see GameMap#getMaxNumberOfPlagueCubesPerField()
     */
    public boolean isInfectable(final Plague plague) {
        if (plagueCubes.containsKey(plague)) {
            final List<PlagueCube> plagueCubeList = plagueCubes.get(plague);
            final int maxNumberOfPlagueCubesPerField = map.getMaxNumberOfPlagueCubesPerField();
            return plagueCubeList.size() < maxNumberOfPlagueCubesPerField;
        }

        plagueCubes.put(plague, new ArrayList<>());
        return true;
    }

    /**
     * Cures one {@link PlagueCube} of the given plague from {@link #plagueCubes} by removing and returning it.
     *
     * <p>
     *     Also checks by calling the {@link #map} whether the {@link Plague} has been completely exterminated.
     * </p>
     *
     * @param plague Plague to cure
     * @return {@link PlagueCube} of plague
     * @throws NoPlagueCubesOfPlagueOnFieldException if there is no {@link PlagueCube} of plague on this field
     * @see NoPlagueCubesOfPlagueOnFieldException
     * @see Plague
     * @see PlagueCube
     */
    public PlagueCube cure(final Plague plague) throws NoPlagueCubesOfPlagueOnFieldException {
        final List<PlagueCube> plagueCubeList = plagueCubes.getOrDefault(plague, List.of());

        if (!isCurable(plague)) {
            throw new NoPlagueCubesOfPlagueOnFieldException(plague.toString(), getCity().getName());
        }

        final PlagueCube plagueCube = plagueCubeList.get(0);
        plagueCubeList.remove(0);

        map.isPlagueExterminated(plague);

        return plagueCube;
    }

    /**
     * <p>
     *     Returns whether the specified {@link Plague} can be cured or not.
     * </p>
     *
     * @param plague The {@link Plague} to check curability for
     * @return {@code true} if there is at least one {@link PlagueCube}
     */
    public boolean isCurable(final Plague plague) {
        final List<PlagueCube> plagueCubeList = plagueCubes.getOrDefault(plague, List.of());
        return !plagueCubeList.isEmpty();
    }

    /**
     * Builds a {@link ResearchLaboratory} on this field if not built already
     *
     * @param researchLaboratory The {@link ResearchLaboratory} to build
     * @throws ResearchLaboratoryAlreadyBuiltOnFieldException if there is already a {@link ResearchLaboratory} on this field
     */
    public void buildResearchLaboratory(final ResearchLaboratory researchLaboratory) throws ResearchLaboratoryAlreadyBuiltOnFieldException {
        if (hasResearchLaboratory()) {
            throw new ResearchLaboratoryAlreadyBuiltOnFieldException(getCity().getName());
        }

        this.researchLaboratory = researchLaboratory;
    }

    /**
     * <p>
     *     Removes the {@link #researchLaboratory} from this {@link Field}.
     * </p>
     *
     * <p>
     *     Sets {@link #researchLaboratory} to {@code null} and returns the previously stored {@link ResearchLaboratory}.
     * </p>
     *
     * @return The {@link #researchLaboratory} of this {@link Field}
     * @throws IllegalStateException if {@link #researchLaboratory} is null
     * @see #hasResearchLaboratory()
     */
    public ResearchLaboratory removeResearchLaboratory() {
        if (!hasResearchLaboratory()) {
            throw new IllegalStateException("Field does not have a ResearchLaboratory on it.");
        }
        final ResearchLaboratory rl = researchLaboratory;
        this.researchLaboratory = null;
        return rl;
    }

    /**
     * Returns whether this field has a research laboratory or not
     *
     * @return {@code true} if {@link #researchLaboratory} is not {@code null}, {@code false} otherwise
     */
    public boolean hasResearchLaboratory() {
        return this.researchLaboratory != null;
    }

    /**
     * <p>
     *     Returns {@code true} if given {@link Plague} is equal to {@link Plague} on {@link #mapSlot}, {@code false} otherwise.
     * </p>
     *
     * <p>
     *     Delegates to {@link MapSlot#hasPlague(Plague)}.
     * </p>
     *
     * @param plague {@link Plague} to check equality with {@link Plague} on {@link #mapSlot} for
     * @return {@code true} if given {@link Plague} is equal to {@link Plague} on {@link #mapSlot}, {@code false} otherwise
     * @see MapSlot#hasPlague(Plague)
     */
    public boolean hasPlague(final Plague plague) {
        return mapSlot.hasPlague(plague);
    }

    /**
     * Returns whether this field has represents the given {@link City} or not
     *
     * @param city the {@link City} to check equality with {@link City} on {@link #mapSlot} for
     * @return {@code true} if given {@link City} is equal to {@link City} on {@link #mapSlot}, {@code false} otherwise
     */
    public boolean hasCity(City city) {
        return this.getCity().equals(city);
    }

    /**
     * Returns a {@link List} of all fields neighboring this {@link Field}
     *
     * @return {@link List} of fields neighboring this {@link Field}
     * @see GameMap#getNeighborFields(Field)
     */
    public List<Field> getNeighborFields() {
        return map.getNeighborFields(this);
    }

    /**
     * Returns a {@link List} of all players on this field.
     *
     * @return {@link List} of all players on this field
     * @see GameMap#getPlayersOnField(Field)
     * @see Player#getCurrentField()
     */
    public List<Player> getPlayersOnField() {
        return this.map.getPlayersOnField(this);
    }

    /**
     * Returns {@link List} of all {@link PlagueCube} of the given Plague found on the field
     *
     * @param plague the {@link Plague} used to return the associated {@link PlagueCube} List
     * @return {@link List} of all {@link PlagueCube} of the given Plague
     */
    public List<PlagueCube> getPlagueCubesOfPlague(Plague plague){
        return plagueCubes.get(plague);
    }

    /**
     * Counts the amount of foreign {@link Plague} types found on the field
     *
     * @return number of foreign plagueCube types found on the field
     */
    public int getNumberOfForeignPlagueCubeTypes(){
        int numberOfForeignPlagueTypes = 0;
        Plague associatedPlague = this.getPlague();

        for (Map.Entry<Plague, List<PlagueCube>> entry : plagueCubes.entrySet()) {
            List<PlagueCube> plagueCubeList = entry.getValue();

            if (!entry.getKey().equals(associatedPlague) && plagueCubeList != null && !plagueCubeList.isEmpty()) {
                numberOfForeignPlagueTypes++;
            }
        }

        return numberOfForeignPlagueTypes;
    }

    /**
     * Returns the number of {@link PlagueCube} for each {@link Plague} found on the field
     *
     * @return {@link Map} of {@link Plague} and {@link Integer} for the amount of {@link PlagueCube} for each {@link Plague}
     */
    public Map<Plague, Integer> getPlagueCubeAmounts() {
        Map<Plague, Integer> plagueCubeAmounts = new HashMap<>();

        for (Map.Entry<Plague, List<PlagueCube>> entry : plagueCubes.entrySet()) {
            plagueCubeAmounts.put(entry.getKey(), entry.getValue().size());
        }
        return plagueCubeAmounts;
    }
    /**
     * Removes plague cubes of the specified type and returns them to the game's supply.
     * If player is a doctor with an antidote marker, cures the plague globally across all fields.
     * Otherwise, removes plague cubes only from the current field.
     *
     * @param plague The type of plague to cure
     * @param currentField The field where the cure action was initiated
     * @param game The game instance
     */
    public void removeAllPlagueCubes(Plague plague, Field currentField, Game game) {
        removeAllPlagueCubes(plague, currentField, game, false);
    }

    public void removeAllPlagueCubes(Plague plague, Field currentField, Game game, boolean isGlobalCure) {
        if (isGlobalCure) {
            cureAllFieldsOfPlague(plague, game);
        } else {
            cureFieldOfPlague(currentField, plague, game);
        }
    }

    /**
     * Cures all instances of the specified plague across all fields on the map.
     * Used for the doctor's special ability with antidote marker.
     *
     * @param plague The type of plague to cure globally
     * @param game The game instance containing the map and plague cube supply
     */
    private void cureAllFieldsOfPlague(Plague plague, Game game) {
        game.getMap().getFields().forEach(field ->
                cureFieldOfPlague(field, plague, game)
        );
    }

    /**
     * Cures all plague cubes of the specified type from a single field.
     * Returns all cured plague cubes to the game's supply.
     *
     * @param field The field to cure plague cubes from
     * @param plague The type of plague to cure
     * @param game The game instance to return plague cubes to
     */
    private void cureFieldOfPlague(Field field, Plague plague, Game game) {
        while (field.isCurable(plague)) {
            PlagueCube curedCube = field.cure(plague);
            game.addPlagueCube(curedCube);
        }
    }
}
