package de.uol.swp.common.action.advanced.discover_antidote;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReducedCostDiscoverAntidoteActionTest {
    private ReducedCostDiscoverAntidoteAction action;

    @BeforeEach
    void setUp() {
        action = new ReducedCostDiscoverAntidoteAction();
    }

    @Test
    @DisplayName("Should return 4")
    void getRequiredAmountOfDiscardedCards() {
        assertThat(action.getRequiredAmountOfDiscardedCards())
                .isEqualTo(4);
    }
}