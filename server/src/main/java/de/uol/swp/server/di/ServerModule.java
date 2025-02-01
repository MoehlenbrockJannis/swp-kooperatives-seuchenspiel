package de.uol.swp.server.di;

import com.google.inject.AbstractModule;
import de.uol.swp.server.ServerAvailabilityChecker;
import de.uol.swp.server.card.CardManagement;
import de.uol.swp.server.store.AbstractStore;
import io.github.cdimascio.dotenv.Dotenv;
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

    private static final Dotenv dotenv = Dotenv.configure().load();

    private final EventBus bus = EventBus.getDefault();
    private final CardManagement cardManagement = new CardManagement();

    String dbHost = dotenv.get("DB_HOST");
    int dbPort = Integer.parseInt(dotenv.get("DB_PORT"));
    int timeoutMs = Integer.parseInt(dotenv.get("TIMEOUT_MS"));
    @SuppressWarnings("rawtypes")
    private final Map<Class, AbstractStore> stores = AbstractStore.createStores(ServerAvailabilityChecker.isServerAvailable(dbHost, dbPort, timeoutMs));




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
