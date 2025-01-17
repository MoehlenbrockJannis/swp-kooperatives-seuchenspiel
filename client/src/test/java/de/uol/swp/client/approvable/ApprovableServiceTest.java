package de.uol.swp.client.approvable;

import de.uol.swp.client.EventBusBasedTest;
import de.uol.swp.common.approvable.Approvable;
import de.uol.swp.common.approvable.ApprovableMessageStatus;
import de.uol.swp.common.approvable.request.ApprovableRequest;
import org.greenrobot.eventbus.Subscribe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ApprovableServiceTest extends EventBusBasedTest {
    private ApprovableService approvableService;
    private Approvable approvable;

    @BeforeEach
    void setUp() {
        this.approvableService = new ApprovableService(getBus());

        approvable = mock();
    }

    @Test
    void sendApprovable() throws InterruptedException {
        approvableService.sendApprovable(approvable);

        testAssertions(ApprovableMessageStatus.OUTBOUND);
    }

    @Test
    void approveApprovable() throws InterruptedException {
        approvableService.approveApprovable(approvable);

        testAssertions(ApprovableMessageStatus.APPROVED);

        verify(approvable)
                .approve();
    }

    @Test
    void rejectApprovable() throws InterruptedException {
        approvableService.rejectApprovable(approvable);

        testAssertions(ApprovableMessageStatus.REJECTED);
    }

    private void testAssertions(final ApprovableMessageStatus status) throws InterruptedException {
        waitForLock();

        assertInstanceOf(ApprovableRequest.class, event);

        final ApprovableRequest approvableRequest = (ApprovableRequest) event;
        assertThat(approvableRequest.getStatus())
                .isEqualTo(status);
        assertThat(approvableRequest.getApprovable())
                .isEqualTo(approvable);
    }

    @Subscribe
    public void onEvent(final ApprovableRequest approvableRequest) {
        handleEvent(approvableRequest);
    }
}