package de.uol.swp.common.action.advanced.transfer_card;

import de.uol.swp.common.action.RoleAction;
import de.uol.swp.common.role.RoleAbility;
import de.uol.swp.common.role.RoleCard;
import de.uol.swp.common.util.Color;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

abstract class ReceiveCardActionTest extends ShareKnowledgeActionTest {
    @Getter
    protected ReceiveCardAction action;
    protected Class<? extends SendCardAction> sendCardActionClass;

    @BeforeEach
    @Override
    void setUp() {
        action = new ReceiveCardAction();

        super.setUp();
    }

    @Test
    @DisplayName("Should return the target player")
    void getSender() {
        assertThat(action.getSender())
                .usingRecursiveComparison()
                .isEqualTo(targetPlayer);
    }

    @Test
    @DisplayName("Should return the executing player")
    void getReceiver() {
        assertThat(action.getReceiver())
                .usingRecursiveComparison()
                .isEqualTo(executingPlayer);
    }

    @Test
    @DisplayName("Should return the replaced SendCardAction class object if the sender has a custom action")
    void getSendCardActionOfOpponent() {
        if (sendCardActionClass != null) {
            assertThat(action.getSendCardActionOfOpponent())
                    .isEqualTo(sendCardActionClass);
        }
    }
}

class ReceiveCardActionTestNoLimitsTest extends ReceiveCardActionTest {
    @BeforeEach
    @Override
    void setUp() {
        super.setUp();

        sendCardActionClass = NoLimitsSendCardAction.class;
        final Class<SendCardAction> sendCardAction = SendCardAction.class;

        @SuppressWarnings("unchecked")
        final RoleAbility ability = new RoleAbility(
                Map.of(
                        sendCardAction, (Class<? extends RoleAction>) sendCardActionClass
                ),
                new ArrayList<>(),
                new ArrayList<>()
        );
        final RoleCard role = new RoleCard("", new Color(), ability);
        targetPlayer.setRole(role);
    }

    @Test
    @Override
    @DisplayName("Should return true if sender has transferred card on hand")
    void isAvailable_true() {
        targetPlayer.addHandCard(transferredCard);

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
        targetPlayer.addHandCard(transferredCard);

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

        targetPlayer.addHandCard(transferredCard);

        assertThat(action.isExecutable())
                .isFalse();
    }
}

class ReceiveCardActionTestWithLimitsTest extends ReceiveCardActionTest {
    @BeforeEach
    @Override
    void setUp() {
        super.setUp();

        sendCardActionClass = SendCardAction.class;

        final RoleAbility ability = new RoleAbility(
                Map.of(),
                new ArrayList<>(),
                new ArrayList<>()
        );
        final RoleCard role = new RoleCard("", new Color(), ability);
        targetPlayer.setRole(role);
    }
}