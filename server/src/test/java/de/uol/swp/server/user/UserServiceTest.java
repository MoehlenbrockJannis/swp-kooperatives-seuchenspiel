package de.uol.swp.server.user;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.request.RegisterUserRequest;
import de.uol.swp.server.EventBusBasedTest;
import de.uol.swp.server.user.store.MainMemoryBasedUserStore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest extends EventBusBasedTest {

    static final User userToRegister = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");
    static final User userWithSameName = new UserDTO("Marco", "Marco2", "Marco2@Grawunder.com");

    final UserManagement userManagement = new UserManagement(new MainMemoryBasedUserStore());
    final UserService userService = new UserService(getBus(), userManagement);

    @Test
    void registerUserTest() {
        final RegisterUserRequest request = new RegisterUserRequest(userToRegister);

        post(request);

        final User loggedInUser = userManagement.login(userToRegister.getUsername(), userToRegister.getPassword());

        assertNotNull(loggedInUser);
        assertEquals(userToRegister, loggedInUser);
    }

    @Test
    void registerSecondUserWithSameName() {
        final RegisterUserRequest request = new RegisterUserRequest(userToRegister);
        final RegisterUserRequest request2 = new RegisterUserRequest(userWithSameName);

        post(request);
        post(request2);

        final User loggedInUser = userManagement.login(userToRegister.getUsername(), userToRegister.getPassword());

        assertNotNull(loggedInUser);
        assertEquals(userToRegister, loggedInUser);

        assertNotEquals(loggedInUser.getEMail(), userWithSameName.getEMail());

    }

}