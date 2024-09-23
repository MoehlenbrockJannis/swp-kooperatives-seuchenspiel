package de.uol.swp.client.lobby;

import com.google.inject.Inject;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyStatus;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.map.request.RetrieveOriginalGameMapTypeRequest;
import de.uol.swp.common.plague.request.RetrieveAllPlaguesRequest;
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
     * @param minPlayers Minimum number of players for the lobby
     * @param maxPlayers Maximum number of players for the lobby
     * @see CreateLobbyRequest
     * @since 2019-11-20
     */
    public void createNewLobby(String lobbyName, User owner, int minPlayers, int maxPlayers) {
        LobbyDTO lobby = new LobbyDTO(lobbyName, owner, minPlayers, maxPlayers);
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

    /**
     * Posts a request to get the mapType from the original game to the EventBus
     *
     * @see RetrieveOriginalGameMapTypeRequest
     * @author David Scheffler
     * @since 2024-09-23
     */
    public void getOriginalGameMapType(){
        RetrieveOriginalGameMapTypeRequest request = new RetrieveOriginalGameMapTypeRequest();
        eventBus.post(request);
    }

    /**
     * Posts a request to get all plagues to the EventBus
     *
     * @see RetrieveAllPlaguesRequest
     * @author David Scheffler
     * @since 2024-09-23
     */
    public void getPlagues(){
        RetrieveAllPlaguesRequest request = new RetrieveAllPlaguesRequest();
        eventBus.post(request);
    }
}
