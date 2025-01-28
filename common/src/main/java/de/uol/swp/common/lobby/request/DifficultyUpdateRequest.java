package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.request.AbstractRequestMessage;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Request to update the difficulty level in a lobby
 *
 * This request is sent when the lobby owner changes the difficulty setting
 * (number of epidemic cards) in the lobby.
 *
 * @see AbstractRequestMessage
 * @since 2025-01-28
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
public class DifficultyUpdateRequest extends AbstractRequestMessage {
    private final Lobby lobby;
    private final int numberOfEpidemicCards;
}