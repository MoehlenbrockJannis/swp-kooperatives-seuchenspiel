package de.uol.swp.common.action.advanced.build_research_laboratory;

import de.uol.swp.common.action.RoleAction;
import lombok.NoArgsConstructor;

/**
 * The {@code ReducedCostBuildResearchLaboratoryAction} class represents an action where a player builds a research laboratory
 * at a reduced cost. It extends {@link BuildResearchLaboratoryAction} and implements {@link RoleAction}, indicating that
 * this action is associated with a specific role in the game.
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
@NoArgsConstructor
public class ReducedCostBuildResearchLaboratoryAction extends BuildResearchLaboratoryAction implements RoleAction {
}
