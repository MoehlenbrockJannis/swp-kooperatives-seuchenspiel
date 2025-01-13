package de.uol.swp.common.action.advanced.transfer_card;

import de.uol.swp.common.card.CityCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.map.GameMap;
import de.uol.swp.common.map.MapSlot;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.UserPlayer;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.util.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

abstract class ShareKnowledgeActionTest {
    private ShareKnowledgeAction action;

    protected Player executingPlayer;
    protected Game game;
    protected Player targetPlayer;
    protected CityCard transferredCard;
    protected Field transferredCardField;
    protected List<Player> allPlayers;

    protected abstract ShareKnowledgeAction getAction();

    @BeforeEach
    void setUp() {
        executingPlayer = new UserPlayer(new UserDTO("test", "", ""));

        targetPlayer = new AIPlayer("target locked");

        allPlayers = new ArrayList<>(List.of(
                executingPlayer,
                targetPlayer
        ));

        game = mock(Game.class);
        when(game.getPlayersInTurnOrder())
                .thenReturn(allPlayers);

        final Set<Plague> plagueSet = new HashSet<>();
        plagueSet.add(new Plague("testPlague", new Color(1, 2, 3)));

        final MapType mapType = mock(MapType.class);
        when(mapType.getUniquePlagues())
                .thenReturn(plagueSet);

        final GameMap map = mock(GameMap.class);
        when(map.getType())
                .thenReturn(mapType);
        final MapSlot mapSlot = mock(MapSlot.class);
        transferredCardField = new Field(map, mapSlot);
        transferredCard = new CityCard(transferredCardField);

        action = getAction();
        action.setExecutingPlayer(executingPlayer);
        action.setGame(game);
        action.setTargetPlayer(targetPlayer);
        action.setTransferredCard(transferredCard);
    }

    @Test
    @DisplayName("Should return true if both players are on the same field and the transferred card is of the field they are on")
    void isAvailable_true() {
        action.getSender().addHandCard(transferredCard);
        executingPlayer.setCurrentField(transferredCardField);
        targetPlayer.setCurrentField(transferredCardField);

        assertThat(action.isAvailable())
                .isTrue();
    }

    @Test
    @DisplayName("Should return false if both players are not on the same field or the transferred card is not on the sender's hand")
    void isAvailable_false() {
        isAvailable_falsePlayersNotOnSameField();

        isAvailable_falseSenderDoesNotHaveCard();
    }

    void isAvailable_falsePlayersNotOnSameField() {
        setUp();

        executingPlayer.setCurrentField(transferredCardField);

        assertThat(action.isAvailable())
                .isFalse();
    }

    void isAvailable_falseSenderDoesNotHaveCard() {
        setUp();

        executingPlayer.setCurrentField(transferredCardField);
        targetPlayer.setCurrentField(transferredCardField);

        assertThat(action.isAvailable())
                .isFalse();
    }

    @Test
    @DisplayName("Should return true if both players are on the same field, the transferred card is of the field they are on and the action is approved")
    void isExecutable_true() {
        action.getSender().addHandCard(transferredCard);
        executingPlayer.setCurrentField(transferredCardField);
        targetPlayer.setCurrentField(transferredCardField);

        action.approve();

        assertThat(action.isExecutable())
                .isTrue();
    }

    @Test
    @DisplayName("Should return false if the action is unavailable or not approved or the sender does not have the card or the players are not on the same field")
    void isExecutable_false() {
        isExecutable_falseUnavailable();

        isExecutable_falseNotApproved();

        isExecutable_falseSenderDoesNotHaveCard();

        isExecutable_falsePlayersNotOnSameField();
    }

    void isExecutable_falseUnavailable() {
        setUp();

        assertThat(action.isExecutable())
                .isFalse();
    }

    void isExecutable_falseNotApproved() {
        setUp();

        action.getSender().addHandCard(transferredCard);
        executingPlayer.setCurrentField(transferredCardField);
        targetPlayer.setCurrentField(transferredCardField);

        assertThat(action.isExecutable())
                .isFalse();
    }

    void isExecutable_falseSenderDoesNotHaveCard() {
        setUp();

        executingPlayer.setCurrentField(transferredCardField);
        targetPlayer.setCurrentField(transferredCardField);

        action.approve();

        assertThat(action.isExecutable())
                .isFalse();
    }

    void isExecutable_falsePlayersNotOnSameField() {
        setUp();

        action.getSender().addHandCard(transferredCard);
        executingPlayer.setCurrentField(transferredCardField);
        targetPlayer.setCurrentField(transferredCardField);

        action.approve();

        final Set<Plague> plagueSet = new HashSet<>();
        plagueSet.add(new Plague("testPlague", new Color(1, 2, 3)));

        final MapType mapType = mock(MapType.class);
        when(mapType.getUniquePlagues())
                .thenReturn(plagueSet);

        final GameMap map = mock(GameMap.class);
        when(map.getType())
                .thenReturn(mapType);
        final MapSlot mapSlot = mock(MapSlot.class);
        final Field newField = new Field(map, mapSlot);
        targetPlayer.setCurrentField(newField);

        final Player player = new AIPlayer("equalField");
        player.setCurrentField(transferredCardField);
        allPlayers.add(player);

        assertThat(action.isExecutable())
                .isFalse();
    }

    @Test
    @DisplayName("Should return the target player")
    void getApprovingPlayer() {
        assertThat(action.getApprovingPlayer())
                .usingRecursiveComparison()
                .isEqualTo(targetPlayer);
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
    @DisplayName("Should remove the card from the sender's hand and add it to the receiver's if executable")
    void execute() {
        action.getSender().addHandCard(transferredCard);
        executingPlayer.setCurrentField(transferredCardField);
        targetPlayer.setCurrentField(transferredCardField);

        action.approve();

        action.execute();

        assertThat(action.getReceiver().getHandCards())
                .contains(transferredCard);
        assertThat(action.getSender().getHandCards())
                .doesNotContain(transferredCard);
    }

    @Test
    @DisplayName("Should throw an exception if not executable")
    void execute_error() {
        executingPlayer.setCurrentField(transferredCardField);
        targetPlayer.setCurrentField(transferredCardField);

        assertThatThrownBy(() -> action.execute())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Should return the target player")
    void getTargetPlayer() {
        assertThat(action.getTargetPlayer())
                .usingRecursiveComparison()
                .isEqualTo(targetPlayer);
    }

    @Test
    @DisplayName("Should return the transferred card")
    void getTransferredCard() {
        assertThat(action.getTransferredCard())
                .usingRecursiveComparison()
                .isEqualTo(transferredCard);
    }

    @Test
    @DisplayName("Should return approval status")
    void isApproved() {
        assertThat(action.isApproved())
                .isFalse();
    }

    @Test
    @DisplayName("Should set the given player as target player")
    void setTargetPlayer() {
        assertThat(action.getTargetPlayer())
                .usingRecursiveComparison()
                .isEqualTo(targetPlayer);

        action.setTargetPlayer(executingPlayer);

        assertThat(action.getTargetPlayer())
                .usingRecursiveComparison()
                .isEqualTo(executingPlayer);
    }

    @Test
    @DisplayName("Should set the given card as transferred card")
    void setTransferredCard() {
        assertThat(action.getTransferredCard())
                .usingRecursiveComparison()
                .isEqualTo(transferredCard);

        action.setTransferredCard(null);

        assertThat(action.getTransferredCard())
                .isNull();
    }
}