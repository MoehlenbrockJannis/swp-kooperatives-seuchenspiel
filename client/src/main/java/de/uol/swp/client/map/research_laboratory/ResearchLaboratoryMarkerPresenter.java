package de.uol.swp.client.map.research_laboratory;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.action.ActionService;
import de.uol.swp.common.action.Action;
import de.uol.swp.common.action.advanced.build_research_laboratory.BuildResearchLaboratoryAction;
import de.uol.swp.common.card.event_card.GovernmentSubsidiesEventCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Presenter class responsible for managing the behavior of the research laboratory marker
 * in the game client. It handles mouse events and actions for moving a research laboratory.
 */

@AllArgsConstructor
public class ResearchLaboratoryMarkerPresenter extends AbstractPresenter {

    private final ResearchLaboratoryMarker researchLaboratoryMarker;
    private final Game game;
    private final ActionService actionService;
    @Getter
    @Setter
    private final Field field;

    /**
     * Initializes mouse events for the research laboratory marker.
     * Includes hover and click event handling.
     */
    public void initializeMouseEvents() {
        initializeHoverEvents();
        initializeClickEvent();
    }

    /**
     * Sets up hover effects for the research laboratory marker.
     * Adjusts the marker's opacity when the mouse hovers over or leaves it.
     */
    private void initializeHoverEvents() {
        researchLaboratoryMarker.setOnMouseEntered(event -> researchLaboratoryMarker.setOpacity(0.5));
        researchLaboratoryMarker.setOnMouseExited(event -> researchLaboratoryMarker.setOpacity(1.0));
    }

    /**
     * Sets up the click event for the research laboratory marker.
     * Clicking the marker triggers the action to move it.
     */
    private void initializeClickEvent() {
        researchLaboratoryMarker.setOnMouseClicked(event -> moveResearchLaboratoryMarker());
    }

    /**
     * Handles the logic for moving the research laboratory marker.
     * Iterates through possible actions in the current turn and sends the action
     * to the server if moving the research laboratory is required.
     */
    private void moveResearchLaboratoryMarker() {
        List<Action> possibleActions = game.getCurrentTurn().getPossibleActions();
        for (Action action : possibleActions) {
            if (action instanceof BuildResearchLaboratoryAction buildAction) {
                processBuildResearchLaboratoryAction(buildAction);
                return;
            }
        }
    }

    /**
     * Processes the action to build a research laboratory.
     * If the action requires moving the research laboratory, the origin field is set.
     *
     * @param action The action to be processed.
     */
    private void processBuildResearchLaboratoryAction(BuildResearchLaboratoryAction action) {
        if (game.requiresResearchLaboratoryMove() && game.isResearchLaboratoryButtonClicked()) {
            action.setResearchLaboratoryOriginField(this.getField());
            actionService.sendAction(game, action);
            game.setResearchLaboratoryButtonClicked(false);
        }
    }

    /**
     * Sets the click listener for the government subsidies event card.
     * When the research laboratory marker is clicked, the origin field is set.
     *
     * @param governmentSubsidiesEventCard the government subsidies event card
     * @param approve the action to approve the event card
     */
    public void setClickListenerForGovernmentSubsidies(GovernmentSubsidiesEventCard governmentSubsidiesEventCard, Runnable approve) {
        researchLaboratoryMarker.setOnMouseClicked(event -> {
            governmentSubsidiesEventCard.setResearchLaboratoryOriginField(field);
            approve.run();
        });
    }
}
