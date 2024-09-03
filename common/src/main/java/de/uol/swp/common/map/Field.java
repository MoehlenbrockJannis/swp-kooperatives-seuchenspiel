package de.uol.swp.common.map;

import de.uol.swp.common.map.exception.NoPlagueCubesOfPlagueOnFieldException;
import de.uol.swp.common.map.exception.ResearchLaboratoryAlreadyBuiltOnFieldException;
import de.uol.swp.common.map.research_laboratory.ResearchLaboratory;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.plague.PlagueCube;
import lombok.EqualsAndHashCode;

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
public class Field {
    private GameMap map;
    private MapSlot mapSlot;
    private ResearchLaboratory researchLaboratory;
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
     * Cures one {@link PlagueCube} of the given plague from {@link #plagueCubes} by removing and returning it
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

        if (plagueCubeList.isEmpty()) {
            throw new NoPlagueCubesOfPlagueOnFieldException(plague.toString(), getCity().getName());
        }

        final PlagueCube plagueCube = plagueCubeList.get(0);
        plagueCubeList.remove(0);
        return plagueCube;
    }

    /**
     * Builds a {@link ResearchLaboratory} on this field if not built already
     *
     * @param researchLaboratory The {@link ResearchLaboratory} to build
     * @throws ResearchLaboratoryAlreadyBuiltOnFieldException if there is already a {@link ResearchLaboratory} on this field
     */
    public void buildResearchLaboratory(final ResearchLaboratory researchLaboratory) throws ResearchLaboratoryAlreadyBuiltOnFieldException {
        if (this.researchLaboratory != null) {
            throw new ResearchLaboratoryAlreadyBuiltOnFieldException(getCity().getName());
        }

        this.researchLaboratory = researchLaboratory;
    }
}
