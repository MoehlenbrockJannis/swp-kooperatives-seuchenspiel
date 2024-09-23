package de.uol.swp.server.lobby;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.lobby.request.CreateLobbyRequest;
import de.uol.swp.common.lobby.request.LobbyJoinUserRequest;
import de.uol.swp.common.lobby.request.LobbyLeaveUserRequest;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.UserManagement;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LobbyServiceTest {

    static final UserDTO firstOwner = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");
    static final UserDTO secondOwner = new UserDTO("Marco2", "Marco2", "Marco2@Grawunder.com");
    static final Lobby lobby = new LobbyDTO("TestLobby", firstOwner, 2, 4);

    // Special version of event bus for testing
    final EventBus bus = EventBus.builder()
            .logNoSubscriberMessages(false)
            .sendNoSubscriberEvent(false)
            .throwSubscriberException(true)
            .build();
    final UserManagement userManagement = new UserManagement(new MainMemoryBasedUserStore());
    final AuthenticationService authService = new AuthenticationService(bus, userManagement);
    final LobbyManagement lobbyManagement = new LobbyManagement();
    final LobbyService lobbyService = new LobbyService(lobbyManagement, authService, bus);

    @Test
    void createLobbyTest() {
        final CreateLobbyRequest request = new CreateLobbyRequest(lobby, firstOwner);

        // The post will lead to a call of a LobbyService function
        bus.post(request);

        // Check if Lobby was created
        assertNotNull(lobbyManagement.getLobby(lobby));
        // Checks whether it is also the correct owner
        if (lobbyManagement.getLobby(lobby).isPresent()) {
            assertEquals(lobbyManagement.getLobby(lobby).get().getOwner(), firstOwner);
        }
    }

    @Test
    void createSecondLobbyWithSameName() {
        final CreateLobbyRequest request = new CreateLobbyRequest(lobby, firstOwner);
        final CreateLobbyRequest request2 = new CreateLobbyRequest(lobby, secondOwner);

        bus.post(request);

        // event bus throw exception
        Exception e = assertThrows(EventBusException.class,
                () -> bus.post(request2)
        );
        // Check if the nested exception is the right exception
        assertTrue(e.getCause() instanceof IllegalArgumentException);

        // old lobby should be still in the LobbyManagement
        assertNotNull(lobbyManagement.getLobby(lobby));

        // old lobby should not be overwritten!
        if (lobbyManagement.getLobby(lobby).isPresent()) {
            assertNotEquals(lobbyManagement.getLobby(lobby).get().getOwner(), secondOwner);
        }
    }

    @Test
    void lobbyJoinUserTest() {
        // Create the lobby
        lobbyManagement.createLobby(lobby);

        final LobbyJoinUserRequest request = new LobbyJoinUserRequest(lobby, secondOwner);

        // The post will lead to a call of a LobbyService function
        bus.post(request);

        // Check if the user is listed in the lobby
        if (lobbyManagement.getLobby(lobby).isPresent()) {
            assertTrue(lobbyManagement.getLobby(lobby).get().getUsers().contains(firstOwner));
            assertTrue(lobbyManagement.getLobby(lobby).get().getUsers().contains(secondOwner));
        }
    }

    @Test
    void lobbyLeaveUserTest() {
        // Create the lobby
        lobbyManagement.createLobby(lobby);
        // Join User
        if (lobbyManagement.getLobby(lobby).isPresent()) {
            lobbyManagement.getLobby(lobby).get().joinUser(secondOwner);
        }

        final LobbyLeaveUserRequest request = new LobbyLeaveUserRequest(lobby, secondOwner);

        // The post will lead to a call of a LobbyService function
        bus.post(request);

        // Check if the user is no longer listed in the lobby
        if (lobbyManagement.getLobby(lobby).isPresent()) {
            assertFalse(lobbyManagement.getLobby(lobby).get().getUsers().contains(secondOwner));
        }
    }

}
