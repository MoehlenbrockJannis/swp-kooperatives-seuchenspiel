package de.uol.swp.common.action.advanced.transfer_card;

import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NoLimitsSendCardActionTest extends SendCardActionTest {
    @Getter
    private NoLimitsSendCardAction action;

    @BeforeEach
    @Override
    void setUp() {
        action = new NoLimitsSendCardAction();

        super.setUp();
    }

    @Test
    @Override
    @DisplayName("Should return true if sender has transferred card on hand")
    void isAvailable_true() {
        executingPlayer.addHandCard(transferredCard);

        assertThat(action.isAvailable())
                .isTrue();
    }

    @Test
    @Override
    @DisplayName("Should return false if sender does not have transferred card on hand")
    void isAvailable_false() {
        assertThat(action.isAvailable())
                .isFalse();
    }

    @Test
    @Override
    @DisplayName("Should return true if sender has transferred card on hand and action is approved")
    void isExecutable_true() {
        executingPlayer.addHandCard(transferredCard);

        action.approve();

        assertThat(action.isExecutable())
                .isTrue();
    }

    @Test
    @Override
    @DisplayName("Should return false if action is unavailable or action is not approved")
    void isExecutable_false() {
        isExecutable_falseUnavailable();

        isExecutable_falseNotApproved();
    }

    @Override
    void isExecutable_falseUnavailable() {
        setUp();

        assertThat(action.isExecutable())
                .isFalse();
    }

    @Override
    void isExecutable_falseNotApproved() {
        setUp();

        executingPlayer.addHandCard(transferredCard);

        assertThat(action.isExecutable())
                .isFalse();
    }

    @Test
    @DisplayName("Should return a list of sendable cards")
    @Override
    protected void getSendableCards() {
        action.getSender().addHandCard(transferredCard);

        assertThat(action.getSendableCards())
                .contains(transferredCard);
    }
}