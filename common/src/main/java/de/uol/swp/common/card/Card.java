package de.uol.swp.common.card;

import de.uol.swp.common.util.Color;

import java.io.Serializable;

/**
 * Represents an abstract card in the game.
 * <p>
 * This class serves as a base for different types of cards in the game, providing common properties
 * such as a title and a color. Subclasses must implement the {@link #getTitle()} and {@link #getColor()} methods.
 * </p>
 */
public abstract class Card implements Serializable {

    public abstract String getTitle();

    public abstract Color getColor();

    @Override
    public String toString() {
        return getTitle();
    }
}
