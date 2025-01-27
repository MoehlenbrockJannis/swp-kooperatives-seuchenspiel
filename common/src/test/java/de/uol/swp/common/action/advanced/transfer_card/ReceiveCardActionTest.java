package de.uol.swp.common.action.advanced.transfer_card;

import de.uol.swp.common.role.RoleAbility;
import de.uol.swp.common.role.RoleCard;
import de.uol.swp.common.util.Color;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

abstract class ReceiveCardActionTest extends ShareKnowledgeActionTest {
    @Getter
    protected ReceiveCardAction action;

    @BeforeEach
    @Override
    void setUp() {
        action = new ReceiveCardAction();

        super.setUp();
    }

    @Test
    @DisplayName("Should return a valid approval request message")
    @Override
    protected void getApprovalRequestMessage() {
        action.getSender().setRole(null);

        assertThat(action.getApprovalRequestMessage())
                .isEqualTo(player2Name + " möchte " + player1Name + " die Karte " + fieldName + " vermachen.");
    }

    @Test
    @DisplayName("Should return a valid approved message")
    @Override
    protected void getApprovedMessage() {
        action.getSender().setRole(null);

        assertThat(action.getApprovedMessage())
                .isEqualTo(player2Name + " hat angenommen. " + player2Name + " hat " + player1Name + " die Karte " + fieldName + " vermacht.");
    }

    @Test
    @DisplayName("Should return a valid rejected message")
    @Override
    protected void getRejectedMessage() {
        action.getSender().setRole(null);

        assertThat(action.getRejectedMessage())
                .isEqualTo(player2Name + " hat abgelehnt. " + player2Name + " hat " + player1Name + " die Karte " + fieldName + " nicht vermacht.");
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

    protected abstract void getSendableCardsOfPlayer();
}

class ReceiveCardActionTestNoLimitsTest extends ReceiveCardActionTest {
    @BeforeEach
    @Override
    void setUp() {
        super.setUp();

        @SuppressWarnings("unchecked")
        final RoleAbility ability = new RoleAbility(
                Map.of(
                        SendCardAction.class, NoLimitsSendCardAction.class
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
        isAvailable_falseNoSender();

        isAvailable_falseNoHandCard();
    }

    private void isAvailable_falseNoSender() {
        setUp();

        action.setTargetPlayer(null);

        assertThat(action.isAvailable())
                .isFalse();
    }

    private void isAvailable_falseNoHandCard() {
        setUp();

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

    @Test
    @DisplayName("Should return a map of players with the cards they can send")
    @Override
    protected void getTargetPlayersWithAvailableCardsAssociation() {
        targetPlayer.addHandCard(transferredCard);

        assertThat(action.getTargetPlayersWithAvailableCardsAssociation())
                .isEqualTo(Map.of(targetPlayer, List.of(transferredCard)));
    }

    @Test
    @DisplayName("Should return a list containing all city cards on hand of target player")
    @Override
    protected void getSendableCardsOfPlayer() {
        targetPlayer.addHandCard(transferredCard);

        assertThat(action.getSendableCardsOfPlayer(targetPlayer))
                .containsExactly(transferredCard);
    }
}

class ReceiveCardActionTestWithLimitsTest extends ReceiveCardActionTest {
    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("Should return a map of players with the cards they can send")
    @Override
    protected void getTargetPlayersWithAvailableCardsAssociation() {
        getTargetPlayersWithAvailableCardsAssociation_empty();

        getTargetPlayersWithAvailableCardsAssociation_notEmpty();
    }

    private void getTargetPlayersWithAvailableCardsAssociation_empty() {
        setUp();

        targetPlayer.addHandCard(transferredCard);

        assertThat(action.getTargetPlayersWithAvailableCardsAssociation())
                .isEqualTo(Map.of());
    }

    private void getTargetPlayersWithAvailableCardsAssociation_notEmpty() {
        setUp();

        targetPlayer.addHandCard(transferredCard);
        targetPlayer.setCurrentField(transferredCardField);
        executingPlayer.setCurrentField(transferredCardField);

        assertThat(action.getTargetPlayersWithAvailableCardsAssociation())
                .isEqualTo(Map.of(targetPlayer, List.of(transferredCard)));
    }

    @Test
    @DisplayName("Should return a list containing all city cards on hand of target player with his current field associated to it")
    @Override
    protected void getSendableCardsOfPlayer() {
        getSendableCardsOfPlayer_empty();

        getSendableCardsOfPlayer_notEmpty();
    }

    private void getSendableCardsOfPlayer_empty() {
        setUp();

        targetPlayer.addHandCard(transferredCard);

        assertThat(action.getSendableCardsOfPlayer(targetPlayer))
                .isEmpty();
    }

    private void getSendableCardsOfPlayer_notEmpty() {
        setUp();

        targetPlayer.addHandCard(transferredCard);
        targetPlayer.setCurrentField(transferredCardField);

        assertThat(action.getSendableCardsOfPlayer(targetPlayer))
                .containsExactly(transferredCard);
    }
}