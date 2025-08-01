package de.uol.swp.server.user.store;

import com.google.common.base.Strings;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.store.AbstractStore;
import de.uol.swp.server.store.MainMemoryBasedStore;

import java.util.*;

/**
 * This is a user store.
 * <p>
 * This is the user store that is used for the start of the software project. The
 * user accounts in this user store only reside within the RAM of your computer
 * and only for as long as the server is running. Therefore the users have to be
 * added every time the server is started.
 *
 * @implNote This store will never return the password of a user!
 * @see de.uol.swp.server.store.AbstractStore
 * @see de.uol.swp.server.user.store.UserStore
 */
public class MainMemoryBasedUserStore extends AbstractStore implements UserStore, MainMemoryBasedStore {

    private final Map<String, User> users = new HashMap<>();

    @Override
    public Optional<User> findUser(String username, String password) {
        User usr = users.get(username);
        if (usr != null && Objects.equals(usr.getPassword(),password)) {
            return Optional.of(usr.getWithoutPassword());
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findUser(String username) {
        User usr = users.get(username);
        if (usr != null) {
            return Optional.of(usr.getWithoutPassword());
        }
        return Optional.empty();
    }

    @Override
    public User createUser(String username, String password, String eMail) {
        if (Strings.isNullOrEmpty(username)){
            throw new IllegalArgumentException("Username must not be null");
        }
        User usr = new UserDTO(username, password, eMail);
        users.put(username, usr);
        return usr;
    }

    @Override
    public User updateUser(String username, String password, String eMail) {
        return createUser(username, password, eMail);
    }

    @Override
    public void removeUser(String username) {
        users.remove(username);
    }

    @Override
    public List<User> getAllUsers() {
        List<User> retUsers = new ArrayList<>();
        users.values().forEach(u -> retUsers.add(u.getWithoutPassword()));
        return retUsers;
    }

    @Override
    protected Set<Integer> getIds() {
        return Collections.emptySet();
    }
}
