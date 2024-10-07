package de.uol.swp.common.map;

import de.uol.swp.common.map.exception.NoPlagueCubesOfPlagueOnFieldException;
import de.uol.swp.common.map.exception.ResearchLaboratoryAlreadyBuiltOnFieldException;
import de.uol.swp.common.map.research_laboratory.ResearchLaboratory;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.plague.PlagueCube;
import de.uol.swp.common.player.Player;
import lombok.EqualsAndHashCode;
import lombok.Getter;

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
 * @author Tom Weelborg
 * @since 2024-09-02
 */
@EqualsAndHashCode
public class Field implements Serializable {
    private GameMap map;
    @Getter
    private MapSlot mapSlot;
    private ResearchLaboratory researchLaboratory;
    private Map<Plague, List<PlagueCube>> plagueCubes;
    @Getter
    private List<Player> playersOnField;

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
        this.playersOnField = new ArrayList<>();
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
     * Infects this field with the default plague as specified by the {@link #mapSlot}
     *
     * <p>
     *     To get a {@link PlagueCube} of the default {@link Plague}, a method of the {@link #map} is called.
     * </p>
     *
     * @see #getPlague()
     * @see #infect(PlagueCube)
     * @see GameMap#getPlagueCubeOfPlague(Plague) 
     */
    public void infect() {
        final PlagueCube plagueCube = map.getPlagueCubeOfPlague(getPlague());
        infect(plagueCube);
    }

    /**
     * Infects this field with the given plagueCube by adding it to {@link #plagueCubes} if it is infectable.
     * Otherwise, it will call the {@link #map} to start an outbreak.
     *
     * @see #getPlague() 
     * @see #isInfectable(Plague)
     * @see GameMap#startOutbreak(Field, Plague)
     */
    public void infect(final PlagueCube plagueCube) {
        final Plague plague = plagueCube.getPlague();
        if (isInfectable(plague)) {
            final List<PlagueCube> plagueCubeList = plagueCubes.get(plague);
            plagueCubeList.add(plagueCube);
        } else {
            map.startOutbreak(this, plague);
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
     * Returns a {@link List} of all fields neighboring this {@link Field}
     *
     * @return {@link List} of fields neighboring this {@link Field}
     * @see GameMap#getNeighborFields(Field)
     */
    public List<Field> getNeighborFields() {
        return map.getNeighborFields(this);
    }
}
