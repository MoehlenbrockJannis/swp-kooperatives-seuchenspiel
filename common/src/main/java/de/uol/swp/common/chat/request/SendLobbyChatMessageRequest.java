package de.uol.swp.common.chat.request;

import de.uol.swp.common.lobby.message.AbstractLobbyRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class SendLobbyChatMessageRequest extends AbstractLobbyRequest {

    private final String chatMessage;
    private final LocalTime timestamp;

}

