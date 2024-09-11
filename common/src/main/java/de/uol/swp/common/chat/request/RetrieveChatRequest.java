package de.uol.swp.common.chat.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class RetrieveChatRequest extends AbstractRequestMessage {
    @Getter
    private String lobbyName;
}
