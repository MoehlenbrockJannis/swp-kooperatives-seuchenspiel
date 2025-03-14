package de.uol.swp.common.player;

import de.uol.swp.common.action.Action;
import de.uol.swp.common.action.GeneralAction;
import de.uol.swp.common.action.RoleAction;
import de.uol.swp.common.action.simple.MoveAction;
import de.uol.swp.common.action.simple.MoveAllyAction;
import de.uol.swp.common.card.CityCard;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.map.GameMap;
import de.uol.swp.common.map.MapSlot;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.role.RoleCard;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.util.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlayerTest {
    private String user1Name;
    private String user2Name;
    private User user1;
    private User user2;
    private Player player1;
    private Player player2;
    private Player player3;
    private Field field1;
    private Field field2;
    private CityCard cityCardField1;
    private CityCard cityCardField2;

    @BeforeEach
    void setUp() {
        field1 = createField();
        field2 = createField();

        cityCardField1 = new CityCard(field1);
        cityCardField2 = new CityCard(field2);

        user1Name = "Ralf";
        user2Name = "Peter";

        user1 = new UserDTO(user1Name, "2", "Ralf@mail.com");
        user2 = new UserDTO(user2Name, "3", "Peter@mail.com");

        player1 = new UserPlayer(user1);
        player2 = new UserPlayer(user1);
        player3 = new UserPlayer(user2);
    }

    private Field createField() {
        final Set<Plague> plagueSet = new HashSet<>();
        plagueSet.add(new Plague("testPlague", new Color(1, 2, 3)));

        final MapType mapType = mock(MapType.class);
        when(mapType.getUniquePlagues())
                .thenReturn(plagueSet);

        final GameMap map = mock(GameMap.class);
        when(map.getType())
                .thenReturn(mapType);
        final MapSlot mapSlot = mock(MapSlot.class);
        return new Field(map, mapSlot);
    }

    @Test
    @DisplayName("Should return the specified name")
    void testToString() {
        assertThat(player1)
                .hasToString(user1Name);
    }

    @Test
    @DisplayName("Should return true if both objects have same name")
    void equalsShouldReturnTrueForEqualPlayers() {
        assertThat(player1).isEqualTo(player2);
    }


    @Test
    @DisplayName("Should return false if both objects do not have same name")
    void equalsShouldReturnFalseForDifferentPlayers() {
        assertThat(player1).isNotEqualTo(player3);
    }

    @Test
    @DisplayName("Should return false if objects are of different types")
    void testEqualsReturnsFalseWhenObjectIsNotPlayer() {
        Object notAPlayer = new Object();
        assertThat(player1.equals(notAPlayer)).isFalse();
    }

    @Test
    @DisplayName("Equal objects should have same hashCode")
    void hashCodeShouldBeConsistentWithEquals() {
        assertThat(player1).hasSameHashCodeAs(player2);
    }

    @Test
    @DisplayName("Unequal objects should not have same hashCode")
    void hashCodeShouldBeDifferentForDifferentPlayers() {
        assertThat(player1).doesNotHaveSameHashCodeAs(player3);
    }

    @Test
    @DisplayName("Hand cards should contain given card after call")
    void shouldAddHandCardSuccessfully() {
        CityCard card = mock(CityCard.class);

        assertThat(this.player1.getHandCards()).doesNotContain(card);

        this.player1.addHandCard(card);
        assertThat(this.player1.getHandCards()).contains(card);
    }

    @Test
    @DisplayName("Hand cards should not contain given card after call")
    void shouldAddAndRemoveCardFromHand() {
        CityCard card = mock(CityCard.class);

        this.player1.addHandCard(card);

        assertThat(this.player1.getHandCards()).contains(card);

        this.player1.removeHandCard(card);

        boolean isCardPresent = this.player1.getHandCards().contains(card);
        assertThat(isCardPresent).isFalse();
    }

    @Test
    @DisplayName("Should return list of hand cards")
    void getHandCardsTest() {
        List<PlayerCard> expectedHandCards = new ArrayList<>();

        CityCard card1 = mock(CityCard.class);
        CityCard card2 = mock(CityCard.class);

        this.player1.addHandCard(card1);
        this.player1.addHandCard(card2);

        expectedHandCards.add(card1);
        expectedHandCards.add(card2);

        List<PlayerCard> actualHandCards = player1.getHandCards();

        assertThat(actualHandCards).isEqualTo(expectedHandCards);
    }

    @Test
    @DisplayName("Should return true if both players do not have specified current field")
    void hasSharedCurrentFieldWith_trueEmpty() {
        assertThat(player1.hasSharedCurrentFieldWith(player2))
                .isTrue();
    }

    @Test
    @DisplayName("Should return true if both players have equal specified current field")
    void hasSharedCurrentFieldWith_trueNotEmpty() {
        player1.setCurrentField(field1);
        player2.setCurrentField(field1);

        assertThat(player1.hasSharedCurrentFieldWith(player2))
                .isTrue();
    }

    @Test
    @DisplayName("Should return false if one player does not have a specified current field")
    void hasSharedCurrentFieldWith_falseEmpty() {
        player2.setCurrentField(field1);

        assertThat(player1.hasSharedCurrentFieldWith(player2))
                .isFalse();
    }

    @Test
    @DisplayName("Should return false if both players have different specified current fields")
    void hasSharedCurrentFieldWith_falseNotEmpty() {
        player1.setCurrentField(field1);
        player2.setCurrentField(field2);

        assertThat(player1.hasSharedCurrentFieldWith(player2))
                .isFalse();
    }

    @Test
    @DisplayName("Should return true if player has a hand card with given field associated to it")
    void hasHandCardOfField_true() {
        player1.addHandCard(cityCardField1);

        assertThat(player1.hasHandCardOfField(field1))
                .isTrue();
    }

    @Test
    @DisplayName("Should return false if player does not have a hand card with given field associated to it")
    void hasHandCardOfField_false() {
        player1.addHandCard(cityCardField2);

        assertThat(player1.hasHandCardOfField(field1))
                .isFalse();
    }

    @Test
    @DisplayName("Should return true if player has given hand card")
    void hasHandCard_true() {
        player1.addHandCard(cityCardField1);

        assertThat(player1.hasHandCard(cityCardField1))
                .isTrue();
    }

    @Test
    @DisplayName("Should return false if player does not have given hand card")
    void hasHandCard_false() {
        assertThat(player1.hasHandCard(cityCardField1))
                .isFalse();
    }

    @Test
    @DisplayName("Should return true if player has at least one hand card")
    void hasHandCards_true() {
        player1.addHandCard(cityCardField1);

        assertThat(player1.hasHandCards())
                .isTrue();
    }

    @Test
    @DisplayName("Should return false if player does not have any hand cards")
    void hasHandCards_false() {
        assertThat(player1.hasHandCards())
                .isFalse();
    }

    @Test
    @DisplayName("Should return result of getRoleSpecificActionClassOrDefault() call on role if player has role")
    void getRoleSpecificActionClassOrDefault_withRole() {
        final Class<? extends Action> input = Action.class;
        final Class<? extends Action> output = RoleAction.class;

        final RoleCard role = mock(RoleCard.class);
        Mockito.<Class<? extends Action>>when(role.getRoleSpecificActionClassOrDefault(input))
                .thenReturn(output);

        player1.setRole(role);

        assertThat(player1.getRoleSpecificActionClassOrDefault(input))
                .isEqualTo(output);
    }

    @Test
    @DisplayName("Should return given Action class object if player does not have a role")
    void getRoleSpecificActionClassOrDefault_withoutRole() {
        final Class<? extends Action> input = Action.class;

        assertThat(player1.getRoleSpecificActionClassOrDefault(input))
                .isEqualTo(input);
    }

    @Test
    @DisplayName("Should return result of getRoleSpecificAdditionallyAvailableActionClasses() call on role if player has role")
    void getRoleSpecificAdditionallyAvailableActionClasses_withRole() {
        final Set<Class<? extends RoleAction>> output = Set.of(
                RoleAction.class,
                MoveAllyAction.class
        );

        final RoleCard role = mock(RoleCard.class);
        when(role.getRoleSpecificAdditionallyAvailableActionClasses())
                .thenReturn(output);

        player1.setRole(role);

        assertThat(player1.getRoleSpecificAdditionallyAvailableActionClasses())
                .containsExactlyInAnyOrderElementsOf(output);
    }

    @Test
    @DisplayName("Should return empty set if player does not have a role")
    void getRoleSpecificAdditionallyAvailableActionClasses_withoutRole() {
        final Set<Class<? extends RoleAction>> output = Set.of();

        assertThat(player1.getRoleSpecificAdditionallyAvailableActionClasses())
                .containsExactlyInAnyOrderElementsOf(output);
    }

    @Test
    @DisplayName("Should return result of getRoleSpecificUnavailableActionClasses() call on role if player has role")
    void getRoleSpecificUnavailableActionClasses_withRole() {
        final Set<Class<? extends GeneralAction>> output = Set.of(
                GeneralAction.class,
                MoveAction.class
        );

        final RoleCard role = mock(RoleCard.class);
        when(role.getRoleSpecificUnavailableActionClasses())
                .thenReturn(output);

        player1.setRole(role);

        assertThat(player1.getRoleSpecificUnavailableActionClasses())
                .containsExactlyInAnyOrderElementsOf(output);
    }

    @Test
    @DisplayName("Should return empty set if player does not have a role")
    void getRoleSpecificUnavailableActionClasses_withoutRole() {
        final Set<Class<? extends GeneralAction>> output = Set.of();

        assertThat(player1.getRoleSpecificUnavailableActionClasses())
                .containsExactlyInAnyOrderElementsOf(output);
    }
}
