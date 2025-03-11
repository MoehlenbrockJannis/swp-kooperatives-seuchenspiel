package de.uol.swp.server.lobby;

import de.uol.swp.common.game.GameDifficulty;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.lobby.LobbyStatus;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.lobby.server_message.JoinUserLobbyServerMessage;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.UserPlayer;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.lobby.store.LobbyStore;
import de.uol.swp.server.lobby.store.MainMemoryBasedLobbyStore;
import de.uol.swp.server.user.AuthenticationService;
import de.uol.swp.server.user.UserManagement;
import de.uol.swp.server.user.store.MainMemoryBasedUserStore;
import de.uol.swp.server.user.store.UserStore;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("LobbyService Test")
class LobbyServiceTest {

    private UserDTO firstOwner;
    private Player firstOwnerAsPlayer;
    private UserDTO secondOwner;
    private Player secondOwnerAsPlayer;
    private Lobby lobby;
    private EventBus bus;
    private UserManagement userManagement;
    private AuthenticationService authService;
    private LobbyManagement lobbyManagement;
    private LobbyService lobbyService;
    private LobbyStore lobbyStore;
    private UserStore userStore;

    @BeforeEach
    void setUp() {
        this.firstOwner = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");
        this.secondOwner = new UserDTO("Marco2", "Marco2", "Marco2@Grawunder.com");
        this.secondOwnerAsPlayer = new UserPlayer(this.secondOwner);
        this.firstOwnerAsPlayer = new UserPlayer(this.firstOwner);
        this.lobby = new LobbyDTO("TestLobby", firstOwner);
        this.lobby.setId(123);
        this.bus = EventBus.builder()
                .logNoSubscriberMessages(false)
                .sendNoSubscriberEvent(false)
                .throwSubscriberException(true)
                .build();
        this.userStore = mock(MainMemoryBasedUserStore.class);
        this.lobbyStore = mock(MainMemoryBasedLobbyStore.class);
        this.userManagement = new UserManagement(userStore);
        this.authService = mock(AuthenticationService.class);
        this.lobbyManagement = spy(new LobbyManagement(lobbyStore));
        this.lobbyService = new LobbyService(lobbyManagement, authService, bus);
        lobbyManagement.getAllLobbies().forEach(lobbyManagement::dropLobby);
    }

    @Test
    @DisplayName("Create lobby")
    void createLobbyTest() {
        final CreateUserLobbyRequest request = new CreateUserLobbyRequest(lobby, firstOwner);

        bus.post(request);

        assertNotNull(lobbyManagement.getLobby(lobby));
        lobbyManagement.getLobby(lobby).ifPresent(l -> assertEquals(firstOwner, l.getOwner()));
    }

    @Test
    @DisplayName("Create second lobby with same name")
    void createSecondLobbyWithSameName() {
        final CreateUserLobbyRequest request = new CreateUserLobbyRequest(lobby, firstOwner);
        final CreateUserLobbyRequest request2 = new CreateUserLobbyRequest(lobby, secondOwner);

        when(lobbyStore.getLobby(lobby.getId())).thenReturn(Optional.of(lobby));

        bus.post(request);

        doThrow(IllegalArgumentException.class).when(lobbyStore).addLobby(lobby);

        Exception e = assertThrows(EventBusException.class, () -> bus.post(request2));
        assertInstanceOf(IllegalArgumentException.class, e.getCause());


        assertNotNull(lobbyManagement.getLobby(lobby));
        lobbyManagement.getLobby(lobby).ifPresent(l -> assertNotEquals(secondOwner, l.getOwner()));
    }

    @Test
    @DisplayName("User joins lobby")
    void lobbyJoinUserTest() {
        lobbyManagement.createLobby(lobby);

        final JoinUserLobbyRequest request = new JoinUserLobbyRequest(lobby, secondOwner);

        bus.post(request);

        lobbyManagement.getLobby(lobby).ifPresent(l -> {
            assertTrue(l.getUsers().contains(firstOwner));
            assertTrue(l.getUsers().contains(secondOwner));
        });
    }

    @Test
    @DisplayName("Player joins lobby")
    void lobbyJoinPlayer() {
        this.lobbyManagement.createLobby(this.lobby);

        final JoinPlayerLobbyRequest request = new JoinPlayerLobbyRequest(this.lobby, secondOwnerAsPlayer);

        bus.post(request);

        lobbyManagement.getLobby(lobby).ifPresent(lobby -> {
            assertTrue(lobby.getPlayers().contains(secondOwnerAsPlayer));
            assertTrue(lobby.getUsers().contains(secondOwner));
        });
    }

