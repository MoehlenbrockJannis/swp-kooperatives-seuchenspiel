package de.uol.swp.server.di;

import de.uol.swp.server.lobby.LobbyManagement;
import org.greenrobot.eventbus.EventBus;
import com.google.inject.AbstractModule;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import de.uol.swp.server.usermanagement.store.UserStore;

/**
 * Module that provides classes needed by the Server.
 *
 * @author Marco Grawunder
 * @since 2019-09-18
 *
 */


public class ServerModule extends AbstractModule {

    private final EventBus bus = EventBus.getDefault();
    private final UserStore store = new MainMemoryBasedUserStore();
    private final LobbyManagement lobbyManagement = new LobbyManagement();

    @Override
    protected void configure() {
        bind(UserStore.class).toInstance(store);
        bind(EventBus.class).toInstance(bus);
        bind(LobbyManagement.class).toInstance(lobbyManagement);
    }
}
