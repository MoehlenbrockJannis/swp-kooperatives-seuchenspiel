package de.uol.swp.server.lobby;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.lobby.LobbyStatus;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.lobby.server_message.LobbyJoinUserServerMessage;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.UserManagement;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("LobbyService Test")
class LobbyServiceTest {

    static final UserDTO firstOwner = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");
    static final UserDTO secondOwner = new UserDTO("Marco2", "Marco2", "Marco2@Grawunder.com");
    static final Lobby lobby = new LobbyDTO("TestLobby", firstOwner, 2, 4);

    final EventBus bus = EventBus.builder()
            .logNoSubscriberMessages(false)
            .sendNoSubscriberEvent(false)
            .throwSubscriberException(true)
            .build();
    final UserManagement userManagement = new UserManagement(new MainMemoryBasedUserStore());
    final AuthenticationService authService = mock(AuthenticationService.class);
    final LobbyManagement lobbyManagement = spy(new LobbyManagement());
    final LobbyService lobbyService = new LobbyService(lobbyManagement, authService, bus);

    @BeforeEach
    void setUp() {
        lobbyManagement.getAllLobbies().forEach(lobbyManagement::dropLobby);
    }

    @Test
    @DisplayName("Create lobby")
    void createLobbyTest() {
        final CreateLobbyRequest request = new CreateLobbyRequest(lobby, firstOwner);

        bus.post(request);

        assertNotNull(lobbyManagement.getLobby(lobby));
        lobbyManagement.getLobby(lobby).ifPresent(l -> assertEquals(firstOwner, l.getOwner()));
    }

    @Test
    @DisplayName("Create second lobby with same name")
    void createSecondLobbyWithSameName() {
        final CreateLobbyRequest request = new CreateLobbyRequest(lobby, firstOwner);
        final CreateLobbyRequest request2 = new CreateLobbyRequest(lobby, secondOwner);

        bus.post(request);

        Exception e = assertThrows(EventBusException.class, () -> bus.post(request2));
        assertInstanceOf(IllegalArgumentException.class, e.getCause());

        assertNotNull(lobbyManagement.getLobby(lobby));
        lobbyManagement.getLobby(lobby).ifPresent(l -> assertNotEquals(secondOwner, l.getOwner()));
    }

    @Test
    @DisplayName("User joins lobby")
    void lobbyJoinUserTest() {
        lobbyManagement.createLobby(lobby);

        final LobbyJoinUserRequest request = new LobbyJoinUserRequest(lobby, secondOwner);

        bus.post(request);

        lobbyManagement.getLobby(lobby).ifPresent(l -> {
            assertTrue(l.getUsers().contains(firstOwner));
            assertTrue(l.getUsers().contains(secondOwner));
        });
    }

    @Test
    @DisplayName("User leaves lobby")
    void lobbyLeaveUserTest() {
        lobbyManagement.createLobby(lobby);
        lobbyManagement.getLobby(lobby).ifPresent(l -> l.joinUser(secondOwner));

        final LobbyLeaveUserRequest request = new LobbyLeaveUserRequest(lobby, secondOwner);

        bus.post(request);

        lobbyManagement.getLobby(lobby).ifPresent(l ->
                assertFalse(l.getUsers().contains(secondOwner))
        );
    }

    @Test
    @DisplayName("Find lobbies request")
    void onLobbyFindLobbiesRequestTest() {
        lobbyManagement.createLobby(lobby);
        final RetrieveAllLobbiesRequest request = new RetrieveAllLobbiesRequest();

        reset(lobbyManagement);

        bus.post(request);

        verify(lobbyManagement, atLeastOnce()).getAllLobbies();
    }

    @Test
    @DisplayName("Update lobby status")
    void onLobbyUpdateStatusRequestTest() {
        lobbyManagement.createLobby(lobby);
        final LobbyUpdateStatusRequest request = new LobbyUpdateStatusRequest(lobby, LobbyStatus.RUNNING);

        bus.post(request);

        verify(lobbyManagement).updateLobbyStatus(lobby, LobbyStatus.RUNNING);
    }

