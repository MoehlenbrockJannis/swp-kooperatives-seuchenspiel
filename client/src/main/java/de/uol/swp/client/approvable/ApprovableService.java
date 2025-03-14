package de.uol.swp.client.approvable;

import com.google.inject.Inject;
import de.uol.swp.common.action.Action;
import de.uol.swp.common.action.request.ActionRequest;
import de.uol.swp.common.approvable.Approvable;
import de.uol.swp.common.approvable.ApprovableMessageStatus;
import de.uol.swp.common.approvable.request.ApprovableRequest;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.player.Player;
import lombok.RequiredArgsConstructor;
import org.greenrobot.eventbus.EventBus;

/**
 * The {@link ApprovableService} handles sending of {@link ApprovableRequest} with appropriate {@link ApprovableMessageStatus}.
 * It utilizes an {@link EventBus} to post requests with an {@link Approvable}.
 */
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ApprovableService {
    private final EventBus eventBus;

    /**
     * Sends an {@link ApprovableRequest} with status {@link ApprovableMessageStatus#OUTBOUND} and given {@code approvableAction}
     * to the server via the {@link #eventBus}.
     * Represents the state when one {@link Player} requests another to do something.
     *
     * @param approvableAction {@link Approvable} that is also an {@link Action} to send to the server
     * @param <A> type of given {@link Approvable} that is also an {@link Action}
     * @see #sendApprovable(Approvable, ApprovableMessageStatus, Message, Player, Message, Player)
     */
    public <A extends Approvable & Action> void sendApprovableAction(final A approvableAction) {
        final ActionRequest actionRequest = new ActionRequest(approvableAction.getGame(), approvableAction);
        sendApprovable(
                approvableAction,
                actionRequest,
                approvableAction.getExecutingPlayer(),
                null,
                null
        );
    }

    /**
     * Sends an {@link ApprovableRequest} with status {@link ApprovableMessageStatus#OUTBOUND} and given {@code approvable}
     * to the server via the {@link #eventBus}.
     * Represents the state when one {@link Player} requests another to do something.
     *
     * @param approvable the {@link Approvable} for the sent {@link ApprovableRequest}
     * @param onApproved {@link Message} to send if {@link Approvable} is approved
     * @param onApprovedPlayer {@link Player} to send {@code onApproved} if {@link Approvable} is approved
     * @param onRejected {@link Message} to send if {@link Approvable} is rejected
     * @param onRejectedPlayer {@link Player} to send {@code onRejected} if {@link Approvable} is rejected
     * @see #sendApprovable(Approvable, ApprovableMessageStatus, Message, Player, Message, Player)
     */
    public void sendApprovable(final Approvable approvable,
                               final Message onApproved,
                               final Player onApprovedPlayer,
                               final Message onRejected,
                               final Player onRejectedPlayer) {
        sendApprovable(
                approvable,
                ApprovableMessageStatus.OUTBOUND,
                onApproved,
                onApprovedPlayer,
                onRejected,
                onRejectedPlayer
        );
    }

    /**
     * Sends an {@link ApprovableRequest} with status {@link ApprovableMessageStatus#APPROVED} and given {@code approvable}
     * to the server via the {@link #eventBus}.
     * Represents the state when one {@link Player} accepts the {@link Approvable}.
     *
     * @param approvable the {@link Approvable} for the sent {@link ApprovableRequest}
     * @param onApproved {@link Message} to send if {@link Approvable} is approved
     * @param onApprovedPlayer {@link Player} to send {@code onApproved} if {@link Approvable} is approved
     * @param onRejected {@link Message} to send if {@link Approvable} is rejected
     * @param onRejectedPlayer {@link Player} to send {@code onRejected} if {@link Approvable} is rejected
     * @see #sendApprovable(Approvable, ApprovableMessageStatus, Message, Player, Message, Player)
     */
    public void approveApprovable(final Approvable approvable,
                                  final Message onApproved,
                                  final Player onApprovedPlayer,
                                  final Message onRejected,
                                  final Player onRejectedPlayer) {
        approvable.approve();
        sendApprovable(
                approvable,
                ApprovableMessageStatus.APPROVED,
                onApproved,
                onApprovedPlayer,
                onRejected,
                onRejectedPlayer
        );
    }

    /**
     * Sends an {@link ApprovableRequest} with status {@link ApprovableMessageStatus#REJECTED} and given {@code approvable}
     * to the server via the {@link #eventBus}.
     * Represents the state when one {@link Player} rejects the {@link Approvable}.
     *
     * @param approvable the {@link Approvable} for the sent {@link ApprovableRequest}
     * @param onApproved {@link Message} to send if {@link Approvable} is approved
     * @param onApprovedPlayer {@link Player} to send {@code onApproved} if {@link Approvable} is approved
     * @param onRejected {@link Message} to send if {@link Approvable} is rejected
     * @param onRejectedPlayer {@link Player} to send {@code onRejected} if {@link Approvable} is rejected
     * @see #sendApprovable(Approvable, ApprovableMessageStatus, Message, Player, Message, Player)
     */
    public void rejectApprovable(final Approvable approvable,
                                 final Message onApproved,
                                 final Player onApprovedPlayer,
                                 final Message onRejected,
                                 final Player onRejectedPlayer) {
        sendApprovable(
                approvable,
                ApprovableMessageStatus.REJECTED,
                onApproved,
                onApprovedPlayer,
                onRejected,
                onRejectedPlayer
        );
    }

    /**
     * Creates and sends an {@link ApprovableRequest} with {@code approvable} and {@code status}
     * to the server via the {@link #eventBus}.
     *
     * @param approvable the {@link Approvable} of the {@link ApprovableRequest}
     * @param status the {@link ApprovableMessageStatus} of the {@link ApprovableRequest}
     * @param onApproved {@link Message} to send if {@link Approvable} is approved
     * @param onApprovedPlayer {@link Player} to send {@code onApproved} if {@link Approvable} is approved
     * @param onRejected {@link Message} to send if {@link Approvable} is rejected
     * @param onRejectedPlayer {@link Player} to send {@code onRejected} if {@link Approvable} is rejected
     * @see ApprovableRequest
     */
    private void sendApprovable(final Approvable approvable,
                                final ApprovableMessageStatus status,
                                final Message onApproved,
                                final Player onApprovedPlayer,
                                final Message onRejected,
                                final Player onRejectedPlayer) {
        final ApprovableRequest approvableRequest = new ApprovableRequest(
                status,
                approvable,
                onApproved,
                onApprovedPlayer,
                onRejected,
                onRejectedPlayer
        );
        eventBus.post(approvableRequest);
    }
}
