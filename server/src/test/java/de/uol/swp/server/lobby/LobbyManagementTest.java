package de.uol.swp.server.lobby;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.lobby.LobbyStatus;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.lobby.store.LobbyStore;
import de.uol.swp.server.lobby.store.MainMemoryBasedLobbyStore;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.UserManagement;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import org.greenrobot.eventbus.EventBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("UnstableApiUsage")
@DisplayName("LobbyManagement Test")
public class LobbyManagementTest {

    static final UserDTO firstOwner = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");
    static final LobbyDTO lobby = new LobbyDTO("TestLobby", firstOwner, 2, 4);

    final EventBus bus = EventBus.getDefault();
    final UserManagement userManagement = new UserManagement(new MainMemoryBasedUserStore());
    final AuthenticationService authService = new AuthenticationService(bus, userManagement);
    LobbyManagement lobbyManagement;
    private LobbyStore lobbyStore;

    @BeforeEach
    void setUp() {
        this.lobbyStore = mock(MainMemoryBasedLobbyStore.class);
        lobbyManagement = new LobbyManagement(lobbyStore);
    }

    LobbyManagement getDefaultManagement() {
        LobbyManagement management = new LobbyManagement(lobbyStore);
        management.createLobby(lobby);
        return management;
    }

    @Test
    @DisplayName("Create lobby")
    void createLobbyTest() {
        lobbyManagement.createLobby(lobby);

        assertNotNull(lobbyManagement.getLobby(lobby));
        if(lobbyManagement.getLobby(lobby).isPresent()){
            assertEquals(lobbyManagement.getLobby(lobby).get().getOwner(), firstOwner);
        }
    }

    @Test
    @DisplayName("Drop lobby")
    void dropLobbyTest() {
        LobbyManagement lobbyManagement = getDefaultManagement();

        lobbyManagement.dropLobby(lobby);

        assertTrue(lobbyManagement.getLobby(lobby).isEmpty());
    }

    @Test
    @DisplayName("Get lobby")
    void getLobbyTest() {
        LobbyManagement lobbyManagement = getDefaultManagement();

        if(lobbyManagement.getLobby(lobby).isPresent()){
            assertNotNull(lobbyManagement.getLobby(lobby));
            assertEquals(lobbyManagement.getLobby(lobby).get().getOwner(), firstOwner);
        }
    }

    @Test
    @DisplayName("Get all lobbies")
    void getAllLobbiesTest() {
        final List<Lobby> lobbies = List.of(
                new LobbyDTO("1", new UserDTO("Test", "", ""), 2, 4),
                new LobbyDTO("2", new UserDTO("Tom", "", ""), 2, 4)
        );

        lobbies.forEach(lobbyManagement::createLobby);

        when(lobbyManagement.getAllLobbies()).thenReturn(lobbies);

        final List<Lobby> lobbiesInLobbyManagement = lobbyManagement.getAllLobbies();



        assertEquals(lobbies.size(), lobbiesInLobbyManagement.size());
        for (int i = 0; i < lobbiesInLobbyManagement.size(); i++) {
            assertEquals(lobbies.get(i).getName(), lobbiesInLobbyManagement.get(i).getName());
            assertEquals(lobbies.get(i).getOwner(), lobbiesInLobbyManagement.get(i).getOwner());
        }
    }

    @Test
    @DisplayName("Update lobby")
    void updateLobbyTest() {
        lobbyManagement.createLobby(lobby);
        LobbyDTO updatedLobby = new LobbyDTO("TestLobby", firstOwner, 3, 5);

        lobbyManagement.updateLobby(updatedLobby);

        when(lobbyStore.getLobby(lobby.getName())).thenReturn(Optional.of(updatedLobby));

        Optional<Lobby> result = lobbyManagement.getLobby(lobby);
        assertTrue(result.isPresent());
        assertEquals(3, result.get().getMinPlayers());
        assertEquals(5, result.get().getMaxPlayers());
    }

    @Test
    @DisplayName("Update lobby status")
    void updateLobbyStatusTest() {
        lobbyManagement.createLobby(lobby);

        when(lobbyStore.getLobby(lobby.getName())).thenReturn(Optional.of(lobby));

        lobbyManagement.updateLobbyStatus(lobby, LobbyStatus.RUNNING);

        Optional<Lobby> result = lobbyManagement.getLobby(lobby);
        assertTrue(result.isPresent());
        assertEquals(LobbyStatus.RUNNING, result.get().getStatus());
    }

    @Test
    @DisplayName("Drop non-existent lobby")
    void dropNonExistentLobbyTest() {
        LobbyDTO nonExistentLobby = new LobbyDTO("NonExistent", firstOwner, 2, 4);

        doThrow(IllegalArgumentException.class).when(lobbyStore).removeLobby(nonExistentLobby);

        assertThrows(IllegalArgumentException.class, () -> lobbyManagement.dropLobby(nonExistentLobby));
    }

    @Test
    @DisplayName("Update non-existent lobby")
    void updateNonExistentLobbyTest() {
        LobbyDTO nonExistentLobby = new LobbyDTO("NonExistent", firstOwner, 2, 4);

        doThrow(IllegalArgumentException.class).when(lobbyStore).updateLobby(nonExistentLobby);

        assertThrowsExactly(IllegalArgumentException.class, () -> lobbyManagement.updateLobby(nonExistentLobby));

    }

    @Test
    @DisplayName("Create lobby with empty name")
    void createLobbyWithEmptyNameTest() {
        LobbyDTO emptyNameLobby = new LobbyDTO("", firstOwner, 2, 4);

        assertDoesNotThrow(() -> lobbyManagement.createLobby(emptyNameLobby));

        when(lobbyStore.getLobby(emptyNameLobby.getName())).thenReturn(Optional.of(emptyNameLobby));
        assertTrue(lobbyManagement.getLobby(emptyNameLobby).isPresent());
    }

    @Test
    @DisplayName("Create duplicate lobby")
    void createDuplicateLobbyTest() {
        lobbyManagement.createLobby(lobby);

        doThrow(IllegalArgumentException.class).when(lobbyStore).addLobby(lobby);

        assertThrows(IllegalArgumentException.class, () -> lobbyManagement.createLobby(lobby));
    }

    @Test
    @DisplayName("Update lobby status for non-existent lobby")
    void updateLobbyStatusForNonExistentLobbyTest() {
        LobbyDTO nonExistentLobby = new LobbyDTO("NonExistent", firstOwner, 2, 4);

        assertThrows(IllegalArgumentException.class, () -> lobbyManagement.updateLobbyStatus(nonExistentLobby, LobbyStatus.RUNNING));


    }
}