    @Test
    @DisplayName("User joins lobby they're already in")
    void lobbyJoinUserAlreadyInLobbyTest() {
        lobbyManagement.createLobby(lobby);
        lobbyManagement.getLobby(lobby).ifPresent(l -> l.joinUser(secondOwner));

        final LobbyJoinUserRequest request = new LobbyJoinUserRequest(lobby, secondOwner);

        bus.post(request);

        verify(lobbyManagement, times(2)).getLobby(lobby);

        lobbyManagement.getLobby(lobby).ifPresent(l -> {
            assertEquals(2, l.getUsers().size());
            assertTrue(l.getUsers().contains(secondOwner));
        });
    }

    @Test
    void lobbyKickUserTest() {
        lobbyManagement.createLobby(lobby);

        if (lobbyManagement.getLobby(lobby).isPresent()) {
            lobbyManagement.getLobby(lobby).get().joinUser(secondOwner);
        }

        final LobbyKickUserRequest request = new LobbyKickUserRequest(lobby, secondOwner);

        bus.post(request);

        if (lobbyManagement.getLobby(lobby).isPresent()) {
            assertFalse(lobbyManagement.getLobby(lobby).get().getUsers().contains(secondOwner));
        }
    }

    @Test
    @DisplayName("User joins non-existent lobby")
    void lobbyJoinUserNonExistentLobbyTest() {
        final Lobby nonExistentLobby = new LobbyDTO("NonExistentLobby", firstOwner, 2, 4);
        final LobbyJoinUserRequest request = new LobbyJoinUserRequest(nonExistentLobby, secondOwner);

        bus.post(request);

        verify(lobbyManagement, times(1)).getLobby(nonExistentLobby);
        assertTrue(lobbyManagement.getAllLobbies().isEmpty());
    }

    @Test
    @DisplayName("Last user leaves lobby")
    void lobbyLeaveUserLastUserTest() {
        Lobby singleUserLobby = new LobbyDTO("SingleUserLobby", firstOwner, 1, 4);
        lobbyManagement.createLobby(singleUserLobby);

        LobbyLeaveUserRequest request = new LobbyLeaveUserRequest(singleUserLobby, firstOwner);

        bus.post(request);

        verify(lobbyManagement, times(1)).dropLobby(singleUserLobby);

        assertTrue(lobbyManagement.getAllLobbies().isEmpty());
    }

    @Test
    @DisplayName("User leaves non-existent lobby")
    void lobbyLeaveUserNonExistentLobbyTest() {
        final Lobby nonExistentLobby = new LobbyDTO("NonExistentLobby", firstOwner, 2, 4);
        final LobbyLeaveUserRequest request = new LobbyLeaveUserRequest(nonExistentLobby, firstOwner);

        bus.post(request);

        verify(lobbyManagement, times(1)).getLobby(nonExistentLobby);
        assertTrue(lobbyManagement.getAllLobbies().isEmpty());
    }

    @Test
    @DisplayName("Update status of non-existent lobby")
    void onLobbyUpdateStatusRequestNonExistentLobbyTest() {
        final Lobby nonExistentLobby = new LobbyDTO("NonExistentLobby", firstOwner, 2, 4);
        final LobbyUpdateStatusRequest request = new LobbyUpdateStatusRequest(nonExistentLobby, LobbyStatus.RUNNING);

        bus.post(request);

        verify(lobbyManagement, times(1)).getLobby(nonExistentLobby);
        verify(lobbyManagement, never()).updateLobbyStatus(any(), any());
    }

    @Test
    @DisplayName("Send message to all in null lobby")
    void sendToAllInLobbyNullLobbyTest() {
        LobbyJoinUserServerMessage message = mock(LobbyJoinUserServerMessage.class);

        assertDoesNotThrow(() -> lobbyService.sendToAllInLobby(null, message));

        verify(message, never()).setReceiver(any());
    }
}