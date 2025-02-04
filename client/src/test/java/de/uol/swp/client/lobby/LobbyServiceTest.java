package de.uol.swp.client.lobby;

import de.uol.swp.client.EventBusBasedTest;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.lobby.LobbyStatus;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.map.request.RetrieveOriginalGameMapTypeRequest;
import de.uol.swp.common.plague.request.RetrieveAllPlaguesRequest;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.UserPlayer;
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
    private Player defaultPlayer;

    @BeforeEach
    void setUp() {
        this.lobbyService = new LobbyService(getBus());
        this.user = new UserDTO("testo", "", "");
        this.lobby = new LobbyDTO("TestLobby", user);
        this.defaultPlayer = new UserPlayer(this.user);
    }

    @Test
    @DisplayName("Create new lobby")
    void createNewLobby() throws InterruptedException {
        lobbyService.createNewLobby(lobby.getName(), user);

        waitForLock();

        assertInstanceOf(CreateUserLobbyRequest.class, event);

        final CreateUserLobbyRequest createLobbyRequest = (CreateUserLobbyRequest) event;
        assertEquals(createLobbyRequest.getLobby(), lobby);
        assertEquals(createLobbyRequest.getUser(), user);
    }

    @Test
    @DisplayName("Join lobby")
    void joinLobby() throws InterruptedException {
        lobbyService.joinLobby(lobby, user);

        waitForLock();

        assertInstanceOf(JoinUserLobbyRequest.class, event);

        final JoinUserLobbyRequest lobbyJoinUserRequest = (JoinUserLobbyRequest) event;
        assertEquals(lobbyJoinUserRequest.getLobby(), lobby);
        assertEquals(lobbyJoinUserRequest.getUser(), user);
    }

    @Test
    @DisplayName("Player join lobby")
    void playerJoinLobby() throws InterruptedException {
        lobbyService.playerJoinLobby(this.lobby, this.defaultPlayer);

        waitForLock();

        assertInstanceOf(JoinPlayerLobbyRequest.class, this.event);

        final JoinPlayerLobbyRequest lobbyJoinPlayerRequest = (JoinPlayerLobbyRequest) this.event;
        assertEquals(lobbyJoinPlayerRequest.getLobby(), this.lobby);
        assertEquals(lobbyJoinPlayerRequest.getPlayer(), this.defaultPlayer);
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
        lobbyService.leaveLobby(lobby, defaultPlayer);

        waitForLock();

        assertInstanceOf(LeavePlayerLobbyRequest.class, event);

        final LeavePlayerLobbyRequest lobbyLeaveUserRequest = (LeavePlayerLobbyRequest) event;
        assertEquals(lobbyLeaveUserRequest.getLobby(), lobby);
        assertEquals(lobbyLeaveUserRequest.getPlayer(), defaultPlayer);
    }

    @Test
    @DisplayName("Kick user")
    void kickUser() throws InterruptedException {
        lobbyService.kickPlayer(lobby, defaultPlayer);

        waitForLock();

        assertTrue(event instanceof KickPlayerLobbyRequest);

        final KickPlayerLobbyRequest kickUserRequest = (KickPlayerLobbyRequest) event;
        assertEquals(kickUserRequest.getLobby(), lobby);
        assertEquals(kickUserRequest.getPlayer(), defaultPlayer);
    }

    @Test
    @DisplayName("Update lobby status")
    void updateLobbyStatus() throws InterruptedException {
        lobbyService.updateLobbyStatus(lobby, LobbyStatus.RUNNING);

        waitForLock();

        assertInstanceOf(UpdateLobbyStatusRequest.class, event);

        final UpdateLobbyStatusRequest lobbyUpdateStatusRequest = (UpdateLobbyStatusRequest) event;
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
    public void onEvent(final CreateUserLobbyRequest createLobbyRequest) {
        handleEvent(createLobbyRequest);
    }

    @Subscribe
    public void onEvent(final JoinUserLobbyRequest lobbyJoinUserRequest) {
        handleEvent(lobbyJoinUserRequest);
    }

    @Subscribe
    public void onEvent(final JoinPlayerLobbyRequest lobbyJoinPlayerRequest) {
        handleEvent(lobbyJoinPlayerRequest);
    }

    @Subscribe
    public void onEvent(final RetrieveAllLobbiesRequest retrieveAllLobbiesRequest) {
        handleEvent(retrieveAllLobbiesRequest);
    }

    @Subscribe
    public void onEvent(final LeavePlayerLobbyRequest lobbyLeaveUserRequest) {
        handleEvent(lobbyLeaveUserRequest);
    }

    @Subscribe
    public void onEvent(final KickPlayerLobbyRequest lobbyKickPlayerRequest) {
        handleEvent(lobbyKickPlayerRequest);
    }

    @Subscribe
    public void onEvent(final UpdateLobbyStatusRequest lobbyUpdateStatusRequest) {
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