package de.uol.swp.server.di;

import com.google.inject.AbstractModule;
import de.uol.swp.common.env.DotEnvReader;
import de.uol.swp.common.env.EnvReader;
import de.uol.swp.server.card.CardManagement;
import de.uol.swp.server.database.DataSourceConfig;
import de.uol.swp.server.store.AbstractStore;
import de.uol.swp.server.util.ServerAvailabilityChecker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger LOG = LogManager.getLogger(ServerModule.class);

    public static final String TIMEOUT_MS = "TIMEOUT_MS";

    private final EventBus bus = EventBus.getDefault();
    private final EnvReader envReader = new DotEnvReader();

    private final CardManagement cardManagement = new CardManagement();

    @Override
    protected void configure() {

        bind(EventBus.class).toInstance(bus);
        bind(EnvReader.class).toInstance(envReader);
        bind(CardManagement.class).toInstance(cardManagement);
        bindStores();
    }

    /**
     * Binds all stores to the corresponding classes.
     */
    private void bindStores() {
        boolean isDatabaseAvailable = isDatabaseAvailable();
        LOG.info("Database connection: {}", isDatabaseAvailable);

        @SuppressWarnings("rawtypes")
        final Map<Class, AbstractStore> stores = AbstractStore.createStores(isDatabaseAvailable);
        stores.forEach((key, value) -> bind(key).toInstance(value));
    }

    private boolean isDatabaseAvailable() {
        final DataSourceConfig dataSourceConfig = new DataSourceConfig(envReader);
        String dbHost = dataSourceConfig.getDbHost();
        int dbPort = dataSourceConfig.getDbPort();
        int timeoutMs = envReader.readInt(TIMEOUT_MS);
        return ServerAvailabilityChecker.isServerAvailable(dbHost, dbPort, timeoutMs);
    }
}
