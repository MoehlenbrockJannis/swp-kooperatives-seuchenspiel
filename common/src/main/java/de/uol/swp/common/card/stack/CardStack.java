package de.uol.swp.common.card.stack;

import de.uol.swp.common.card.Card;

import java.util.*;

/**
 * A stack of cards that extends the functionality of the standard {@link Stack} class.
 *
 * @param <C> the type of card that this stack will hold, which must extend the {@link Card} class
 */
public class CardStack<C extends Card> extends Stack<C> {

    /**
     * Shuffles the cards in this stack using a default source of randomness.
     */
    public void shuffle() {
        Collections.shuffle(this, new Random());
    }

    /**
     * Removes and returns the first card from the stack.
     *
     * @return the first card from the stack
     */
    public C removeFirstCard() {
       return this.remove(0);
    }
}
