package de.uol.swp.server.lobby;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.UserManagement;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import org.greenrobot.eventbus.EventBus;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("UnstableApiUsage")
public class LobbyManagementTest {

    static final UserDTO firstOwner = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");
    static final LobbyDTO lobby = new LobbyDTO("TestLobby", firstOwner, 2, 4);

    final EventBus bus = EventBus.getDefault();
    final UserManagement userManagement = new UserManagement(new MainMemoryBasedUserStore());
    final AuthenticationService authService = new AuthenticationService(bus, userManagement);
    final LobbyManagement lobbyManagement = new LobbyManagement();
    final LobbyService lobbyService = new LobbyService(lobbyManagement, authService, bus);

    LobbyManagement getDefaultManagement() {
        LobbyManagement management = new LobbyManagement();
        management.createLobby(lobby);
        return management;
    }

    @Test
    void createLobbyTest() {
        lobbyManagement.createLobby(lobby);

        assertNotNull(lobbyManagement.getLobby(lobby));
        if(lobbyManagement.getLobby(lobby).isPresent()){
            assertEquals(lobbyManagement.getLobby(lobby).get().getOwner(), firstOwner);
        }
    }

    @Test
    void dropLobbyTest() {
        LobbyManagement lobbyManagement = getDefaultManagement();

        lobbyManagement.dropLobby(lobby);

        assertTrue(lobbyManagement.getLobby(lobby).isEmpty());
    }

    @Test
    void getLobbyTest() {
        LobbyManagement lobbyManagement = getDefaultManagement();

        if(lobbyManagement.getLobby(lobby).isPresent()){
            assertNotNull(lobbyManagement.getLobby(lobby));
            assertEquals(lobbyManagement.getLobby(lobby).get().getOwner(), firstOwner);
        }
    }

    @Test
    void getAllLobbiesTest() {
        final List<Lobby> lobbies = List.of(
                new LobbyDTO("1", new UserDTO("Test", "", ""), 2, 4),
                new LobbyDTO("2", new UserDTO("Tom", "", ""), 2, 4)
        );

        lobbies.forEach(lobbyManagement::createLobby);

        final List<Lobby> lobbiesInLobbyManagement = lobbyManagement.getAllLobbies();

        assertEquals(lobbies.size(), lobbiesInLobbyManagement.size());
        for (int i = 0; i < lobbiesInLobbyManagement.size(); i++) {
            assertEquals(lobbies.get(i).getName(), lobbiesInLobbyManagement.get(i).getName());
            assertEquals(lobbies.get(i).getOwner(), lobbiesInLobbyManagement.get(i).getOwner());
        }
    }

}
