package de.uol.swp.client.lobby;

import com.google.inject.Inject;
import de.uol.swp.common.game.GameDifficulty;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.lobby.LobbyStatus;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.map.request.RetrieveOriginalGameMapTypeRequest;
import de.uol.swp.common.plague.request.RetrieveAllPlaguesRequest;
import de.uol.swp.common.player.Player;
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
     * @see CreateUserLobbyRequest
     * @since 2019-11-20
     */
    public void createNewLobby(String lobbyName, User owner) {
        LobbyDTO lobby = new LobbyDTO(lobbyName, owner);
        CreateUserLobbyRequest createLobbyRequest = new CreateUserLobbyRequest(lobby, owner);
        eventBus.post(createLobbyRequest);
    }

    /**
     * Posts a request to join a specified lobby on the EventBus
     *
     * @param lobby Name of the lobby the user wants to join
     * @param user User who wants to join the lobby
     * @see JoinUserLobbyRequest
     * @since 2019-11-20
     */
    public void joinLobby(final Lobby lobby, final User user) {
        final JoinUserLobbyRequest joinUserRequest = new JoinUserLobbyRequest(lobby, user);
        eventBus.post(joinUserRequest);
    }

    /**
     * Sends a request to add a player to a lobby.
     *
     * This method creates a new {@link JoinPlayerLobbyRequest} with the provided lobby and player.
     * It then posts this request to the event bus, initiating the process of adding the player to
     * the specified lobby. The request will be handled by the relevant subscribers listening for
     * player join events.
     *
     * @param lobby  The lobby to which the player is joining.
     * @param player The player who is attempting to join the lobby.
     * @since 2024-10-06
     */
    public void playerJoinLobby(final Lobby lobby, final Player player) {
        final JoinPlayerLobbyRequest lobbyJoinPlayerRequest = new JoinPlayerLobbyRequest(lobby, player);
        eventBus.post(lobbyJoinPlayerRequest);
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
     * @param player player who wants to leave the lobby
     * @see LeavePlayerLobbyRequest
     * @since 2024-08-28
     */
    public void leaveLobby(final Lobby lobby, final Player player) {
        final LeavePlayerLobbyRequest leaveLobbyRequest = new LeavePlayerLobbyRequest(lobby, player);
        eventBus.post(leaveLobbyRequest);
    }

    /**
     * Sends a request to kick a specified player from a given lobby.
     * This method creates a LobbyKickUserRequest with the provided lobby and player,
     * and posts it to the event bus for further processing. This action is typically
     * triggered when the lobby owner decides to remove a player from the lobby.
     *
     * @param lobby The lobby from which the user will be kicked.
     * @param player The player to be kicked from the lobby.
     * @since 2024-09-23
     */
    public void kickPlayer(final Lobby lobby, final Player player) {
        final KickPlayerLobbyRequest request = new KickPlayerLobbyRequest(lobby, player);
        eventBus.post(request);
    }


    /**
     * Updates the status of a lobby
     *
     * @param lobby The lobby to update
     * @param lobbyStatus The new status of the lobby
     * @since 2024-08-29
     */
    public void updateLobbyStatus(Lobby lobby, LobbyStatus lobbyStatus) {
        final UpdateLobbyStatusRequest updateStatusRequest = new UpdateLobbyStatusRequest(lobby, lobbyStatus);
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

    /**
     * Posts a request to update the difficulty level of a lobby
     *
     * @param lobby The lobby to update the difficulty for
     * @param difficulty The new difficulty selected
     * @since 2025-01-28
     */
    public void updateDifficulty(Lobby lobby, GameDifficulty difficulty) {
        final DifficultyUpdateRequest request = new DifficultyUpdateRequest(lobby, difficulty);
        eventBus.post(request);
    }
}
