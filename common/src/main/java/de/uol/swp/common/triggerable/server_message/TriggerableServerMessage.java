package de.uol.swp.common.triggerable.server_message;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.message.server.AbstractServerMessage;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.server_message.SendMessageByPlayerServerMessage;
import de.uol.swp.common.triggerable.Triggerable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * ServerMessage sent to communicate a {@link Triggerable} between client and server.
 * It specifies a {@link Message} to send by a specified {@link Player} after.
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
public class TriggerableServerMessage extends AbstractServerMessage implements SendMessageByPlayerServerMessage {
    private final Triggerable triggerable;
    private Message cause;
    private final Player returningPlayer;

    @Override
    public Game getGame() {
        return triggerable.getGame();
    }

    @Override
    public Message getMessageToSend() {
        return cause;
    }

    @Override
    public void setMessageToSend(final Message message) {
        this.cause = message;
    }
}
