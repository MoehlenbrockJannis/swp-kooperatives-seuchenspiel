package de.uol.swp.common.user.response;

import de.uol.swp.common.user.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RetrieveAllOnlineUsersResponseTest {

    final ArrayList<User> users = new ArrayList<>();

    @Test
    void createAllOnlineUsersResponse() {
        RetrieveAllOnlineUsersResponse response = new RetrieveAllOnlineUsersResponse(users);

        assertEquals(users, response.getUsers());
    }

}
