package de.uol.swp.client.lobby;

import de.uol.swp.client.EventBusBasedTest;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyStatus;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.lobby.request.CreateLobbyRequest;
import de.uol.swp.common.lobby.request.LobbyJoinUserRequest;
import de.uol.swp.common.lobby.request.LobbyLeaveUserRequest;
import de.uol.swp.common.lobby.request.RetrieveAllLobbiesRequest;
import de.uol.swp.common.lobby.request.LobbyUpdateStatusRequest;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.greenrobot.eventbus.Subscribe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LobbyServiceTest extends EventBusBasedTest {
    private LobbyService lobbyService;

    private Lobby lobby;
    private User user;


    @BeforeEach
    void setUp() {
        this.lobbyService = new LobbyService(getBus());
        this.user = new UserDTO("testo", "", "");
        this.lobby = new LobbyDTO("TestLobby", user);
    }

    @Test
    void createNewLobby() throws InterruptedException {
        lobbyService.createNewLobby(lobby.getName(), user);

        waitForLock();

        assertTrue(event instanceof CreateLobbyRequest);

        final CreateLobbyRequest createLobbyRequest = (CreateLobbyRequest) event;
        assertEquals(createLobbyRequest.getLobby(), lobby);
        assertEquals(createLobbyRequest.getUser(), user);
    }

    @Test
    void joinLobby() throws InterruptedException {
        lobbyService.joinLobby(lobby, user);

        waitForLock();

        assertTrue(event instanceof LobbyJoinUserRequest);

        final LobbyJoinUserRequest lobbyJoinUserRequest = (LobbyJoinUserRequest) event;
        assertEquals(lobbyJoinUserRequest.getLobby(), lobby);
        assertEquals(lobbyJoinUserRequest.getUser(), user);
    }

    @Test
    void findLobbies() throws InterruptedException {
        lobbyService.findLobbies();

        waitForLock();

        assertTrue(event instanceof RetrieveAllLobbiesRequest);
    }

    @Test
    void leaveLobby() throws InterruptedException {
        lobbyService.leaveLobby(lobby, user);

        waitForLock();

        assertTrue(event instanceof LobbyLeaveUserRequest);

        final LobbyLeaveUserRequest lobbyLeaveUserRequest = (LobbyLeaveUserRequest) event;
        assertEquals(lobbyLeaveUserRequest.getLobby(), lobby);
        assertEquals(lobbyLeaveUserRequest.getUser(), user);
    }

    @Test
    void updateLobbyStatus() throws InterruptedException {
        lobbyService.updateLobbyStatus(lobby, LobbyStatus.RUNNING);

        waitForLock();

        assertInstanceOf(LobbyUpdateStatusRequest.class, event);

        final LobbyUpdateStatusRequest lobbyUpdateStatusRequest = (LobbyUpdateStatusRequest) event;
        assertEquals(LobbyStatus.RUNNING, lobbyUpdateStatusRequest.getStatus());
    }

    @Subscribe
    public void onEvent(final CreateLobbyRequest createLobbyRequest) {
        handleEvent(createLobbyRequest);
    }

    @Subscribe
    public void onEvent(final LobbyJoinUserRequest lobbyJoinUserRequest) {
        handleEvent(lobbyJoinUserRequest);
    }

    @Subscribe
    public void onEvent(final RetrieveAllLobbiesRequest retrieveAllLobbiesRequest) {
        handleEvent(retrieveAllLobbiesRequest);
    }

    @Subscribe
    public void onEvent(final LobbyLeaveUserRequest lobbyLeaveUserRequest) {
        handleEvent(lobbyLeaveUserRequest);
    }

    @Subscribe
    public void onEvent(final LobbyUpdateStatusRequest lobbyUpdateStatusRequest) {
        handleEvent(lobbyUpdateStatusRequest);
    }
}