package de.uol.swp.common.player;

import de.uol.swp.common.action.Action;
import de.uol.swp.common.action.GeneralAction;
import de.uol.swp.common.action.RoleAction;
import de.uol.swp.common.card.CityCard;
import de.uol.swp.common.card.OverviewCard;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.role.RoleCard;
import de.uol.swp.common.user.UserContainerEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

/**
 * The Player class represents an abstract player in the game.
 * It holds common player properties and actions that can be performed by any player.
 * This class is intended to be extended by specific types of players.
 */
@Getter
public abstract class Player implements Serializable, UserContainerEntity {
    @Setter
    private RoleCard role;
    @Setter
    private Field currentField;
    private List<PlayerCard> handCards;
    private Date lastSick;
    private OverviewCard overviewCard;

    /**
     * Constructor for creating a Player instance with the lastSick date.
     */
    protected Player() {
        this.handCards = new ArrayList<>();
    }

    @Override
    public String toString() {
        if(this.role != null) {
            return getName() + " (" + this.role.getName() + ")";
        }
        return getName();
    }

    /**
     * Retrieves the player's name.
     * This method should be overridden by subclasses to return the actual player's name.
     *
     * @return an empty string or the player's name if implemented in a subclass
     */
    public abstract String getName();

    /**
     * Adds a player card to the player's hand.
     *
     * @param playerCard the card to be added to the hand
     */
    public void addHandCard(PlayerCard playerCard) {
        this.handCards.add(playerCard);
    }

    /**
     * Removes a player card from the player's hand.
     *
     * @param playerCard the card to be removed from the hand
     */
    public void removeHandCard(PlayerCard playerCard) {
        this.handCards.remove(playerCard);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Player player && getClass() == player.getClass()) {
            return this.getName().equals(player.getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getClass());
    }

    /**
     * <p>
     *     Returns an unmodifiable {@link List} of all {@link #handCards}.
     * </p>
     *
     * @return an unmodifiable {@link List} of {@link #handCards}
     */
    public List<PlayerCard> getHandCards() {
        return Collections.unmodifiableList(this.handCards);
    }

    /**
     * <p>
     *     Checks if the this {@link Player} and the given {@link Player} share the same {@link #currentField}.
     *     Returns {@code true} if they do, {@code false} otherwise.
     *     If both players are not assigned a {@link #currentField} (i.e. it is {@code null}), also returns {@code true}.
     * </p>
     *
     * @param player {@link Player} to check {@link #currentField} equality for
     * @return {@code true} if {@link #currentField} of this and given {@link Player} are equal, {@code false} otherwise
     */
    public boolean hasSharedCurrentFieldWith(final Player player) {
        if (currentField == null) {
            return player.currentField == null;
        }
        return currentField.equals(player.currentField);
    }

    /**
     * <p>
     *     Checks if there is a {@link CityCard} with given {@link Field} as associated {@link Field} in {@link #handCards}.
     * </p>
     *
     * @param field to check existence of {@link CityCard} in {@link #handCards} for
     * @return {@code true} if a {@link CityCard} with associated {@link Field} {@code field} is in {@link #handCards}, {@code false} otherwise
     * @see CityCard#hasField(Field)
     */
    public boolean hasHandCardOfField(final Field field) {
        for (final PlayerCard handCard : handCards) {
            if (handCard instanceof CityCard cityCard && cityCard.hasField(field)) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>
     *     Returns {@code true} if given {@code card} is in {@link #handCards}, {@code false} otherwise.
     * </p>
     *
     * @param card {@link PlayerCard} to check if it is in {@link #handCards}
     * @return {@code true} if given {@code card} is in {@link #handCards}, {@code false} otherwise
     */
    public boolean hasHandCard(final PlayerCard card) {
        return handCards.contains(card);
    }

    /**
     * <p>
     *     Returns {@code true} if {@link #handCards} is not empty, {@code false} otherwise.
     * </p>
     *
     * @return {@code true} if {@link #handCards} is not empty, {@code false} otherwise
     */
    public boolean hasHandCards() {
        return !handCards.isEmpty();
    }

    /**
     * <p>
     *     Delegates to {@link RoleCard#getRoleSpecificActionClassOrDefault(Class)} of {@link #role}.
     * </p>
     *
     * <p>
     *     Returns given {@link Action} {@link Class} if {@link #role} is not set;
     * </p>
     *
     * @param actionClass {@link Action} {@link Class} to pass to {@link RoleCard#getRoleSpecificActionClassOrDefault(Class)}
     * @return result of {@link RoleCard#getRoleSpecificActionClassOrDefault(Class)} called with given {@code actionClass}
     * @see RoleCard#getRoleSpecificActionClassOrDefault(Class)
     */
    public Class<? extends Action> getRoleSpecificActionClassOrDefault(final Class<? extends Action> actionClass) {
        if (role == null) {
            return actionClass;
        }
        return role.getRoleSpecificActionClassOrDefault(actionClass);
    }

    /**
     * <p>
     *     Returns an unmodifiable {@link Set} of all {@link RoleAction} classes that the associated role can execute in addition to the general actions.
     * </p>
     *
     * <p>
     *     Delegates to {@link RoleCard#getRoleSpecificAdditionallyAvailableActionClasses()} of {@link #role}.
     * </p>
     *
     * <p>
     *     Returns empty {@link Set} if {@link #role} is not set;
     * </p>
     *
     * @return unmodifiable {@link Set} of all available {@link RoleAction} classes the associated role can execute
     * @see RoleCard#getRoleSpecificAdditionallyAvailableActionClasses()
     */
    public Set<Class<? extends RoleAction>> getRoleSpecificAdditionallyAvailableActionClasses() {
        if (role == null) {
            return Set.of();
        }
        return role.getRoleSpecificAdditionallyAvailableActionClasses();
    }

    /**
     * <p>
     *     Returns an unmodifiable {@link Set} of all {@link GeneralAction} classes that the associated role can no longer execute as they are replace by role actions.
     * </p>
     *
     * <p>
     *     Delegates to {@link RoleCard#getRoleSpecificUnavailableActionClasses()} of {@link #role}.
     * </p>
     *
     * <p>
     *     Returns empty {@link Set} if {@link #role} is not set;
     * </p>
     *
     * @return unmodifiable {@link Set} of all unavailable {@link GeneralAction} classes the associated role cannot execute
     * @see RoleCard#getRoleSpecificUnavailableActionClasses()
     */
    public Set<Class<? extends GeneralAction>> getRoleSpecificUnavailableActionClasses() {
        if (role == null) {
            return Set.of();
        }
        return role.getRoleSpecificUnavailableActionClasses();
    }

    /**
     * Retrieves the hand card that matches the given player card.
     *
     * @param card the player card to find in the hand
     * @return the matching player card from the hand
     */
    @SuppressWarnings("unchecked")
    public <P extends PlayerCard> P getHandCardForGivenPlayerCard(P card) {
        PlayerCard playerCard = handCards.get(handCards.indexOf(card));
        if (playerCard != null) {
            return (P) playerCard;
        }
        throw new NoSuchElementException("No matching card found in hand.");
    }
}
