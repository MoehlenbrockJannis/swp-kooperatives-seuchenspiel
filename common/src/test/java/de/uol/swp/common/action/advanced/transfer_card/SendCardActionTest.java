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

class SendCardActionTest extends ShareKnowledgeActionTest {
    private static class ReceiveCardActionDummy extends ReceiveCardAction implements RoleAction {}

    @Getter
    private SendCardAction action;

    @BeforeEach
    @Override
    void setUp() {
        action = getAction();

        if (action == null) {
            action = new SendCardAction();
        }

        super.setUp();
    }

    @Test
    @DisplayName("Should return the executing player")
    void getSender() {
        assertThat(action.getSender())
                .usingRecursiveComparison()
                .isEqualTo(executingPlayer);
    }

    @Test
    @DisplayName("Should return the target player")
    void getReceiver() {
        assertThat(action.getReceiver())
                .usingRecursiveComparison()
                .isEqualTo(targetPlayer);
    }

    @Test
    @DisplayName("Should return the replaced ReceiveCardAction class object if the receiver has a custom action")
    void getReceiveCardActionOfOpponent_replaced() {
        final Class<ReceiveCardActionDummy> replacement = ReceiveCardActionDummy.class;
        final Class<ReceiveCardAction> receiveCardAction = ReceiveCardAction.class;
        final RoleAbility ability = new RoleAbility(
                Map.of(
                        receiveCardAction, replacement
                ),
                new ArrayList<>(),
                new ArrayList<>()
        );
        final RoleCard role = new RoleCard("", new Color(), ability);
        targetPlayer.setRole(role);

        assertThat(action.getReceiveCardActionOfOpponent())
                .isEqualTo(replacement);
    }

    @Test
    @DisplayName("Should return the default ReceiveCardAction class object if the receiver does not have a custom action")
    void getReceiveCardActionOfOpponent_notReplaced() {
        final Class<ReceiveCardAction> receiveCardAction = ReceiveCardAction.class;
        final RoleAbility ability = new RoleAbility(
                Map.of(),
                new ArrayList<>(),
                new ArrayList<>()
        );
        final RoleCard role = new RoleCard("", new Color(), ability);
        targetPlayer.setRole(role);

        assertThat(action.getReceiveCardActionOfOpponent())
                .isEqualTo(receiveCardAction);
    }
}