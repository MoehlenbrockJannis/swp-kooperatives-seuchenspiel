package de.uol.swp.client.triggerable;

import com.google.inject.Inject;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.triggerable.Triggerable;
import de.uol.swp.common.triggerable.request.TriggerableRequest;
import lombok.RequiredArgsConstructor;
import org.greenrobot.eventbus.EventBus;

/**
 * The {@link TriggerableService} handles sending of {@link TriggerableRequest}.
 * It utilizes an {@link EventBus} to post requests with a {@link Triggerable}.
 */
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class TriggerableService {
    private final EventBus eventBus;

    /**
     * Sends a given {@link Triggerable} to the server via a {@link TriggerableRequest} posted to the {@link #eventBus}.
     *
     * @param triggerable {@link Triggerable} to send to the server
     * @param cause {@link Message} that caused the given {@link Triggerable}
     * @param returningPlayer {@link Player} sending the {@code cause} {@link Message}
     */
    public void sendManualTriggerable(final Triggerable triggerable, final Message cause, final Player returningPlayer) {
        final TriggerableRequest triggerableRequest = new TriggerableRequest(
                triggerable,
                cause,
                returningPlayer
        );
        eventBus.post(triggerableRequest);
    }
}
