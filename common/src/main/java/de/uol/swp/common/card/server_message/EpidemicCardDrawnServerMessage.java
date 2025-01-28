package de.uol.swp.common.card.server_message;

import de.uol.swp.common.card.EpidemicCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.message.server.AbstractServerMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Server message that is sent when an epidemic card is drawn.
 * This message indicates that an epidemic has been triggered in the game,
 * which leads to:
 * 1. Increased infection rate
 * 2. Infection of a city from the bottom card with three plague cubes
 * 3. Reshuffling of the infection discard pile onto the draw pile
 */
@AllArgsConstructor
@Getter
public class EpidemicCardDrawnServerMessage extends AbstractServerMessage {

    private final Game game;
    private final EpidemicCard epidemicCard;
}