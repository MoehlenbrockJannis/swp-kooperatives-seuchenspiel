package de.uol.swp.common.chat.request;

import de.uol.swp.common.message.request.AbstractRequestMessage;
import de.uol.swp.common.user.User;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalTime;

/**
 * Request message to send a chat message to a chat system.
 * <p>
 * This request contains the sender's user information, the chat message text, and the timestamp
 * representing when the message was sent.
 * </p>
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
public class SendChatMessageRequest extends AbstractRequestMessage {
    private final User user;
    private final String chatMessage;
    private final LocalTime timestamp;
}
