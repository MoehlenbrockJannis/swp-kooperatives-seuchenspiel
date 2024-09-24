package de.uol.swp.common.card.response;

import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.message.response.AbstractResponseMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A response to the DrawPlayerCardRequest
 */
@AllArgsConstructor
@Getter
public class DrawPlayerCardResponse extends AbstractResponseMessage {

    private PlayerCard playerCard;
}
