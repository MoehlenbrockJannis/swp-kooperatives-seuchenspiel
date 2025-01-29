package de.uol.swp.server.usermanagement.store;

import de.uol.swp.common.user.User;
import de.uol.swp.server.store.AbstractStore;
import de.uol.swp.server.store.DatabaseStore;

import java.util.List;
import java.util.Optional;

public class DatabaseBasedUserStore extends AbstractStore implements UserStore, DatabaseStore {


    @Override
    public Optional<User> findUser(String username, String password) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findUser(String username) {
        return Optional.empty();
    }

    @Override
    public User createUser(String username, String password, String eMail) {
        return null;
    }

    @Override
    public User updateUser(String username, String password, String eMail) {
        return null;
    }

    @Override
    public void removeUser(String username) {

    }

    @Override
    public List<User> getAllUsers() {
        return List.of();
    }
}
