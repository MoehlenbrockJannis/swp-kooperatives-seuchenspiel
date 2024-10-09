package de.uol.swp.common.card.response;

import de.uol.swp.common.card.InfectionCard;
import de.uol.swp.common.message.response.AbstractResponseMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Response message sent when an infection card is drawn.
 * This response contains the drawn infection card.
 */
@AllArgsConstructor
@Getter
public class DrawInfectionCardResponse extends AbstractResponseMessage {

    private final InfectionCard infectionCard;
}
