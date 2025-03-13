package de.uol.swp.common.card;

import java.util.Objects;

/**
 * Represents an abstract Player card in the game.
 * <p>
 * This class serves as a base for all player cards and provides common functionality,
 * such as equality and hashing based on the card's title.
 * </p>
 */
public abstract class PlayerCard extends Card{

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerCard playerCard = (PlayerCard) o;
        return Objects.equals(getTitle(), playerCard.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle());
    }
}
