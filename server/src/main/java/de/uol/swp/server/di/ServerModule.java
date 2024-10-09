package de.uol.swp.server.di;

import com.google.inject.AbstractModule;
import de.uol.swp.server.game.GameManagement;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import de.uol.swp.server.usermanagement.store.UserStore;
import org.greenrobot.eventbus.EventBus;

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
    private final GameManagement gameManagement = new GameManagement();

    @Override
    protected void configure() {
        bind(UserStore.class).toInstance(store);
        bind(EventBus.class).toInstance(bus);
        bind(LobbyManagement.class).toInstance(lobbyManagement);
        bind(GameManagement.class).toInstance(gameManagement);
    }
}
