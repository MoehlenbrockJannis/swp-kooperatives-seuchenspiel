package de.uol.swp.common.action.simple;

import de.uol.swp.common.map.Field;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MoveAllyActionTest {
    private MoveAllyAction moveAllyAction;
    private String player1Name;
    private String player2Name;
    private String fieldName;

    @BeforeEach
    void setUp() {
        moveAllyAction = new MoveAllyToAllyAction();

        player1Name = "player1";
        player2Name = "player2";
        fieldName = "field";

        final Player player1 = new AIPlayer(player1Name);
        final Player player2 = new AIPlayer(player2Name);
        final Field field = mock();
        when(field.toString())
                .thenReturn(fieldName);

        moveAllyAction.setExecutingPlayer(player1);
        moveAllyAction.setMovedAlly(player2);
        moveAllyAction.setTargetField(field);
    }

    @Test
    @DisplayName("Should return a valid approval request message")
    void getApprovalRequestMessage() {
        assertThat(moveAllyAction.getApprovalRequestMessage())
                .isEqualTo(player1Name + " möchte " + player2Name + " auf das Feld " + fieldName + " versetzen.");
    }

    @Test
    @DisplayName("Should return a valid approved message")
    void getApprovedMessage() {
        assertThat(moveAllyAction.getApprovedMessage())
                .isEqualTo(player2Name+ " hat angenommen. " + player1Name + " hat " + player2Name+ " auf das Feld " + fieldName + " versetzt.");
    }

    @Test
    @DisplayName("Should return a valid rejected message")
    void getRejectedMessage() {
        assertThat(moveAllyAction.getRejectedMessage())
                .isEqualTo(player2Name + " hat abgelehnt. " + player1Name + " hat " + player2Name + " nicht auf das Feld " + fieldName + " versetzt.");
    }
}