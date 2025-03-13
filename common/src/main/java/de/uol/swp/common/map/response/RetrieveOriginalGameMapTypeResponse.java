package de.uol.swp.common.map.response;

import de.uol.swp.common.map.MapType;
import de.uol.swp.common.message.response.AbstractResponseMessage;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;


/**
 * This class receives the mapType based on the original game from the backend when a {@link de.uol.swp.common.plague.request.RetrieveAllPlaguesRequest} request is executed.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class RetrieveOriginalGameMapTypeResponse extends AbstractResponseMessage {
    private MapType mapType;
}
