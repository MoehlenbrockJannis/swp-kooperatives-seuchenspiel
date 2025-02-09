package de.uol.swp.common.card.server_message;

import de.uol.swp.common.card.EpidemicCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.message.server.AbstractServerMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Server message that is sent when an epidemic card is drawn.
 */
@AllArgsConstructor
@Getter
public class EpidemicCardDrawnServerMessage extends AbstractServerMessage {

    private final Game game;
    private final EpidemicCard epidemicCard;
}