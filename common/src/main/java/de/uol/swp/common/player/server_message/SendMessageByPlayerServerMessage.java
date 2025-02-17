package de.uol.swp.common.player.server_message;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.message.server.ServerMessage;
import de.uol.swp.common.player.Player;

public interface SendMessageByPlayerServerMessage extends ServerMessage {
    /**
     * Returns the {@link Game} the {@link Player} to send the {@link Message} is part of.
     *
     * @return the {@link Game} the {@link Player} to send the {@link Message} is part of
     */
    Game getGame();

    /**
     * Returns the {@link Player} to send the {@link Message}.
     *
     * @return the {@link Player} to send the {@link Message}
     */
    Player getReturningPlayer();

    /**
     * Returns the {@link Message} to send.
     *
     * @return the {@link Message} to send
     */
    Message getMessageToSend();
}
