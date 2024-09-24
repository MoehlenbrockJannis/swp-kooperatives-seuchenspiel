package de.uol.swp.common.card.stack;

import de.uol.swp.common.card.Card;

import java.util.Stack;

/**
 * A stack of cards that extends the functionality of the standard {@link Stack} class.
 *
 * @param <C> the type of card that this stack will hold, which must extend the {@link Card} class
 */
public class CardStack<C extends Card> extends Stack<C> {

}
