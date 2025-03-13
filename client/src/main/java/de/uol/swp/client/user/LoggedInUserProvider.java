package de.uol.swp.client.user;

import com.google.inject.Provider;
import de.uol.swp.client.ClientApp;
import de.uol.swp.common.user.User;
import lombok.RequiredArgsConstructor;

/**
 * Provides the currently logged in user stored on {@link ClientApp}
 *
 * @see ClientApp
 * @see User
 */
@RequiredArgsConstructor
public class LoggedInUserProvider implements Provider<User> {
    private final ClientApp clientApp;

    @Override
    public User get() {
        return clientApp.getUser();
    }
}
