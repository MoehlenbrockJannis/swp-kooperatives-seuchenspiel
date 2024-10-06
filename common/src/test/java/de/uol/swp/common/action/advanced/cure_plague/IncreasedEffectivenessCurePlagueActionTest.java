package de.uol.swp.common.action.advanced.cure_plague;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IncreasedEffectivenessCurePlagueActionTest {
    private IncreasedEffectivenessCurePlagueAction action;

    @BeforeEach
    void setUp() {
        action = new IncreasedEffectivenessCurePlagueAction();
    }

    @Test
    @DisplayName("Should always return true")
    void isRemoveAllPlagueCubesAvailable() {
        assertThat(action.isRemoveAllPlagueCubesAvailable())
                .isTrue();
    }
}