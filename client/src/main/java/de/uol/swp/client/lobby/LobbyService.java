package de.uol.swp.client.lobby;

import com.google.inject.Inject;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyStatus;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.user.User;
import org.greenrobot.eventbus.EventBus;

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
     * @param eventBus    The EventBus set in ClientModule
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
     * @param lobbyName Name chosen for the new lobby
     * @param owner User who wants to create the new lobby
     * @see CreateLobbyRequest
     * @since 2019-11-20
     */
    public void createNewLobby(String lobbyName, User owner) {
        LobbyDTO lobby = new LobbyDTO(lobbyName, owner);
        CreateLobbyRequest createLobbyRequest = new CreateLobbyRequest(lobby, owner);
        eventBus.post(createLobbyRequest);
    }

    /**
     * Posts a request to join a specified lobby on the EventBus
     *
     * @param lobby Name of the lobby the user wants to join
     * @param user User who wants to join the lobby
     * @see LobbyJoinUserRequest
     * @since 2019-11-20
     */
    public void joinLobby(final Lobby lobby, final User user) {
        final LobbyJoinUserRequest joinUserRequest = new LobbyJoinUserRequest(lobby, user);
        eventBus.post(joinUserRequest);
    }

    /**
     * Posts a request to find all lobbies to the EventBus
     *
     * @see RetrieveAllLobbiesRequest
     * @since 2024-08-24
     */
    public void findLobbies() {
        final RetrieveAllLobbiesRequest retrieveAllLobbiesRequest = new RetrieveAllLobbiesRequest();
        eventBus.post(retrieveAllLobbiesRequest);
    }

    /**
     * Posts a request to leave a lobby on the EventBus
     *
     * @param lobby Name of the lobby to leave
     * @param user User who wants to leave the lobby
     * @see LobbyLeaveUserRequest
     * @since 2024-08-28
     */
    public void leaveLobby(final Lobby lobby, final User user) {
        final LobbyLeaveUserRequest leaveLobbyRequest = new LobbyLeaveUserRequest(lobby, user);
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
