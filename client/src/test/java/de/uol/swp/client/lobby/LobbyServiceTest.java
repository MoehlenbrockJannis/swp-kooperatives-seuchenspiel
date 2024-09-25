package de.uol.swp.client.lobby;

import de.uol.swp.client.EventBusBasedTest;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyStatus;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.map.request.RetrieveOriginalGameMapTypeRequest;
import de.uol.swp.common.plague.request.RetrieveAllPlaguesRequest;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.greenrobot.eventbus.Subscribe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LobbyService Test")
public class LobbyServiceTest extends EventBusBasedTest {
    private LobbyService lobbyService;

    private Lobby lobby;
    private User user;

    @BeforeEach
    void setUp() {
        this.lobbyService = new LobbyService(getBus());
        this.user = new UserDTO("testo", "", "");
        this.lobby = new LobbyDTO("TestLobby", user, 2, 4);
    }

    @Test
    @DisplayName("Create new lobby")
    void createNewLobby() throws InterruptedException {
        lobbyService.createNewLobby(lobby.getName(), user, 2, 4);

        waitForLock();

        assertInstanceOf(CreateLobbyRequest.class, event);

        final CreateLobbyRequest createLobbyRequest = (CreateLobbyRequest) event;
        assertEquals(createLobbyRequest.getLobby(), lobby);
        assertEquals(createLobbyRequest.getUser(), user);
    }

    @Test
    @DisplayName("Join lobby")
    void joinLobby() throws InterruptedException {
        lobbyService.joinLobby(lobby, user);

        waitForLock();

        assertInstanceOf(LobbyJoinUserRequest.class, event);

        final LobbyJoinUserRequest lobbyJoinUserRequest = (LobbyJoinUserRequest) event;
        assertEquals(lobbyJoinUserRequest.getLobby(), lobby);
        assertEquals(lobbyJoinUserRequest.getUser(), user);
    }

    @Test
    @DisplayName("Find lobbies")
    void findLobbies() throws InterruptedException {
        lobbyService.findLobbies();

        waitForLock();

        assertInstanceOf(RetrieveAllLobbiesRequest.class, event);
    }

    @Test
    @DisplayName("Leave lobby")
    void leaveLobby() throws InterruptedException {
        lobbyService.leaveLobby(lobby, user);

        waitForLock();

        assertInstanceOf(LobbyLeaveUserRequest.class, event);

        final LobbyLeaveUserRequest lobbyLeaveUserRequest = (LobbyLeaveUserRequest) event;
        assertEquals(lobbyLeaveUserRequest.getLobby(), lobby);
        assertEquals(lobbyLeaveUserRequest.getUser(), user);
    }

    @Test
    void kickUser() throws InterruptedException {
        lobbyService.kickUser(lobby, user);

        waitForLock();

        assertTrue(event instanceof LobbyKickUserRequest);

        final LobbyKickUserRequest kickUserRequest = (LobbyKickUserRequest) event;
        assertEquals(kickUserRequest.getLobby(), lobby);
        assertEquals(kickUserRequest.getUser(), user);
    }

    @Test
    @DisplayName("Update lobby status")
    void updateLobbyStatus() throws InterruptedException {
        lobbyService.updateLobbyStatus(lobby, LobbyStatus.RUNNING);

        waitForLock();

        assertInstanceOf(LobbyUpdateStatusRequest.class, event);

        final LobbyUpdateStatusRequest lobbyUpdateStatusRequest = (LobbyUpdateStatusRequest) event;
        assertEquals(LobbyStatus.RUNNING, lobbyUpdateStatusRequest.getStatus());
    }

    @Test
    @DisplayName("Get original game map type")
    void getOriginalGameMapType() throws InterruptedException {
        lobbyService.getOriginalGameMapType();

        waitForLock();

        assertInstanceOf(RetrieveOriginalGameMapTypeRequest.class, event);
    }

    @Test
    @DisplayName("Get plagues")
    void getPlagues() throws InterruptedException {
        lobbyService.getPlagues();

        waitForLock();

        assertInstanceOf(RetrieveAllPlaguesRequest.class, event);
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
    public void onEvent(final LobbyKickUserRequest lobbyKickUserRequest) {
        handleEvent(lobbyKickUserRequest);
    }

    @Subscribe
    public void onEvent(final LobbyUpdateStatusRequest lobbyUpdateStatusRequest) {
        handleEvent(lobbyUpdateStatusRequest);
    }

    @Subscribe
    public void onEvent(final RetrieveOriginalGameMapTypeRequest retrieveOriginalGameMapTypeRequest) {
        handleEvent(retrieveOriginalGameMapTypeRequest);
    }

    @Subscribe
    public void onEvent(final RetrieveAllPlaguesRequest retrieveAllPlaguesRequest) {
        handleEvent(retrieveAllPlaguesRequest);
    }
}