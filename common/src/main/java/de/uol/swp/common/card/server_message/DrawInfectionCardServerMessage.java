package de.uol.swp.common.card.server_message;

import de.uol.swp.common.card.InfectionCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.message.server_message.AbstractServerMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Server message that is sent when an infection card is drawn.
 */
@AllArgsConstructor
@Getter
public class DrawInfectionCardServerMessage extends AbstractServerMessage {

    private InfectionCard infectionCard;
    private Game game;
}
