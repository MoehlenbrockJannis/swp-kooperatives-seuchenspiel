package de.uol.swp.common.chat.request;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.request.AbstractRequestMessage;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@NoArgsConstructor
public class RetrieveAllChatMessagesRequest extends AbstractRequestMessage {
    private Lobby lobby;
}
