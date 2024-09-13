package de.uol.swp.common.user.response;

import de.uol.swp.common.user.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for the all online user response
 *
 * @see RetrieveAllOnlineUsersResponse
 * @since 2023-05-14
 */
public class RetrieveAllOnlineUsersResponseTest {

    final ArrayList<User> users = new ArrayList<>();

    /**
     * Test for creation of the AllOnlineUserResponses
     *
     * This test checks if the user list of the AllOnlineUserResponse gets
     * set correctly during the creation of the response
     *
     * @since 2023-05-14
     */
    @Test
    void createAllOnlineUsersResponse() {
        RetrieveAllOnlineUsersResponse response = new RetrieveAllOnlineUsersResponse(users);

        assertEquals(users, response.getUsers());
    }

}
