package de.uol.swp.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.greenrobot.eventbus.EventBus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class EventBusBasedTest {

    private static final Logger LOG = LogManager.getLogger(EventBusBasedTest.class);
    private final CountDownLatch lock = new CountDownLatch(1);

    private final EventBus bus = EventBus.builder()
            .logNoSubscriberMessages(false)
            .sendNoSubscriberEvent(false)
            .throwSubscriberException(true)
            .build();

    protected Object event;

    protected void handleEvent(Object e){
        this.event = e;
        lock.countDown();
    }

    @BeforeEach
    void registerBus() {
        event = null;
        try {
            bus.register(this);
        }catch(Exception e){
            LOG.warn("No @Subscribe method found --> events cannot be compared!");
        }
    }

    @AfterEach
    void deregisterBus() {
        bus.unregister(this);
    }

    protected void postAndWait(Object event) throws InterruptedException{
        bus.post(event);
        lock.await(1000, TimeUnit.MILLISECONDS);
    }

    protected void post(Object event){
        bus.post(event);
    }

    protected EventBus getBus() {
        return bus;
    }

    protected void waitForLock() throws InterruptedException {
        lock.await(1000, TimeUnit.MILLISECONDS);
    }
}
