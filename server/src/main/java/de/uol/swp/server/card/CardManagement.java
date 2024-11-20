package de.uol.swp.server.card;

import de.uol.swp.common.card.InfectionCard;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.game.Game;

public class CardManagement {

    /**
     * Discards a player card by pushing it onto the player's discard stack.
     *
     * @param game       The game from which the player card is to be discarded
     * @param playerCard The player card to be discarded
     */
    public void discardPlayerCard(Game game, PlayerCard playerCard) {
        game.getPlayerDiscardStack().push(playerCard);
    }

    /**
     * Draws an infection card from the top of the infection draw stack of a game.
     *
     * @param game The game from which the infection card is to be drawn
     */
    public InfectionCard drawInfectionCardFromTheTop(Game game) {
        return game.getInfectionDrawStack().pop();
    }

    /**
     * Draws an infection card from the bottom of the infection draw stack of a game.
     *
     * @param game The game from which the infection card is to be drawn
     */
    public InfectionCard drawInfectionCardFromTheBottom(Game game) {
        return game.getInfectionDrawStack().removeFirstCard();
    }

    /**
     * Discards an infection card by pushing it onto the infection discard stack.
     *
     * @param game          The game from which the infection card is to be discarded
     * @param infectionCard The infection card to be discarded
     */
    public void discardInfectionCard(Game game, InfectionCard infectionCard) {
        game.getInfectionDiscardStack().push(infectionCard);
    }

}
