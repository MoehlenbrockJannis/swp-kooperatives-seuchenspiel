package de.uol.swp.common.lobby;

import de.uol.swp.common.SerializationTestHelper;
import de.uol.swp.common.lobby.request.CreateUserLobbyRequest;
import de.uol.swp.common.lobby.request.JoinUserLobbyRequest;
import de.uol.swp.common.lobby.request.LeavePlayerLobbyRequest;
import de.uol.swp.common.lobby.server_message.JoinUserLobbyServerMessage;
import de.uol.swp.common.lobby.server_message.LeavePlayerLobbyServerMessage;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.UserPlayer;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Lobby Message Serialization Test")
class LobbyMessageSerializableTest {

    private UserDTO defaultUser;
    private Player defaultPlayer;
    Lobby lobby;

    @BeforeEach
    void setUp() {
        this.defaultUser = new UserDTO("marco", "marco", "marco@grawunder.de");
        this.defaultPlayer = new UserPlayer(defaultUser);
        this.lobby = new LobbyDTO("TestLobby", defaultUser);
    }

    @Test
    @DisplayName("All lobby messages should be serializable and deserializable")
    void testLobbyMessagesSerializable() {
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new CreateUserLobbyRequest(lobby, defaultUser),
                CreateUserLobbyRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new JoinUserLobbyRequest(lobby, defaultUser),
                JoinUserLobbyRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LeavePlayerLobbyRequest(lobby, defaultPlayer),
                LeavePlayerLobbyRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new JoinUserLobbyServerMessage(lobby, defaultUser),
                JoinUserLobbyServerMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LeavePlayerLobbyServerMessage(lobby, defaultPlayer),
                LeavePlayerLobbyServerMessage.class));
    }
}