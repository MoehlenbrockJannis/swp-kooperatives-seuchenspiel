package de.uol.swp.common.user.response;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginSuccessfulResponseTest {

    final User user = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");


    @Test
    void createLoginSuccessfulResponse() {
        LoginSuccessfulResponse response = new LoginSuccessfulResponse(user);

        assertEquals(user, response.getUser());
    }

}
