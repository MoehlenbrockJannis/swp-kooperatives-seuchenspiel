package de.uol.swp.server.user;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import de.uol.swp.common.user.User;
import de.uol.swp.common.util.HashUtil;
import de.uol.swp.server.user.store.UserStore;

import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Handles most user related issues e.g. login/logout
 *
 * @see de.uol.swp.server.user.AbstractUserManagement
 * @author Marco Grawunder
 * @since 2019-08-05
 */
public class UserManagement extends AbstractUserManagement {

    private final UserStore userStore;
    private final SortedMap<String, User> loggedInUsers = new TreeMap<>();

    /**
     * Constructor
     *
     * @param userStore object of the UserStore to be used
     * @see de.uol.swp.server.user.store.UserStore
     * @since 2019-08-05
     */
    @Inject
    public UserManagement(UserStore userStore){
        this.userStore = userStore;
    }

    @Override
    public User login(String username, String password) {
        Optional<User> user = userStore.findUser(username, HashUtil.hash(password));
        if (user.isPresent()){
            this.loggedInUsers.put(username, user.get());
            return user.get();
        }else{
            throw new SecurityException("Der Nutzer: " + username + " kann nicht authentifiziert werden");
        }
    }

    @Override
    public boolean isLoggedIn(User username) {
        return loggedInUsers.containsKey(username.getUsername());
    }

    @Override
    public User createUser(User userToCreate){
        Optional<User> user = userStore.findUser(userToCreate.getUsername());
        if (user.isPresent()){
            throw new UserManagementException("Nutzername existiert bereits!");
        }
        return userStore.createUser(userToCreate.getUsername(), HashUtil.hash(userToCreate.getPassword()), userToCreate.getEMail());
    }

    @Override
    public User updateUser(User userToUpdate){
        Optional<User> user = userStore.findUser(userToUpdate.getUsername());
        if (user.isEmpty()){
            throw new UserManagementException("Nutzername unbekannt!");
        }
        String newPassword = firstNotNull(userToUpdate.getPassword(), user.get().getPassword());
        String newEMail = firstNotNull(userToUpdate.getEMail(), user.get().getEMail());
        return userStore.updateUser(userToUpdate.getUsername(), HashUtil.hash(newPassword), newEMail);

    }

    @Override
    public void dropUser(User userToDrop) {
        Optional<User> user = userStore.findUser(userToDrop.getUsername());
        if (user.isEmpty()) {
            throw new UserManagementException("Nutzername unbekannt!");
        }
        userStore.removeUser(userToDrop.getUsername());

    }

    /**
     * Sub-function of update user
     * <p>
     * This method is used to set the new user values to the old ones if the values
     * in the update request were empty.
     *
     * @param firstValue value to update to, empty String or null
     * @param secondValue the old value
     * @return String containing the value to be used in the update command
     * @since 2019-08-05
     */
    private String firstNotNull(String firstValue, String secondValue) {
        return Strings.isNullOrEmpty(firstValue)?secondValue:firstValue;
    }

    @Override
    public void logout(User user) {
        loggedInUsers.remove(user.getUsername());
    }

    @Override
    public List<User> retrieveAllUsers() {
        return userStore.getAllUsers();
    }
}
