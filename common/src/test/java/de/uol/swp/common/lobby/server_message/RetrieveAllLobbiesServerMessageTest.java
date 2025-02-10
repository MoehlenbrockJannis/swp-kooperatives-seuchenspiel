package de.uol.swp.common.lobby.server_message;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.message.server.AbstractServerMessage;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RetrieveAllLobbiesServerMessage Test")
class RetrieveAllLobbiesServerMessageTest {

    private List<Lobby> testLobbies;

    @BeforeEach
    void setUp() {
        testLobbies = new ArrayList<>();
        UserDTO owner1 = new UserDTO("owner1", "password", "owner1@example.com");
        UserDTO owner2 = new UserDTO("owner2", "password", "owner2@example.com");
        testLobbies.add(new LobbyDTO("Lobby1", owner1));
        testLobbies.add(new LobbyDTO("Lobby2", owner2));
    }

    @Test
    @DisplayName("Constructor should handle empty list")
    void constructorWithEmptyListTest() {
        List<Lobby> emptyList = new ArrayList<>();
        RetrieveAllLobbiesServerMessage message = new RetrieveAllLobbiesServerMessage(emptyList);

        assertNotNull(message.getLobbies());
        assertTrue(message.getLobbies().isEmpty());
    }

    @Test
    @DisplayName("Constructor should handle null list")
    void constructorWithNullListTest() {
        assertDoesNotThrow(() -> new RetrieveAllLobbiesServerMessage(null));

        RetrieveAllLobbiesServerMessage message = new RetrieveAllLobbiesServerMessage(null);

        assertNull(message.getLobbies(), "getLobbies() should return null when constructed with null");
    }

    @Test
    @DisplayName("Lobbies list should be mutable")
    void mutabilityTest() {
        RetrieveAllLobbiesServerMessage message = new RetrieveAllLobbiesServerMessage(testLobbies);

        List<Lobby> lobbies = message.getLobbies();
        int originalSize = lobbies.size();

        lobbies.add(new LobbyDTO("NewLobby", new UserDTO("newOwner", "password", "new@example.com")));

        assertEquals(originalSize + 1, message.getLobbies().size(), "The size of the lobbies list should change");
        assertTrue(message.getLobbies().contains(lobbies.get(lobbies.size() - 1)), "The new lobby should be in the message's list");
    }
}