package de.uol.swp.common.action.advanced.build_research_laboratory;

import de.uol.swp.common.action.DiscardCardsAction;
import de.uol.swp.common.action.advanced.AdvancedAction;
import de.uol.swp.common.card.CityCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.map.research_laboratory.ResearchLaboratory;
import de.uol.swp.common.player.Player;
import lombok.Getter;

import java.util.List;

/**
 * The {@code BuildResearchLaboratoryAction} class represents an action where a player builds a research laboratory
 * on a specific field. It extends {@link AdvancedAction} and implements {@link DiscardCardsAction}, meaning it requires
 * discarding a {@link CityCard} to perform the action.
 * <p>
 * The action may also involve moving an existing research laboratory, depending on the game context.
 * </p>
 *
 *  @author Jannis Moehlenbrock
 *  @since 2024-09-17
 */
@Getter
public class BuildResearchLaboratoryAction extends AdvancedAction implements DiscardCardsAction {

    /**
     * The field where the research laboratory is to be built or moved from.
     */
    private Field researchLaboratoryOriginField;

    /**
     * <p>
     *     Sets the {@link #researchLaboratoryOriginField} to move a {@link ResearchLaboratory} from if needed.
     * </p>
     *
     * <p>
     *     The given {@code researchLaboratoryOriginField} must have a {@link ResearchLaboratory} on it.
     * </p>
     *
     * @param researchLaboratoryOriginField {@link Field} a {@link ResearchLaboratory} is moved from if needed
     * @throws IllegalStateException if the given {@code researchLaboratoryOriginField} does not have a {@link ResearchLaboratory} on it
     */
    public void setResearchLaboratoryOriginField(final Field researchLaboratoryOriginField) {
        if (!researchLaboratoryOriginField.hasResearchLaboratory()) {
            throw new IllegalStateException("Given Field does not have a ResearchLaboratory.");
        }
        this.researchLaboratoryOriginField = researchLaboratoryOriginField;
    }

    /**
     * Determines if the action requires moving an existing research laboratory to a new location.
     *
     * @return {@code true} if moving the research laboratory is required, otherwise {@code false}
     */
    public boolean requiresMovingOfResearchLaboratory() {
        return !getGame().hasResearchLaboratory();
    }

    /**
     * <p>
     *     Returns {@code true} if
     *      the current {@link Field} does not have a {@link ResearchLaboratory} and
     *      the executing {@link Player} has the card of the current {@link Field} on hand.
     * </p>
     *
     * {@inheritDoc}
     *
     * @return {@code true} if
     *   the current {@link Field} does not have a {@link ResearchLaboratory} and
     *   the executing {@link Player} has the card of the current {@link Field} on hand,
     *  {@code false} otherwise
     * @see #getExecutingPlayer()
     * @see Player#getCurrentField()
     * @see #getDiscardedCards()
     */
    @Override
    public boolean isAvailable() {
        if (getExecutingPlayer().getCurrentField().hasResearchLaboratory()) {
            return false;
        }

        try {
            getDiscardedCards();
            return true;
        } catch (final IllegalStateException e) {
            return false;
        }
    }

    /**
     * <p>
     *     A {@link BuildResearchLaboratoryAction} is executable if it is available and
     *      either no {@link ResearchLaboratory} needs to be moved
     *      or the {@link #researchLaboratoryOriginField} has been set.
     * </p>
     *
     * @return {@code true} if it is available and the moving of a {@link ResearchLaboratory} has been dealt with, {@code false} otherwise
     * @see #isAvailable()
     * @see #requiresMovingOfResearchLaboratory()
     * @see #researchLaboratoryOriginField
     */
    @Override
    public boolean isExecutable() {
        return isAvailable() && (!requiresMovingOfResearchLaboratory() || researchLaboratoryOriginField != null);
    }

    /**
     * <p>
     *     If this {@link BuildResearchLaboratoryAction} is executable,
     *     adds a {@link ResearchLaboratory} to the current {@link Field} by
     *      either moving it from {@link #researchLaboratoryOriginField}
     *      or by getting it from the associated {@link Game}.
     * </p>
     *
     * @throws IllegalStateException if the {@link BuildResearchLaboratoryAction} is not executable
     * @see #isExecutable()
     * @see #requiresMovingOfResearchLaboratory()
     * @see Field#removeResearchLaboratory()
     * @see Game#getResearchLaboratory()
     * @see Player#getCurrentField()
     * @see Field#buildResearchLaboratory(ResearchLaboratory)
     */
    @Override
    public void execute() {
        if (!isExecutable()) {
            throw new IllegalStateException("This Action may not be executed.");
        }

        ResearchLaboratory researchLaboratory;
        if (requiresMovingOfResearchLaboratory()) {
            researchLaboratory = researchLaboratoryOriginField.removeResearchLaboratory();
        } else {
            researchLaboratory = getGame().getResearchLaboratory();
        }
        final Field currentField = getExecutingPlayer().getCurrentField();
        currentField.buildResearchLaboratory(researchLaboratory);
    }

    /**
     * <p>
     *     Returns a {@link List} of city cards with one element,
     *     the {@link CityCard} of the current {@link Field}.
     * </p>
     *
     * @return {@link List} with {@link CityCard} of current {@link Field}
     * @throws IllegalStateException if the moved {@link Player} does not have a fitting {@link CityCard} on hand
     */
    @Override
    public List<CityCard> getDiscardedCards() {
        final Player player = getExecutingPlayer();
        final Field currentField = player.getCurrentField();
        final List<CityCard> cityCardsWithCurrentFieldLimitedTo1 = player.getHandCards().stream()
                .filter(CityCard.class::isInstance)
                .map(CityCard.class::cast)
                .filter(card -> card.hasField(currentField))
                .limit(1)
                .toList();
        if (cityCardsWithCurrentFieldLimitedTo1.isEmpty()) {
            throw new IllegalStateException("Player does not have the CityCard of his current Field.");
        }
        return cityCardsWithCurrentFieldLimitedTo1;
    }
}
