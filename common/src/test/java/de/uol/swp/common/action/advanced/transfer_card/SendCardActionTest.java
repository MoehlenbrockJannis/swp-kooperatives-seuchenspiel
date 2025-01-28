package de.uol.swp.common.action.advanced.transfer_card;

import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SendCardActionTest extends ShareKnowledgeActionTest {
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
    @DisplayName("Should return a valid approval request message")
    @Override
    protected void getApprovalRequestMessage() {
        assertThat(action.getApprovalRequestMessage())
                .isEqualTo(player1Name + " möchte " + player2Name + " die Karte " + fieldName + " vermachen.");
    }

    @Test
    @DisplayName("Should return a valid approved message")
    @Override
    protected void getApprovedMessage() {
        assertThat(action.getApprovedMessage())
                .isEqualTo(player2Name + " hat angenommen. " + player1Name + " hat " + player2Name + " die Karte " + fieldName + " vermacht.");
    }

    @Test
    @DisplayName("Should return a valid rejected message")
    @Override
    protected void getRejectedMessage() {
        assertThat(action.getRejectedMessage())
                .isEqualTo(player2Name + " hat abgelehnt. " + player1Name + " hat " + player2Name + " die Karte " + fieldName + " nicht vermacht.");
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
    @DisplayName("Should return the correct player to cards association")
    @Override
    protected void getTargetPlayersWithAvailableCardsAssociation() {
        getTargetPlayersWithAvailableCardsAssociation_noSendableCards();

        getTargetPlayersWithAvailableCardsAssociation_sendableCards();
    }

    private void getTargetPlayersWithAvailableCardsAssociation_noSendableCards() {
        setUp();

        assertThat(action.getTargetPlayersWithAvailableCardsAssociation())
                .isEmpty();
    }

    private void getTargetPlayersWithAvailableCardsAssociation_sendableCards() {
        setUp();

        action.getSender().addHandCard(transferredCard);
        executingPlayer.setCurrentField(transferredCardField);
        targetPlayer.setCurrentField(transferredCardField);

        assertThat(action.getTargetPlayersWithAvailableCardsAssociation())
                .isEqualTo(Map.of(targetPlayer, List.of(transferredCard)));
    }

    @Test
    @DisplayName("Should return a list of sendable cards with same field of the players")
    protected void getSendableCards() {
        action.getSender().addHandCard(transferredCard);
        executingPlayer.setCurrentField(transferredCardField);
        targetPlayer.setCurrentField(transferredCardField);

        assertThat(action.getSendableCards())
                .contains(transferredCard);
    }
}