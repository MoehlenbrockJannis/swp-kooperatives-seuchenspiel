package de.uol.swp.common.chat.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.request.AbstractRequestMessage;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request message to retrieve all chat messages for a specific lobby.
 * <p>
 * This request is used to fetch the entire chat history of a given {@link Lobby}.
 * </p>
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@NoArgsConstructor
public class RetrieveAllChatMessagesRequest extends AbstractRequestMessage {
    private Lobby lobby;
}
