package de.uol.swp.common.user.response;

import de.uol.swp.common.message.response.AbstractResponseMessage;
import de.uol.swp.common.user.User;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Response message for the RetrieveAllOnlineUsersRequest
 *
 * This message gets sent to the client that sent an RetrieveAllOnlineUsersRequest.
 * It contains a List with User objects of every user currently logged in to the
 * server.
 *
 * @see AbstractResponseMessage
 * @see de.uol.swp.common.user.request.RetrieveAllOnlineUsersRequest
 * @see de.uol.swp.common.user.User
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
public class RetrieveAllOnlineUsersResponse extends AbstractResponseMessage {
    private final ArrayList<User> users = new ArrayList<>();

    /**
     * Constructor
     * <p>
     * This constructor generates a new List of the logged in users from the given
     * Collection. The significant difference between the two being that the new
     * List contains copies of the User objects. These copies have their password
     * variable set to an empty String.
     *
     * @param users Collection of all users currently logged in
     */
    public RetrieveAllOnlineUsersResponse(final Collection<User> users) {
        for (final User user : users) {
            this.users.add(user.getWithoutPassword());
        }
    }
}
