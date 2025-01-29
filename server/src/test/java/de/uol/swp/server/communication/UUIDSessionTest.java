package de.uol.swp.server.communication;

import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.communication.UUIDSession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SuppressWarnings("UnstableApiUsage")
public class UUIDSessionTest {

    final User user = new UserDTO("name", "password", "email@test.de");

    @Test
    void createSessionTest() {
        // Creates the session of the user
        Session session = UUIDSession.create(user);

        assertNotNull(session);
        assertNotNull(session.getSessionId());
    }

    @Test
    void getSessionUserTest() {
        Session session = UUIDSession.create(user);
    }

}
