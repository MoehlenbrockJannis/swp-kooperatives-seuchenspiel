package de.uol.swp.common.action.advanced.build_research_laboratory;

import de.uol.swp.common.action.advanced.AdvancedAction;
import de.uol.swp.common.action.advanced.DiscardCardAction;
import de.uol.swp.common.card.CityCard;
import de.uol.swp.common.map.Field;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * The {@code BuildResearchLaboratoryAction} class represents an action where a player builds a research laboratory
 * on a specific field. It extends {@link AdvancedAction} and implements {@link DiscardCardAction}, meaning it requires
 * discarding a {@link CityCard} to perform the action.
 * <p>
 * The action may also involve moving an existing research laboratory, depending on the game context.
 * </p>
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BuildResearchLaboratoryAction extends AdvancedAction implements DiscardCardAction {

    /**
     * The field where the research laboratory is to be built or moved from.
     */
    private Field researchLaboratoryOriginField;

    /**
     * Determines if the action requires moving an existing research laboratory to a new location.
     *
     * @return {@code true} if moving the research laboratory is required, otherwise {@code false}
     */
    public boolean requiresMovingOfResearchLaboratory() {
        return false;
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public void execute() {

    }
    @Override
    public CityCard getDiscardedCard() {
        return null;
    }
}
