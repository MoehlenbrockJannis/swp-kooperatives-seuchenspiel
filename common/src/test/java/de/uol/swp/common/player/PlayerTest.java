package de.uol.swp.common.player;

import de.uol.swp.common.card.CityCard;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test class for the UserPlayer class.
 * This class contains test methods to verify the functionality and behavior
 * of the UserPlayer class, including equality, hash code consistency, and hand card management.
 * It initializes the test environment by creating instances of UserDTO and UserPlayer
 * before each test.
 * The tests cover the following scenarios:
 * - Equality and hash code consistency of Player instances
 * - Correct behavior of the equals method when comparing different types of objects
 * - Successful addition and removal of cards from a UserPlayer's hand
 *
 * @see Player
 * @since 2024-09-18
 */
class PlayerTest {

    private User user1;
    private User user2;
    private Player player1;
    private Player player2;
    private Player player3;

    /**
     * This method is executed before each test case.
     * It initializes the test environment by creating instances of UserDTO and UserPlayer.
     * Specifically, it sets up:
     * - user1 with the name "Ralf", ID "2", and email "Ralf@mail.com"
     * - user2 with the name "Peter", ID "3", and email "Peter@mail.com"
     * - player1 and player2 as UserPlayer instances based on user1
     * - player3 as a UserPlayer instance based on user2
     *
     * This setup ensures that each test starts with a consistent and predefined state.
     *
     * @since 2024-09-18
     */
    @BeforeEach
    void setUp() {
        user1 = new UserDTO("Ralf", "2", "Ralf@mail.com");
        user2 = new UserDTO("Peter", "3", "Peter@mail.com");
        player1 = new UserPlayer(user1);
        player2 = new UserPlayer(user1);
        player3 = new UserPlayer(user2);
    }

    /**
     * This test verifies that the equals method correctly
     * identifies two Player instances as equal
     * when they are initialized with the same UserDTO instance.
     * It asserts that player1 and player2 are considered equal.
     *
     * @since 2024-09-18
     */
    @Test
    void equalsShouldReturnTrueForEqualPlayers() {
        assertThat(player1).isEqualTo(player2);
    }

    /**
     * This test verifies that the equals method correctly
     * identifies two Player instances as not equal
     * when they are initialized with different UserDTO instances.
     * It asserts that player1 and player3 are not considered equal.
     *
     * @since 2024-09-18
     */
    @Test
    void equalsShouldReturnFalseForDifferentPlayers() {
        assertThat(player1).isNotEqualTo(player3);
    }

    /**
     * This test verifies that the equals method returns false when compared with an object
     * that is not an instance of Player.
     * It asserts that player1 does not equal an Object instance that is not a Player.
     *
     * @since 2024-09-18
     */
    @Test
    void testEqualsReturnsFalseWhenObjectIsNotPlayer() {
        Object notAPlayer = new Object();
        assertThat(player1.equals(notAPlayer)).isFalse();
    }

    /**
     * This test verifies that the hashCode method is consistent with the equals method.
     * It asserts that player1 and player2, which are considered equal, have the same hash code.
     *
     * @since 2024-09-18
     */
    @Test
    void hashCodeShouldBeConsistentWithEquals() {
        assertThat(player1).hasSameHashCodeAs(player2);
    }

    /**
     * This test verifies that the hashCode method produces different hash codes
     * for Player instances that are not considered equal.
     * It asserts that player1 and player3, which are not equal, have different hash codes.
     *
     * @since 2024-09-18
     */
    @Test
    void hashCodeShouldBeDifferentForDifferentPlayers() {
        assertThat(player1.hashCode()).isNotEqualTo(player3.hashCode());
    }

    /**
     * This test verifies that a hand card is successfully added to a UserPlayer's hand.
     * It creates a new UserPlayer and a CityCard, adds the card to the player's hand,
     * and then asserts that the hand contains the added card.
     *
     * @since 2024-09-18
     */
    @Test
    void shouldAddHandCardSuccessfully() {
        CityCard card = new CityCard();

        this.player1.addHandCard(card);
        assertThat(this.player1.getHandCards()).contains(card);
    }

    /**
     * This test verifies that a card can be successfully added to
     * and then removed from a UserPlayer's hand.
     * It creates a new UserPlayer and a CityCard,
     * adds the card to the player's hand, removes it,
     * and then asserts that the hand no longer contains the card.
     *
     * @since 2024-09-18
     */
    @Test
    void shouldAddAndRemoveCardFromHand() {
        CityCard card = new CityCard();

        this.player1.addHandCard(card);

        assertThat(this.player1.getHandCards()).contains(card);

        this.player1.removeHandCard(card);

        boolean isCardPresent = this.player1.getHandCards().contains(card);
        assertThat(isCardPresent).isFalse();
    }

    @Test
    void getHandCardsTest() {
        List<PlayerCard> expectedHandCards = new ArrayList<>();

        CityCard card1 = new CityCard();
        CityCard card2 = new CityCard();

        this.player1.addHandCard(card1);
        this.player1.addHandCard(card2);

        expectedHandCards.add(card1);
        expectedHandCards.add(card2);

        List<PlayerCard> actualHandCards = player1.getHandCards();

        assertThat(actualHandCards).isEqualTo(expectedHandCards);
    }
}
