package de.uol.swp.client.di;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import de.uol.swp.client.ClientApp;
import de.uol.swp.client.SceneManager;
import de.uol.swp.client.SceneManagerFactory;
import de.uol.swp.client.communication.ClientConnection;
import de.uol.swp.client.communication.ClientConnectionFactory;
import de.uol.swp.client.user.ClientUserService;
import de.uol.swp.client.user.LoggedInUserProvider;
import de.uol.swp.client.user.UserService;
import de.uol.swp.common.env.DotEnvReader;
import de.uol.swp.common.env.EnvReader;
import javafx.fxml.FXMLLoader;
import lombok.RequiredArgsConstructor;
import org.greenrobot.eventbus.EventBus;

/**
 * Module that provides classes needed by the client.
 */

@RequiredArgsConstructor
public class ClientModule extends AbstractModule {
    private final EventBus eventBus = EventBus.getDefault();
    private final EnvReader envReader = new DotEnvReader();
    private final ClientApp clientApp;

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().implement(SceneManager.class, SceneManager.class).
                build(SceneManagerFactory.class));
        install(new FactoryModuleBuilder().implement(ClientConnection.class, ClientConnection.class).
                build(ClientConnectionFactory.class));
        bind(FXMLLoader.class).toProvider(FXMLLoaderProvider.class);
        bind(EventBus.class).toInstance(eventBus);
        bind(EnvReader.class).toInstance(envReader);
        bind(ClientUserService.class).to(UserService.class);
        bind(LoggedInUserProvider.class).toInstance(new LoggedInUserProvider(clientApp));
    }
}
