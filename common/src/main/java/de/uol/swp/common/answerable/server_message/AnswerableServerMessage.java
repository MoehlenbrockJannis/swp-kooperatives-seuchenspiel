package de.uol.swp.common.answerable.server_message;

import de.uol.swp.common.answerable.Answerable;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.message.server.AbstractServerMessage;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.server_message.SendMessageByPlayerServerMessage;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * ServerMessage sent to communicate am {@link Answerable} between client and server.
 * It specifies a {@link Message} to send by a specified {@link Player} after.
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
public class AnswerableServerMessage extends AbstractServerMessage implements SendMessageByPlayerServerMessage {
    private final Answerable answerable;
    private final Message cause;
    private final Player returningPlayer;

    @Override
    public Game getGame() {
        return answerable.getGame();
    }

    @Override
    public Message getMessageToSend() {
        return cause;
    }
}
