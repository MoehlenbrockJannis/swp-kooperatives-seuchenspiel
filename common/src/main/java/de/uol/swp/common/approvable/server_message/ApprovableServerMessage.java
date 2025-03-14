package de.uol.swp.common.approvable.server_message;

import de.uol.swp.common.approvable.Approvable;
import de.uol.swp.common.approvable.ApprovableMessageStatus;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.message.server_message.AbstractServerMessage;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.server_message.SendMessageByPlayerServerMessage;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * ServerMessage sent to communicate an {@link Approvable} between client and server.
 * It can be either outbound, approved or rejected (or temporarily rejected).
 * It specifies messages to send by a specified {@link Player} after it is either approved or rejected.
 * <br>
 * It implements {@link SendMessageByPlayerServerMessage} to determine which {@link Message} should be sent after.
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
public class ApprovableServerMessage extends AbstractServerMessage implements SendMessageByPlayerServerMessage {
    private final ApprovableMessageStatus status;
    private final Approvable approvable;
    private final Message onApproved;
    private final Player onApprovedPlayer;
    private final Message onRejected;
    private final Player onRejectedPlayer;

    @Override
    public Game getGame() {
        return approvable.getGame();
    }

    /**
     * Returns
     *  {@link #onApprovedPlayer} if {@link #status} is {@link ApprovableMessageStatus#APPROVED} or
     *  {@link #onRejectedPlayer} if {@link #status} is {@link ApprovableMessageStatus#REJECTED}.
     * Throws an {@link IllegalStateException} otherwise.
     *
     * @return returning {@link Player} if there currently is one
     * @throws IllegalStateException if there is no returning {@link Player} at the moment
     */
    @Override
    public Player getReturningPlayer() throws IllegalStateException {
        return switch (status) {
            case APPROVED -> onApprovedPlayer;
            case REJECTED -> onRejectedPlayer;
            default -> throw new IllegalStateException();
        };
    }

    /**
     * Returns
     *  {@link #onApproved} if {@link #status} is {@link ApprovableMessageStatus#APPROVED} or
     *  {@link #onRejected} if {@link #status} is {@link ApprovableMessageStatus#REJECTED}.
     * Throws an {@link IllegalStateException} otherwise.
     *
     * @return {@link Message} to send if there currently is one
     * @throws IllegalStateException if there is no defined {@link Message} to send at the moment
     */
    @Override
    public Message getMessageToSend() throws IllegalStateException {
        return switch (status) {
            case APPROVED -> onApproved;
            case REJECTED -> onRejected;
            default -> throw new IllegalStateException();
        };
    }
}
