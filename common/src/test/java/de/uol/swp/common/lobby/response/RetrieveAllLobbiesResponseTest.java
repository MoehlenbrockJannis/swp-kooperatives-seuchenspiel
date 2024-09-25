package de.uol.swp.common.lobby.response;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.lobby.LobbyStatus;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RetrieveAllLobbiesResponse Test")
class RetrieveAllLobbiesResponseTest {

    private List<Lobby> testLobbies;
    private User owner1;
    private User owner2;
    private User member1;
    private User member2;

    @BeforeEach
    void setUp() {
        owner1 = new UserDTO("owner1", "password1", "owner1@example.com");
        owner2 = new UserDTO("owner2", "password2", "owner2@example.com");
        member1 = new UserDTO("member1", "password3", "member1@example.com");
        member2 = new UserDTO("member2", "password4", "member2@example.com");

        testLobbies = new ArrayList<>();

        Lobby lobby1 = new LobbyDTO("Lobby1", owner1, 2, 4);
        lobby1.setStatus(LobbyStatus.OPEN);
        lobby1.joinUser(member1);

        Lobby lobby2 = new LobbyDTO("Lobby2", owner2, 3, 6);
        lobby2.setStatus(LobbyStatus.RUNNING);
        lobby2.joinUser(member2);

        testLobbies.add(lobby1);
        testLobbies.add(lobby2);
    }

    @Test
    @DisplayName("User passwords should be hidden in response")
    void passwordsAreHiddenTest() {
        RetrieveAllLobbiesResponse response = new RetrieveAllLobbiesResponse(testLobbies);

        for (Lobby lobby : response.getLobbies()) {
            for (User user : lobby.getUsers()) {
                assertTrue(user.getPassword().isEmpty());
            }
            assertTrue(lobby.getOwner().getPassword().isEmpty());
        }
    }

    @Test
    @DisplayName("Response should handle empty lobbies list")
    void emptyLobbiesListTest() {
        RetrieveAllLobbiesResponse response = new RetrieveAllLobbiesResponse(new ArrayList<>());
        assertTrue(response.getLobbies().isEmpty());
    }

    @Test
    @DisplayName("Response should handle lobby with no members")
    void lobbyWithNoMembersTest() {
        Lobby emptyLobby = new LobbyDTO("EmptyLobby", owner1, 2, 4);
        List<Lobby> lobbies = List.of(emptyLobby);

        RetrieveAllLobbiesResponse response = new RetrieveAllLobbiesResponse(lobbies);

        assertEquals(1, response.getLobbies().size());
        assertEquals(1, response.getLobbies().get(0).getUsers().size());
        assertEquals(owner1.getUsername(), response.getLobbies().get(0).getOwner().getUsername());
    }

    @Test
    @DisplayName("Constructor should throw NullPointerException for null lobbies list")
    void nullLobbiesListTest() {
        assertThrows(NullPointerException.class, () -> new RetrieveAllLobbiesResponse(null));
    }
}