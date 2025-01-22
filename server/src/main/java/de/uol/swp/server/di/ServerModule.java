package de.uol.swp.server.di;

import com.google.inject.AbstractModule;
import de.uol.swp.server.card.CardManagement;
import de.uol.swp.server.store.AbstractStore;
import org.greenrobot.eventbus.EventBus;

import java.util.Map;

/**
 * Module that provides classes needed by the Server.
 *
 * @author Marco Grawunder
 * @since 2019-09-18
 *
 */


public class ServerModule extends AbstractModule {

    private final EventBus bus = EventBus.getDefault();
    private final CardManagement cardManagement = new CardManagement();
    @SuppressWarnings("rawtypes")
    private final Map<Class, AbstractStore> stores = AbstractStore.createStores(false);

    @Override
    protected void configure() {
        bind(EventBus.class).toInstance(bus);
        bind(CardManagement.class).toInstance(cardManagement);
        bindStores();
    }

    /**
     * Binds all stores to the corresponding classes.
     */
    private void bindStores() {
        stores.forEach((key, value) -> bind(key).toInstance(value));
    }
}
