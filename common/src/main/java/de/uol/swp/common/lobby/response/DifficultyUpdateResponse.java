package de.uol.swp.common.lobby.response;

import de.uol.swp.common.message.response.AbstractResponseMessage;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Response indicating the result of a difficulty update request
 *
 * @see AbstractResponseMessage
 * @since 2025-01-28
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
public class DifficultyUpdateResponse extends AbstractResponseMessage {
    private final boolean updateSuccessful;
}