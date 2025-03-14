package de.uol.swp.client.user;

import de.uol.swp.common.user.User;

/**
 * An interface for all methods of the client user service
 *
 * As the communication with the server is based on events, the
 * returns of the call must be handled by events
 */

public interface ClientUserService {

    /**
     * Login with username and password
     *
     * @param username the name of the user
     * @param password the password of the user
     */
    void login(String username, String password);

    /**
     * Log out from server
     *
     * @implNote the User Object has to contain a unique identifier in order to
     * 			 remove the correct user
     */
    void logout(User user);

    /**
     * Create a new persistent user
     *
     * @implNote the User Object has to contain a unique identifier in order to
     * 			 remove the correct user
     * @param user The user to create
     */
    void createUser(User user);

    /**
     * Update a user
     * <p>
     * Updates the User specified by the User object.
     *
     * @implNote the User Object has to contain a unique identifier in order to
     * 			 update the correct user
     * @param user the user object containing all infos to
     *             update, if some values are not set, (e.g. password is "")
     *             these fields are not updated
     */
    void updateUser(User user);

    /**
     * Retrieve the list of all current logged in users
     */
    void retrieveAllUsers();

}
