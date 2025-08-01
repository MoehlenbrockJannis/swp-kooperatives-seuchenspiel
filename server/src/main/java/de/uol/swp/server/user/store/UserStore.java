package de.uol.swp.server.user.store;

import de.uol.swp.common.user.User;
import de.uol.swp.server.store.ContentStore;

import java.util.List;
import java.util.Optional;

/**
 * Interface to unify different kinds of UserStores in order to able to exchange
 * them easily.
 */
public interface UserStore extends ContentStore {

    /**
     * Find a user by username and password
     *
     * @param username username of the user to find
     * @param password password of the user to find
     * @return The User without password information, if found
     */
    Optional<User> findUser(String username, String password);

    /**
     * Find a user only by name
     *
     * @param username username of the user to find
     * @return The User without password information, if found
     */
    Optional<User> findUser(String username);

    /**
     * Create a new user
     *
     * @param username username of the new user
     * @param password password the user wants to use
     * @param eMail email address of the new user
     * @return The User without password information
     */
    User createUser(String username, String password, String eMail);

    /**
     * Update user. Update only given fields. Username cannot be changed
     *
     * @param username username of the user to be modified
     * @param password new password
     * @param eMail new email address
     * @return The User without password information
     */
    User updateUser(String username, String password, String eMail);

    /**
     * Remove user from store
     *
     * @param username the username of the user to remove
     */
    void removeUser(String username);


    /**
     * Retrieves the list of all users.
     * @return A list of all users without password information
     */
    List<User> getAllUsers();


}
