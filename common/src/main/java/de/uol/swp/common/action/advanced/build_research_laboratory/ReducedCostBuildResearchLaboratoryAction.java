package de.uol.swp.common.action.advanced.build_research_laboratory;

import de.uol.swp.common.action.RoleAction;
import de.uol.swp.common.card.CityCard;

import java.util.List;

/**
 * The {@code ReducedCostBuildResearchLaboratoryAction} class represents an action where a player builds a research laboratory
 * at a reduced cost. It extends {@link BuildResearchLaboratoryAction} and implements {@link RoleAction}, indicating that
 * this action is associated with a specific role in the game.
 */
public class ReducedCostBuildResearchLaboratoryAction extends BuildResearchLaboratoryAction implements RoleAction {

    /**
     * <p>
     *     Returns an empty {@link List}.
     * </p>
     *
     * @return empty {@link List}
     */
    @Override
    public List<CityCard> getDiscardedCards() {
        return List.of();
    }
}
