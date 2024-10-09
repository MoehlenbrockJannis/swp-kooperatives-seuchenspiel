package de.uol.swp.common.chat.request;

import de.uol.swp.common.message.request.AbstractRequestMessage;
import de.uol.swp.common.user.User;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalTime;

@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
public class SendChatMessageRequest extends AbstractRequestMessage {
    private final User user;
    private final String chatMessage;
    private final LocalTime timestamp;
}
