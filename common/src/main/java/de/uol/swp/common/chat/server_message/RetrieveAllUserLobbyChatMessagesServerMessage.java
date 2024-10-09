package de.uol.swp.common.chat.server_message;

import de.uol.swp.common.lobby.server_message.AbstractUserLobbyServerMessage;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = false)
public class RetrieveAllUserLobbyChatMessagesServerMessage extends AbstractUserLobbyServerMessage {
    private final List<String> chatMessage = new ArrayList<>();

    public RetrieveAllUserLobbyChatMessagesServerMessage(List<String> chatMessage) {
        this.chatMessage.addAll(chatMessage);
    }
}
