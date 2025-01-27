package de.uol.swp.common.action.advanced.transfer_card;

import de.uol.swp.common.action.GeneralAction;
import de.uol.swp.common.action.RoleAction;
import de.uol.swp.common.card.CityCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.*;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.UserPlayer;
import de.uol.swp.common.role.RoleAbility;
import de.uol.swp.common.role.RoleCard;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.util.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

abstract class ShareKnowledgeActionTest {
    private static class ReceiveCardActionDummy extends ReceiveCardAction implements RoleAction {
        private ReceiveCardActionDummy() {}
    }

    private ShareKnowledgeAction action;

    protected Player executingPlayer;
    protected Game game;
    protected Player targetPlayer;
    protected CityCard transferredCard;
    protected Field transferredCardField;
    protected List<Player> allPlayers;

    protected String player1Name;
    protected String player2Name;
    protected String fieldName;

    protected abstract ShareKnowledgeAction getAction();

    @BeforeEach
    void setUp() {
        player1Name = "test";
        player2Name = "target locked";

        executingPlayer = new UserPlayer(new UserDTO(player1Name, "", ""));

        targetPlayer = new AIPlayer(player2Name);

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
        when(mapSlot.getCity())
                .thenReturn(new City(fieldName, ""));
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

    protected abstract void getApprovalRequestMessage();

    protected abstract void getApprovedMessage();

    protected abstract void getRejectedMessage();

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

    protected abstract void getTargetPlayersWithAvailableCardsAssociation();

    @Test
    @DisplayName("Should return a list with all players on the same field")
    void getPlayersWithPossibilityOfTransfer() {
        executingPlayer.setCurrentField(transferredCardField);
        targetPlayer.setCurrentField(transferredCardField);

        assertThat(action.getPlayersWithPossibilityOfTransfer())
                .contains(targetPlayer);
    }

    @Test
    @DisplayName("Should return true if given player has same current field but is not equal to executing player")
    void isTransferPossibleWithPlayer_true() {
        executingPlayer.setCurrentField(transferredCardField);
        targetPlayer.setCurrentField(transferredCardField);

        assertThat(action.isTransferPossibleWithPlayer(targetPlayer))
                .isTrue();
    }

    @Test
    @DisplayName("Should return false if players are equal")
    void isTransferPossibleWithPlayer_falseSamePlayer() {
        assertThat(action.isTransferPossibleWithPlayer(executingPlayer))
                .isFalse();
    }

    @Test
    @DisplayName("Should return false if players are not on the same field")
    void isTransferPossibleWithPlayer_falseDifferentField() {
        executingPlayer.setCurrentField(transferredCardField);

        assertThat(action.isTransferPossibleWithPlayer(targetPlayer))
                .isFalse();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(classes = NoLimitsSendCardAction.class)
    @DisplayName("Should return the replaced SendCardAction class object if the sender has a custom action")
    void getSendCardActionClass(final Class<? extends RoleAction> replacement) {
        assignRoleActionToPlayer(SendCardAction.class, replacement, executingPlayer);

        assertThat(this.action.getSendCardActionClass(executingPlayer))
                .isEqualTo(replacement != null ? replacement : SendCardAction.class);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(classes = ReceiveCardActionDummy.class)
    @DisplayName("Should return the replaced ReceiveCardAction class object if the receiver has a custom action")
    void getReceiveCardActionClass(final Class<? extends RoleAction> replacement) {
        assignRoleActionToPlayer(ReceiveCardAction.class, replacement, executingPlayer);

        assertThat(this.action.getReceiveCardActionClass(executingPlayer))
                .isEqualTo(replacement != null ? replacement : ReceiveCardAction.class);
    }

    private void assignRoleActionToPlayer(final Class<? extends GeneralAction> action, final Class<? extends RoleAction> replacement, final Player player) {
        final Map<Class<? extends GeneralAction>, Class<? extends RoleAction>> replacementsMap = new HashMap<>();
        if (action != null && replacement != null) {
            replacementsMap.put(action, replacement);
        }
        final RoleAbility ability = new RoleAbility(
                replacementsMap,
                new ArrayList<>(),
                new ArrayList<>()
        );
        final RoleCard role = new RoleCard("", new Color(), ability);
        player.setRole(role);
    }

    @Test
    @DisplayName("Should create an instance of a ShareKnowledgeAction with executing player and game")
    void createShareKnowledgeAction() {
        final Class<? extends ShareKnowledgeAction> clazz = SendCardAction.class;
        final Player player = targetPlayer;
        final Game sameGame = game;

        final ShareKnowledgeAction expected = new SendCardAction();
        expected.setExecutingPlayer(targetPlayer);
        expected.setGame(sameGame);

        assertThat(action.createShareKnowledgeAction(clazz, player, sameGame))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Should throw exception if an error occurs during instantiation")
    void createShareKnowledgeAction_error() {
        final Class<? extends ShareKnowledgeAction> clazz = ReceiveCardActionDummy.class;
        final Player player = targetPlayer;
        final Game sameGame = game;

        assertThatThrownBy(() -> action.createShareKnowledgeAction(clazz, player, sameGame))
                .isInstanceOf(IllegalStateException.class);
    }
}