package de.uol.swp.common.lobby;

import de.uol.swp.common.SerializationTestHelper;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.lobby.request.CreateLobbyRequest;
import de.uol.swp.common.lobby.request.LobbyJoinUserRequest;
import de.uol.swp.common.lobby.request.LobbyLeaveUserRequest;
import de.uol.swp.common.lobby.server_message.*;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LobbyMessageSerializableTest {

    private static final UserDTO defaultUser = new UserDTO("marco", "marco", "marco@grawunder.de");
    Lobby lobby = new LobbyDTO("TestLobby", defaultUser);

    @Test
    void testLobbyMessagesSerializable() {
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new CreateLobbyRequest(lobby, defaultUser),
                CreateLobbyRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LobbyJoinUserRequest(lobby, defaultUser),
                LobbyJoinUserRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LobbyLeaveUserRequest(lobby, defaultUser),
                LobbyLeaveUserRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LobbyJoinUserServerMessage(lobby, defaultUser),
                LobbyJoinUserServerMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LobbyLeaveUserServerMessage(lobby, defaultUser),
                LobbyLeaveUserServerMessage.class));
    }


}