    @Test
    @DisplayName("User leaves lobby")
    void lobbyLeaveUserTest() {
        lobbyManagement.createLobby(lobby);
        lobbyManagement.getLobby(lobby).ifPresent(l -> l.joinUser(secondOwner));

        final LeavePlayerLobbyRequest request = new LeavePlayerLobbyRequest(lobby, secondOwnerAsPlayer);

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

        when(lobbyManagement.getLobby(lobby)).thenReturn(Optional.of(lobby));

        doNothing().when(lobbyManagement).updateLobbyStatus(lobby, LobbyStatus.RUNNING);

        final UpdateLobbyStatusRequest request = new UpdateLobbyStatusRequest(lobby, LobbyStatus.RUNNING);

        bus.post(request);


        verify(lobbyManagement).updateLobbyStatus(lobby, LobbyStatus.RUNNING);
    }

    @Test
    @DisplayName("User joins lobby they're already in")
    void lobbyJoinUserAlreadyInLobbyTest() {
        lobbyManagement.createLobby(lobby);
        lobbyManagement.getLobby(lobby).ifPresent(l -> l.joinUser(secondOwner));

        final JoinUserLobbyRequest request = new JoinUserLobbyRequest(lobby, secondOwner);

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

        final KickPlayerLobbyRequest request = new KickPlayerLobbyRequest(lobby, secondOwnerAsPlayer);

        bus.post(request);

        if (lobbyManagement.getLobby(lobby).isPresent()) {
            assertFalse(lobbyManagement.getLobby(lobby).get().getUsers().contains(secondOwner));
        }
    }

    @Test
    @DisplayName("User joins non-existent lobby")
    void lobbyJoinUserNonExistentLobbyTest() {
        final Lobby nonExistentLobby = new LobbyDTO("NonExistentLobby", firstOwner);
        final JoinUserLobbyRequest request = new JoinUserLobbyRequest(nonExistentLobby, secondOwner);

        bus.post(request);

        verify(lobbyManagement, times(1)).getLobby(nonExistentLobby);
        assertTrue(lobbyManagement.getAllLobbies().isEmpty());
    }

    @Test
    @DisplayName("Last user leaves lobby")
    void lobbyLeaveUserLastUserTest() {
        Lobby singleUserLobby = new LobbyDTO("SingleUserLobby", firstOwner);
        lobbyManagement.createLobby(singleUserLobby);

        when(lobbyManagement.getLobby(singleUserLobby)).thenReturn(Optional.of(singleUserLobby));

        LeavePlayerLobbyRequest request = new LeavePlayerLobbyRequest(singleUserLobby, firstOwnerAsPlayer);

        bus.post(request);

        verify(lobbyManagement, times(1)).dropLobby(singleUserLobby);

        assertTrue(lobbyManagement.getAllLobbies().isEmpty());
    }

    @Test
    @DisplayName("User leaves non-existent lobby")
    void lobbyLeaveUserNonExistentLobbyTest() {
        final Lobby nonExistentLobby = new LobbyDTO("NonExistentLobby", firstOwner);
        final LeavePlayerLobbyRequest request = new LeavePlayerLobbyRequest(nonExistentLobby, firstOwnerAsPlayer);

        bus.post(request);

        verify(lobbyManagement, times(1)).getLobby(nonExistentLobby);
        assertTrue(lobbyManagement.getAllLobbies().isEmpty());
    }

    @Test
    @DisplayName("Update status of non-existent lobby")
    void onLobbyUpdateStatusRequestNonExistentLobbyTest() {
        final Lobby nonExistentLobby = new LobbyDTO("NonExistentLobby", firstOwner);
        final UpdateLobbyStatusRequest request = new UpdateLobbyStatusRequest(nonExistentLobby, LobbyStatus.RUNNING);

        bus.post(request);

        verify(lobbyManagement, times(1)).getLobby(nonExistentLobby);
        verify(lobbyManagement, never()).updateLobbyStatus(any(), any());
    }

    @Test
    @DisplayName("Send message to all in null lobby")
    void sendToAllInLobbyNullLobbyTest() {
        JoinUserLobbyServerMessage message = mock(JoinUserLobbyServerMessage.class);

        assertDoesNotThrow(() -> lobbyService.sendToAllInLobby(null, message));

        verify(message, never()).setReceiver(any());
    }

    @Test
    @DisplayName("Update difficulty in lobby")
    void onDifficultyUpdateRequestTest() {
        lobbyManagement.createLobby(lobby);
        final GameDifficulty newDifficulty = GameDifficulty.HARD;
        final DifficultyUpdateRequest request = new DifficultyUpdateRequest(lobby, newDifficulty);

        when(lobbyManagement.getLobby(lobby)).thenReturn(Optional.of(lobby));

        bus.post(request);

        verify(lobbyManagement, times(1)).getLobby(lobby);
        verify(authService, times(1)).getSessions((lobby.getUsers()));
    }
}