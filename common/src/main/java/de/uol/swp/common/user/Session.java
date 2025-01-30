package de.uol.swp.common.user;

/**
 * Interface for different kinds of user objects.
 *
 * This interface is for unifying different kinds of user objects throughout the
 * project. With this being the base project it is currently only used for the UUIDSession
 * objects within the server.
 *
 * @author Marco Grawunder
 * @since 2019-08-05
 */
public interface Session {

    /**
     * Getter for the SessionID
     *
     * @return ID of the session as String
     * @since 2019-08-05
     */
    String getSessionId();

}
