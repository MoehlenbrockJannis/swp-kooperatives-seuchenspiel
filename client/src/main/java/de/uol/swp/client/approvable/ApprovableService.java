package de.uol.swp.client.approvable;

import com.google.inject.Inject;
import de.uol.swp.common.approvable.Approvable;
import de.uol.swp.common.approvable.ApprovableMessageStatus;
import de.uol.swp.common.approvable.request.ApprovableRequest;
import de.uol.swp.common.player.Player;
import lombok.RequiredArgsConstructor;
import org.greenrobot.eventbus.EventBus;

/**
 * The {@link ApprovableService} handles sending of {@link ApprovableRequest} with appropriate {@link ApprovableMessageStatus}.
 * It utilizes an {@link EventBus} to post requests with an {@link Approvable}.
 *
 * @author Tom Weelborg
 */
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ApprovableService {
    private final EventBus eventBus;

    /**
     * Sends an {@link ApprovableRequest} with status {@link ApprovableMessageStatus#OUTBOUND} and given {@code approvable}
     * to the server via the {@link #eventBus}.
     * Represents the state when one {@link Player} requests another to do something.
     *
     * @param approvable the {@link Approvable} for the sent {@link ApprovableRequest}
     * @see #sendApprovable(Approvable, ApprovableMessageStatus)
     */
    public void sendApprovable(final Approvable approvable) {
        sendApprovable(approvable, ApprovableMessageStatus.OUTBOUND);
    }

    /**
     * Sends an {@link ApprovableRequest} with status {@link ApprovableMessageStatus#APPROVED} and given {@code approvable}
     * to the server via the {@link #eventBus}.
     * Represents the state when one {@link Player} accepts the {@link Approvable}.
     *
     * @param approvable the {@link Approvable} for the sent {@link ApprovableRequest}
     * @see #sendApprovable(Approvable, ApprovableMessageStatus)
     */
    public void approveApprovable(final Approvable approvable) {
        approvable.approve();
        sendApprovable(approvable, ApprovableMessageStatus.APPROVED);
    }

    /**
     * Sends an {@link ApprovableRequest} with status {@link ApprovableMessageStatus#REJECTED} and given {@code approvable}
     * to the server via the {@link #eventBus}.
     * Represents the state when one {@link Player} rejects the {@link Approvable}.
     *
     * @param approvable the {@link Approvable} for the sent {@link ApprovableRequest}
     * @see #sendApprovable(Approvable, ApprovableMessageStatus)
     */
    public void rejectApprovable(final Approvable approvable) {
        sendApprovable(approvable, ApprovableMessageStatus.REJECTED);
    }

    /**
     * Creates and sends an {@link ApprovableRequest} with {@code approvable} and {@code status}
     * to the server via the {@link #eventBus}.
     *
     * @param approvable the {@link Approvable} of the {@link ApprovableRequest}
     * @param status the {@link ApprovableMessageStatus} of the {@link ApprovableRequest}
     * @see ApprovableRequest
     */
    private void sendApprovable(final Approvable approvable, final ApprovableMessageStatus status) {
        final ApprovableRequest approvableRequest = new ApprovableRequest(status, approvable);
        eventBus.post(approvableRequest);
    }
}
