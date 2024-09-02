package de.uol.swp.client.lobby;

import de.uol.swp.client.EventBusBasedTest;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.CreateLobbyRequest;
import de.uol.swp.common.lobby.message.LobbyJoinUserRequest;
import de.uol.swp.common.lobby.message.LobbyLeaveUserRequest;
import de.uol.swp.common.lobby.request.LobbyFindLobbiesRequest;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.greenrobot.eventbus.Subscribe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LobbyServiceTest extends EventBusBasedTest {
    private LobbyService lobbyService;

    private String lobbyName;
    private User user;

    @BeforeEach
    void setUp() {
        this.lobbyService = new LobbyService(getBus());
        this.lobbyName = "test";
        this.user = new UserDTO("testo", "", "");
    }

    @Test
    void createNewLobby() throws InterruptedException {
        lobbyService.createNewLobby(lobbyName, (UserDTO) user);

        waitForLock();

        assertTrue(event instanceof CreateLobbyRequest);

        final CreateLobbyRequest createLobbyRequest = (CreateLobbyRequest) event;
        assertEquals(createLobbyRequest.getLobbyName(), lobbyName);
        assertEquals(createLobbyRequest.getOwner(), user);
    }

    @Test
    void joinLobby() throws InterruptedException {
        lobbyService.joinLobby(lobbyName, (UserDTO) user);

        waitForLock();

        assertTrue(event instanceof LobbyJoinUserRequest);

        final LobbyJoinUserRequest lobbyJoinUserRequest = (LobbyJoinUserRequest) event;
        assertEquals(lobbyJoinUserRequest.getLobbyName(), lobbyName);
        assertEquals(lobbyJoinUserRequest.getUser(), user);
    }

    @Test
    void findLobbies() throws InterruptedException {
        lobbyService.findLobbies();

        waitForLock();

        assertTrue(event instanceof LobbyFindLobbiesRequest);
    }

    @Test
    void leaveLobby() throws InterruptedException {
        lobbyService.leaveLobby(lobbyName, user);

        waitForLock();

        assertTrue(event instanceof LobbyLeaveUserRequest);

        final LobbyLeaveUserRequest lobbyLeaveUserRequest = (LobbyLeaveUserRequest) event;
        assertEquals(lobbyLeaveUserRequest.getLobbyName(), lobbyName);
        assertEquals(lobbyLeaveUserRequest.getUser(), user);
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
    public void onEvent(final LobbyFindLobbiesRequest lobbyFindLobbiesRequest) {
        handleEvent(lobbyFindLobbiesRequest);
    }

    @Subscribe
    public void onEvent(final LobbyLeaveUserRequest lobbyLeaveUserRequest) {
        handleEvent(lobbyLeaveUserRequest);
    }
}