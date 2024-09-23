package de.uol.swp.common.plague.request;

import de.uol.swp.common.message.request.AbstractRequestMessage;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Request sent to the server to retrieve all available plagues
 *
 * @see de.uol.swp.common.plague.Plague
 * @author David Scheffler
 * @since 2024-09-22
 */
@EqualsAndHashCode(callSuper = false)
@Getter
public class RetrieveAllPlaguesRequest extends AbstractRequestMessage {
}
