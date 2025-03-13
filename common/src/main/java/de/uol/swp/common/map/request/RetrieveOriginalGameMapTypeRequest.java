package de.uol.swp.common.map.request;

import de.uol.swp.common.message.request.AbstractRequestMessage;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Request sent to the server to retrieve the mapType for the original game
 *
 * @see de.uol.swp.common.map.MapType
 */
@EqualsAndHashCode(callSuper = false)
@Getter
public class RetrieveOriginalGameMapTypeRequest extends AbstractRequestMessage {
}
