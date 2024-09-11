package de.uol.swp.common.chat.message;

import de.uol.swp.common.lobby.message.AbstractLobbyMessage;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode
public class LobbyChatRetrieveAllMessagesMessage extends AbstractLobbyMessage {

    private final List<String> chatMessage = new ArrayList<>();

    public LobbyChatRetrieveAllMessagesMessage(List<String> chatMessage) {
        this.chatMessage.addAll(chatMessage);
    }
}
