package de.uol.swp.common.user.request;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegisterUserRequestTest {

    final User user = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");

    @Test
    void createRegisterUserRequest() {
        RegisterUserRequest request = new RegisterUserRequest(user);

        assertEquals(user, request.getUser());
    }

}
