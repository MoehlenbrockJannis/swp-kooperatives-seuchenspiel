package de.uol.swp.client.approvable;

import de.uol.swp.client.EventBusBasedTest;
import de.uol.swp.common.action.request.ActionRequest;
import de.uol.swp.common.action.simple.MoveAllyAction;
import de.uol.swp.common.approvable.Approvable;
import de.uol.swp.common.approvable.ApprovableMessageStatus;
import de.uol.swp.common.approvable.request.ApprovableRequest;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.player.Player;
import org.greenrobot.eventbus.Subscribe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;

public class ApprovableServiceTest extends EventBusBasedTest {
    private ApprovableService approvableService;
    private Approvable approvable;
    private Message onApproved;
    private Player onApprovedPlayer;
    private Message onRejected;
    private Player onRejectedPlayer;

    @BeforeEach
    void setUp() {
        this.approvableService = new ApprovableService(getBus());

        approvable = mock();
        onApproved = mock();
        onApprovedPlayer = mock();
        onRejected = mock();
        onRejectedPlayer = mock();
    }

    @Test
    @DisplayName("Should send an Approvable Action via a request the EventBus")
    void sendApprovableAction() throws InterruptedException {
        final MoveAllyAction moveAllyAction = mock();
        when(moveAllyAction.getExecutingPlayer())
                .thenReturn(onApprovedPlayer);

        approvableService.sendApprovableAction(moveAllyAction);

        testAssertions(moveAllyAction, ApprovableMessageStatus.OUTBOUND, new ActionRequest(moveAllyAction.getGame(), moveAllyAction), onApprovedPlayer, null, null);
    }

    @Test
    @DisplayName("Should send an Approvable via a request the EventBus")
    void sendApprovable() throws InterruptedException {
        approvableService.sendApprovable(approvable, onApproved, onApprovedPlayer, onRejected, onRejectedPlayer);

        testAssertions(approvable, ApprovableMessageStatus.OUTBOUND, onApproved, onApprovedPlayer, onRejected, onRejectedPlayer);
    }

    @Test
    @DisplayName("Should approve an Approvable via a request the EventBus")
    void approveApprovable() throws InterruptedException {
        approvableService.approveApprovable(approvable, onApproved, onApprovedPlayer, onRejected, onRejectedPlayer);

        testAssertions(approvable, ApprovableMessageStatus.APPROVED, onApproved, onApprovedPlayer, onRejected, onRejectedPlayer);

        verify(approvable)
                .approve();
    }

    @Test
    @DisplayName("Should reject an Approvable via a request the EventBus")
    void rejectApprovable() throws InterruptedException {
        approvableService.rejectApprovable(approvable, onApproved, onApprovedPlayer, onRejected, onRejectedPlayer);

        testAssertions(approvable, ApprovableMessageStatus.REJECTED, onApproved, onApprovedPlayer, onRejected, onRejectedPlayer);
    }

    private void testAssertions(final Approvable approvable,
                                final ApprovableMessageStatus status,
                                final Message onApproved,
                                final Player onApprovedPlayer,
                                final Message onRejected,
                                final Player onRejectedPlayer) throws InterruptedException {
        final ApprovableRequest expected = new ApprovableRequest(
                status,
                approvable,
                onApproved,
                onApprovedPlayer,
                onRejected,
                onRejectedPlayer
        );

        waitForLock();

        assertInstanceOf(ApprovableRequest.class, event);

        final ApprovableRequest approvableRequest = (ApprovableRequest) event;
        assertThat(approvableRequest)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Subscribe
    public void onEvent(final ApprovableRequest approvableRequest) {
        handleEvent(approvableRequest);
    }
}