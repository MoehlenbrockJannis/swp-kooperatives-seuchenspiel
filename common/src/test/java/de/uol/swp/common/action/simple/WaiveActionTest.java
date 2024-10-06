package de.uol.swp.common.action.simple;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class WaiveActionTest {
    private WaiveAction action;

    @BeforeEach
    void setUp() {
        action = new WaiveAction();
    }

    @Test
    @DisplayName("Should always be available")
    void isAvailable() {
        assertThat(action.isAvailable())
                .isTrue();
    }

    @Test
    @DisplayName("Should always be executable")
    void isExecutable() {
        assertThat(action.isExecutable())
                .isTrue();
    }

    @Test
    @DisplayName("Should do nothing")
    void execute() {
        assertThatCode(() -> action.execute())
                .doesNotThrowAnyException();
    }
}