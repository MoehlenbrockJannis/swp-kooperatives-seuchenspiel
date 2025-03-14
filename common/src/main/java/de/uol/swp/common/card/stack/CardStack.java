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

    /**
     * Retrieves the top {@code numberOfCards} cards from the list without removing them.
     * If {@code numberOfCards} is greater than the current list size, it returns all available cards.
     *
     * @param numberOfCards The number of cards to retrieve from the top of the list.
     * @return A list containing up to {@code numberOfCards} cards from the top.
     * @throws IllegalArgumentException If {@code numberOfCards} is negative.
     */
    public List<C> getTopCards(int numberOfCards) {
        if (numberOfCards < 0) {
            throw new IllegalArgumentException("Number of cards to peek cannot be negative.");
        }

        int currentSize = this.size();
        int count = Math.min(numberOfCards, currentSize);
        int startIndex = currentSize - count;

        List<C> topCards = new ArrayList<>(this.subList(startIndex, currentSize));
        Collections.reverse(topCards);
        return topCards;
    }
}
