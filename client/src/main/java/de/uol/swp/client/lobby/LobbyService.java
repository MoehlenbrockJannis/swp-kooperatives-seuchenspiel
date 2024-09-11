package de.uol.swp.client.lobby;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyStatus;
import de.uol.swp.common.lobby.request.LobbyFindLobbiesRequest;
import de.uol.swp.common.lobby.request.LobbyUpdateStatusRequest;
import de.uol.swp.common.user.User;
import org.greenrobot.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.common.lobby.message.CreateLobbyRequest;
import de.uol.swp.common.lobby.message.LobbyJoinUserRequest;
import de.uol.swp.common.lobby.message.LobbyLeaveUserRequest;
import de.uol.swp.common.user.UserDTO;

/**
 * Classes that manages lobbies
 *
 * @author Marco Grawunder
 * @since 2019-11-20
 *
 */


public class LobbyService {

    private final EventBus eventBus;

    /**
     * Constructor
     *
     * @param eventBus The EventBus set in ClientModule
     * @see de.uol.swp.client.di.ClientModule
     * @since 2019-11-20
     */
    @Inject
    public LobbyService(EventBus eventBus) {
        this.eventBus = eventBus;
        // No @Subscribe, no need to register
        // this.eventBus.register(this);
    }

    /**
     * Posts a request to create a lobby on the EventBus
     *
     * @param name Name chosen for the new lobby
     * @param user User who wants to create the new lobby
     * @see de.uol.swp.common.lobby.message.CreateLobbyRequest
     * @since 2019-11-20
     */
    public void createNewLobby(String name, UserDTO user) {
        CreateLobbyRequest createLobbyRequest = new CreateLobbyRequest(name, user);
        eventBus.post(createLobbyRequest);
    }

    /**
     * Posts a request to join a specified lobby on the EventBus
     *
     * @param name Name of the lobby the user wants to join
     * @param user User who wants to join the lobby
     * @see de.uol.swp.common.lobby.message.LobbyJoinUserRequest
     * @since 2019-11-20
     */
    public void joinLobby(final String name, final User user) {
        final LobbyJoinUserRequest joinUserRequest = new LobbyJoinUserRequest(name, user);
        eventBus.post(joinUserRequest);
    }

    /**
     * Posts a request to find all lobbies to the EventBus
     *
     * @see de.uol.swp.common.lobby.request.LobbyFindLobbiesRequest
     * @since 2024-08-24
     */
    public void findLobbies() {
        final LobbyFindLobbiesRequest lobbyFindLobbiesRequest = new LobbyFindLobbiesRequest();
        eventBus.post(lobbyFindLobbiesRequest);
    }

    /**
     * Posts a request to leave a lobby on the EventBus
     *
     * @param lobbyName Name of the lobby to leave
     * @param user User who wants to leave the lobby
     * @see de.uol.swp.common.lobby.message.LobbyLeaveUserRequest
     * @since 2024-08-28
     */
    public void leaveLobby(final String lobbyName, final User user) {
        final LobbyLeaveUserRequest leaveLobbyRequest = new LobbyLeaveUserRequest(lobbyName, (UserDTO) user);
        eventBus.post(leaveLobbyRequest);
    }

    /**
     * Updates the status of a lobby
     *
     * @param lobby The lobby to update
     * @param lobbyStatus The new status of the lobby
     * @since 2024-08-29
     */
    public void updateLobbyStatus(Lobby lobby, LobbyStatus lobbyStatus) {
        final LobbyUpdateStatusRequest updateStatusRequest = new LobbyUpdateStatusRequest(lobby, lobbyStatus);
        eventBus.post(updateStatusRequest);
    }
}
