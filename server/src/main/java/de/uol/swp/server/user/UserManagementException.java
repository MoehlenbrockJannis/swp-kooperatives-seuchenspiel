package de.uol.swp.server.user;

/**
 * Exception thrown in UserManagement
 * <p>
 * This exception is thrown if someone wants to register a with a username that
 * is already taken or someone tries to modify or remove a user that does not (yet)
 * exist within the UserStore.
 *
 * @see de.uol.swp.server.user.UserManagement
 */
class UserManagementException extends RuntimeException {

    /**
     * Constructor
     *
     * @param s String containing the cause for the exception.
     */
    UserManagementException(String s) {
        super(s);
    }
}
