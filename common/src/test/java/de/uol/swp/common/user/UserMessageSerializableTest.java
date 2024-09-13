package de.uol.swp.common.user;

import de.uol.swp.common.SerializationTestHelper;
import de.uol.swp.common.user.response.RegisterUserExceptionResponse;
import de.uol.swp.common.user.server_message.LoginServerMessage;
import de.uol.swp.common.user.server_message.LogoutServerMessage;
import de.uol.swp.common.user.server_message.RetrieveAllOnlineUsersServerMessage;
import de.uol.swp.common.user.request.LoginRequest;
import de.uol.swp.common.user.request.LogoutRequest;
import de.uol.swp.common.user.request.RegisterUserRequest;
import de.uol.swp.common.user.request.RetrieveAllOnlineUsersRequest;
import de.uol.swp.common.user.response.LoginSuccessfulResponse;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UserMessageSerializableTest {

    private static final User defaultUser = new UserDTO("marco", "marco", "marco@grawunder.de");

    private static final int SIZE = 10;
    private static final List<User> users = new ArrayList<>();

    static {
        for (int i = 0; i < SIZE; i++) {
            users.add(defaultUser.getWithoutPassword());
        }
    }

    @Test
    void testUserMessagesSerializable() {
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LoginServerMessage(defaultUser),
                LoginServerMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LogoutServerMessage(defaultUser),
                LogoutServerMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new RetrieveAllOnlineUsersServerMessage(users),
                RetrieveAllOnlineUsersServerMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new RegisterUserExceptionResponse("Error"),
                RegisterUserExceptionResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LoginSuccessfulResponse(defaultUser),
                LoginSuccessfulResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LoginRequest("name", "pass"),
                LoginRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LogoutRequest(), LogoutRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new RegisterUserRequest(defaultUser),
                RegisterUserRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new RetrieveAllOnlineUsersRequest(),
                RetrieveAllOnlineUsersRequest.class));

    }
}
