package de.uol.swp.common.action.advanced.build_research_laboratory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReducedCostBuildResearchLaboratoryActionTest {
    private ReducedCostBuildResearchLaboratoryAction action;

    @BeforeEach
    void setUp() {
        action = new ReducedCostBuildResearchLaboratoryAction();
    }

    @Test
    @DisplayName("Should always be empty")
    void getDiscardedCard() {
        assertThat(action.getDiscardedCards())
                .isEmpty();
    }
}