package de.uol.swp.common.action.simple.shuttle_flight;

import de.uol.swp.common.action.simple.MoveAction;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.map.research_laboratory.ResearchLaboratory;
import de.uol.swp.common.player.Player;

import java.util.List;

/**
 * This class represent and realized the shuttle flight action.
 */
public class ShuttleFlightAction extends MoveAction {

    @Override
    public String toString() {
        return "Zubringerflug";
    }

    @Override
    public Player getMovedPlayer() {
        return getExecutingPlayer();
    }

    /**
     * <p>
     *     Returns a {@link List} of fields with a {@link ResearchLaboratory}.
     *     Does not include the field the moved {@link Player} is standing on.
     * </p>
     *
     * <p>
     *     If the field the moved {@link Player} is standing on does not have a {@link ResearchLaboratory},
     *     returns an empty {@link List}.
     * </p>
     *
     * {@inheritDoc}
     *
     * @return {@link List} of all fields with a {@link ResearchLaboratory} or empty {@link List}
     * @see Game#getFields()
     * @see Field#hasResearchLaboratory()
     * @see #getCurrentField()
     */
    @Override
    public List<Field> getAvailableFields() {
        final Field currentField = getCurrentField();
        if (currentField.hasResearchLaboratory()) {
            return getGame().getFields().stream()
                    .filter(field -> !field.equals(currentField) && field.hasResearchLaboratory())
                    .toList();
        } else {
            return List.of();
        }
    }
}
