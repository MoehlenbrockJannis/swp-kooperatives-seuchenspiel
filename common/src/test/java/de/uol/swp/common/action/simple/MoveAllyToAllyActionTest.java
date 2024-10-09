package de.uol.swp.common.action.simple;

import de.uol.swp.common.map.Field;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.UserPlayer;
import de.uol.swp.common.user.UserDTO;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class MoveAllyToAllyActionTest extends AbstractMoveActionTest {
    @Getter
    private MoveAllyToAllyAction action;
    private Player player1;
    private Player player2;
    private Player player3;

    @BeforeEach
    void setUp() {
        player1 = new AIPlayer("moved");
        player2 = new UserPlayer(new UserDTO("mover", "s", ""));
        player3 = new AIPlayer("target");

        action = new MoveAllyToAllyAction();
        action.setExecutingPlayer(player2);
        action.setMovedAlly(player1);
        action.setTargetAlly(player3);

        setUpFields();
    }

    @Test
    @DisplayName("Should return moved ally")
    void getApprovingPlayer() {
        assertThat(action.getApprovingPlayer())
                .usingRecursiveComparison()
                .isEqualTo(player1);
    }

    @Test
    @DisplayName("Should set the approval status to true")
    void approve() {
        assertThat(action.isApproved())
                .isFalse();

        action.approve();

        assertThat(action.isApproved())
                .isTrue();
    }

    @Test
    @DisplayName("Should return moved ally")
    void getMovedPlayer() {
        assertThat(action.getMovedPlayer())
                .usingRecursiveComparison()
                .isEqualTo(player1);
    }

    @Test
    @DisplayName("Should return fields with other players standing on them")
    void getAvailableFields() {
        when(action.getGame().getPlayersInTurnOrder())
                .thenReturn(List.of(player1, player2, player3));

        final Field field1 = allFields.get(0);
        player1.setCurrentField(field1);
        final Field field2 = allFields.get(1);
        player2.setCurrentField(field2);
        final Field field3 = allFields.get(2);
        player3.setCurrentField(field3);

        assertThat(action.getAvailableFields())
                .usingRecursiveComparison()
                .isEqualTo(List.of(field2, field3));
    }

    @Test
    @DisplayName("Should return specified moved ally")
    void getMovedAlly() {
        assertThat(action.getMovedAlly())
                .usingRecursiveComparison()
                .isEqualTo(player1);
    }

    @Test
    @DisplayName("Should return specified target ally")
    void getTargetAlly() {
        assertThat(action.getTargetAlly())
                .usingRecursiveComparison()
                .isEqualTo(player3);
    }

    @Test
    @DisplayName("Should return approval status")
    void isApproved() {
        assertThat(action.isApproved())
                .isFalse();
    }

    @Test
    @DisplayName("Should set given player as specified moved ally")
    void setMovedAlly() {
        assertThat(action.getMovedAlly())
                .usingRecursiveComparison()
                .isEqualTo(player1);

        action.setMovedAlly(player2);

        assertThat(action.getMovedAlly())
                .usingRecursiveComparison()
                .isEqualTo(player2);
    }

    @Test
    @DisplayName("Should set given player as target ally and target field to given player's current field")
    void setTargetAlly() {
        assertThat(action.getTargetAlly())
                .usingRecursiveComparison()
                .isEqualTo(player3);

        final Field field = allFields.get(0);
        player2.setCurrentField(field);

        action.setTargetAlly(player2);

        assertThat(action.getTargetAlly())
                .usingRecursiveComparison()
                .isEqualTo(player2);
        assertThat(action.getTargetField())
                .usingRecursiveComparison()
                .isEqualTo(field);
    }
}