package de.uol.swp.common.player;

import de.uol.swp.common.card.OverviewCard;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.role.RoleCard;
import de.uol.swp.common.user.UserContainerEntity;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The Player class represents an abstract player in the game.
 * It holds common player properties and actions that can be performed by any player.
 * This class is intended to be extended by specific types of players.
 */
public abstract class Player implements Serializable, UserContainerEntity {

    @Getter
    @Setter
    private RoleCard role;
    @Getter
    @Setter
    private Field currentField;
    private List<PlayerCard> handCards;
    @Getter
    private Date lastSick;
    @Getter
    private OverviewCard overviewCard;

    /**
     * Constructor for creating a Player instance with the lastSick date.
     *
     * @param lastSick the date the player was last sick
     */
    protected Player(Date lastSick) {
        this.lastSick = lastSick;
        this.handCards = new ArrayList<>();
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
}
