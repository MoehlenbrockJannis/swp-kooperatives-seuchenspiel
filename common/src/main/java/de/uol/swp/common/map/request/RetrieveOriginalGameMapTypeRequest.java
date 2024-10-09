package de.uol.swp.common.map.request;

import de.uol.swp.common.message.request.AbstractRequestMessage;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Request sent to the server to retrieve the mapType for the original game
 *
 * @see de.uol.swp.common.map.MapType
 * @author David Scheffler
 * @since 2024-09-22
 */
@EqualsAndHashCode(callSuper = false)
@Getter
public class RetrieveOriginalGameMapTypeRequest extends AbstractRequestMessage {
}
