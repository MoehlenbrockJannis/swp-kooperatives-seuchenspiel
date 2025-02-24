package de.uol.swp.common.card.event_card;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.map.research_laboratory.ResearchLaboratory;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Represents a Government Subsidies Event Card in the game.
 * This card allows the player to build a research station in any city without discarding a card.
 */
@Setter
@Getter
public class GovernmentSubsidiesEventCard extends EventCard {
    private static final String DESCRIPTION_STRING = "Errichten Sie in einer beliebigen Stadt ein Forschungslabor. Sie müssen dafür keine Karte ablegen.";
    private static final String TITLE_STRING = "Regierungssubventionen";

    private Field field;
    private Field researchLaboratoryOriginField;
    private ResearchLaboratory researchLaboratory;

    /**
     * Constructs a new GovernmentSubsidiesEventCard with predefined description and title.
     */
    public GovernmentSubsidiesEventCard() {
        super(DESCRIPTION_STRING, TITLE_STRING);
    }

    /**
     * Trigger the effect of the GovernmentSubsidiesEventCard.
     * This effect allows the player to build a research station in any city without discarding a card.
     */
    @Override
    public void trigger() {
        if (game.requiresResearchLaboratoryMove()) {
            researchLaboratory = removeOriginResearchLaboratory();
        } else {
            researchLaboratory = game.getResearchLaboratory();
        }

        buildResearchLaboratory();
    }

    /**
     * Builds a research laboratory in the target field.
     */
    private void buildResearchLaboratory() {
        List<ResearchLaboratory> researchLaboratories = game.getResearchLaboratories();

        field.buildResearchLaboratory(researchLaboratory);
        researchLaboratories.add(researchLaboratory);
    }

    /**
     * Removes the research laboratory from the origin field.
     *
     * @return The removed research laboratory.
     */
    private ResearchLaboratory removeOriginResearchLaboratory() {
        ResearchLaboratory originLaboratory = researchLaboratoryOriginField.removeResearchLaboratory();
        removeResearchLaboratoryFromList(originLaboratory);

        return originLaboratory;
    }

    /**
     * Removes the research laboratory from the list of research laboratories.
     *
     * @param researchLaboratory The research laboratory to be removed.
     */
    private void removeResearchLaboratoryFromList(ResearchLaboratory researchLaboratory) {
        List<ResearchLaboratory> researchLaboratories = game.getResearchLaboratories();
        researchLaboratories.remove(researchLaboratory);
    }

    /**
     * Gets the effect message of the GovernmentSubsidiesEventCard.
     */
    @Override
    public String getEffectMessage() {
        return "Es wurde ein neues Forschungslabor in " + field + " mit Hilfe von " + TITLE_STRING + " errichtet.";
    }

    /**
     * Initializes the GovernmentSubsidiesEventCard with the game.
     *
     * @param game The game to be initialized with.
     */
    @Override
    public void initWithGame(final Game game) {
        super.initWithGame(game);
        this.field = this.game.findField(this.field).orElseThrow();
        this.researchLaboratoryOriginField = this.researchLaboratoryOriginField == null ? null : this.game.findField(this.researchLaboratoryOriginField).orElseThrow();
    }
}
