package de.uol.swp.server.lobby;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyStatus;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.lobby.response.CreateLobbyResponse;
import de.uol.swp.common.lobby.response.JoinUserLobbyResponse;
import de.uol.swp.common.lobby.response.JoinUserUserAlreadyInLobbyLobbyResponse;
import de.uol.swp.common.lobby.response.RetrieveAllLobbiesResponse;
import de.uol.swp.common.lobby.server_message.*;
import de.uol.swp.common.message.server.ServerMessage;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.UserPlayer;
import de.uol.swp.common.user.User;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.lobby.message.LobbyDroppedServerInternalMessage;
import de.uol.swp.server.role.message.UserLeaveLobbyServerInternalMessage;
import de.uol.swp.server.usermanagement.AuthenticationService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Handles the lobby requests send by the users
 *
 * @author Marco Grawunder
 * @since 2019-10-08
 */

@Singleton
public class LobbyService extends AbstractService {

    private final LobbyManagement lobbyManagement;
    private final AuthenticationService authenticationService;

    /**
     * Constructor
     *
     * @param lobbyManagement The management class for creating, storing and deleting
     *                        lobbies
     * @param authenticationService the user management
     * @param eventBus the server-wide EventBus
     * @since 2019-10-08
     */
    @Inject
    public LobbyService(LobbyManagement lobbyManagement, AuthenticationService authenticationService, EventBus eventBus) {
        super(eventBus);
        this.lobbyManagement = lobbyManagement;
        this.authenticationService = authenticationService;
    }

    /**
     * Handles CreateLobbyRequests found on the EventBus
     *
     * If a CreateLobbyRequest is detected on the EventBus, this method is called.
     * It creates a new Lobby via the LobbyManagement using the parameters from the
     * request and sends a LobbyCreatedServerMessage to every connected user
     *
     * @param createLobbyRequest The CreateLobbyRequest found on the EventBus
     * @see de.uol.swp.server.lobby.LobbyManagement#createLobby(Lobby)
     * @see CreateUserLobbyServerMessage
     * @since 2019-10-08
     */
    @Subscribe
    public void onCreateLobbyRequest(CreateUserLobbyRequest createLobbyRequest) {
        final Lobby lobby = lobbyManagement.createLobby(createLobbyRequest.getLobby());

        final CreateLobbyResponse response = new CreateLobbyResponse(lobby);
        response.initWithMessage(createLobbyRequest);
        post(response);
    }

    /**
     * Handles LobbyJoinUserRequests found on the EventBus
     *
     * If a LobbyJoinUserRequest is detected on the EventBus, this method is called.
     * It adds a user to a Lobby stored in the LobbyManagement and sends a UserJoinedLobbyServerMessage
     * to every user in the lobby.
     *
     * @param lobbyJoinUserRequest The LobbyJoinUserRequest found on the EventBus
     * @see de.uol.swp.common.lobby.Lobby
     * @see JoinUserLobbyServerMessage
     * @since 2019-10-08
     */
    @Subscribe
    public void onLobbyJoinUserRequest(JoinUserLobbyRequest lobbyJoinUserRequest) {
        Optional<Lobby> lobbyOptional = lobbyManagement.getLobby(lobbyJoinUserRequest.getLobby());

        if (lobbyOptional.isEmpty()) {
            // TODO: error handling not existing lobby
            return;
        }

        final Lobby lobby = lobbyOptional.get();

        final User user = lobbyJoinUserRequest.getUser();

        if (user == null) {
            // TODO: error handling not existing user
            return;
        }

        if (lobby.containsUser(user)) {
            final JoinUserUserAlreadyInLobbyLobbyResponse response = new JoinUserUserAlreadyInLobbyLobbyResponse(lobby, user);
            response.initWithMessage(lobbyJoinUserRequest);
            post(response);
            return;
        }

        lobby.joinUser(user);

        final JoinUserLobbyResponse response = new JoinUserLobbyResponse(lobby, user);
        response.initWithMessage(lobbyJoinUserRequest);
        post(response);

        final JoinUserLobbyServerMessage userJoinedLobbyMessage = new JoinUserLobbyServerMessage(lobby, user.getWithoutPassword());
        sendToAllInLobby(lobby, userJoinedLobbyMessage);
    }


    /**
     * Handles JoinPlayerLobbyRequests found on the EventBus.
     *
     * When a JoinPlayerLobbyRequest is detected on the EventBus, this method is called.
     * It attempts to retrieve the lobby associated with the request. If the lobby does not exist,
     * appropriate error handling should be implemented. If the lobby exists, the specified player
     * is added to the lobby, and a JoinPlayerLobbyServerMessage is sent to notify all users in the
     * lobby that a new player has joined.
     *
     * @param lobbyJoinPlayerRequest The JoinPlayerLobbyRequest containing information about the
     *                                lobby and the player attempting to join.
     * @see de.uol.swp.common.lobby.Lobby
     * @see JoinPlayerLobbyServerMessage
     * @since 2024-10-06
     */
    @Subscribe
    public void onLobbyJoinPlayerRequest(JoinPlayerLobbyRequest lobbyJoinPlayerRequest) {
        Optional<Lobby> lobbyOptional = lobbyManagement.getLobby(lobbyJoinPlayerRequest.getLobby());

        if (lobbyOptional.isEmpty()) {
            // TODO: error handling not existing lobby
            return;
        }

        final Lobby lobby = lobbyOptional.get();
        final Player player = lobbyJoinPlayerRequest.getPlayer();

        lobby.addPlayer(player);

        final JoinPlayerLobbyServerMessage userJoinedLobbyMessage = new JoinPlayerLobbyServerMessage(lobby, player);
        sendToAllInLobby(lobby, userJoinedLobbyMessage);
    }

    /**
     * Handles LobbyLeaveUserRequests found on the EventBus.
     *
     * When a LobbyLeaveUserRequest is detected on the EventBus, this method is triggered.
     * It removes a user from the specified Lobby stored in the LobbyManagement and sends a
     * LobbyLeaveUserServerMessage to notify all users in the lobby that someone has left.
     *
     * The method checks if the lobby exists and processes the user leaving if the lobby is found.
     * If the lobby does not exist, error handling is required.
     *
     * @param lobbyLeaveUserRequest The LobbyLeaveUserRequest detected on the EventBus, containing
     *                              information about the user leaving and the associated lobby.
     * @see de.uol.swp.common.lobby.Lobby
     * @see LeavePlayerLobbyServerMessage
     * @since 2019-10-08
     */
    @Subscribe
    public void onLobbyLeaveUserRequest(LeavePlayerLobbyRequest lobbyLeaveUserRequest) {
        handleLobbyLeave(lobbyLeaveUserRequest, lobby -> {
            final AbstractPlayerLobbyServerMessage message = new LeavePlayerLobbyServerMessage(lobby, lobbyLeaveUserRequest.getPlayer());
            sendToAllInLobby(lobby, message);
        });
        // TODO: error handling not existing lobby
    }

    /**
     * Handles LobbyKickUserRequests found on the EventBus.
     *
     * When a LobbyKickUserRequest is detected on the EventBus, this method is triggered.
     * It removes the specified user from the Lobby stored in the LobbyManagement and
     * sends a LobbyKickUserServerMessage to notify all users in the lobby that the user
     * has been kicked.
     *
     * The method verifies that the lobby exists and, if found, processes the user kick
     * and performs further handling such as notifying all users in the lobby. If the
     * lobby does not exist, error handling is needed.
     *
     * @param lobbyKickPlayerRequest The LobbyKickUserRequest detected on the EventBus, containing
     *                             the user to be kicked and the associated lobby information.
     * @see de.uol.swp.common.lobby.Lobby
     * @see KickPlayerLobbyServerMessage
     * @since 2024-09-23
     */
    @Subscribe
    public void onLobbyKickUserRequest(KickPlayerLobbyRequest lobbyKickPlayerRequest) {
        handleLobbyLeave(lobbyKickPlayerRequest, lobby -> {
            final AbstractPlayerLobbyServerMessage message = new KickPlayerLobbyServerMessage(lobby, lobbyKickPlayerRequest.getPlayer());
            sendToAllInLobby(lobbyKickPlayerRequest.getLobby(), message);
        });
        // TODO: error handling not existing lobby
    }

    /**
     * Handles the process of a player leaving or being removed from a lobby.
     *
     * This method checks if the lobby exists and contains only one user. If so, it triggers the lobby
     * to be dropped by the LobbyManagement and sends a LobbyDroppedServerInternalMessage
     * to notify the system that the lobby has been closed. If there are more users, the method
     * removes the specified user from the lobby.
     *
     * Additionally, a callback function is invoked with the updated lobby after processing.
     * This method is used in both user-leave and user-kick scenarios.
     *
     * @param lobbyRequest The request that contains information about the user and the lobby.
     * @param callback     A Consumer function that is executed after the lobby has been updated.
     * @see de.uol.swp.common.lobby.Lobby
     * @see LobbyDroppedServerInternalMessage
     * @since 2024-10-06
     */
    private void handleLobbyLeave(AbstractPlayerLobbyRequest lobbyRequest, Consumer<Lobby> callback) {
        Optional<Lobby> lobbyOptional = lobbyManagement.getLobby(lobbyRequest.getLobby());

        if (lobbyOptional.isPresent()) {
            final Lobby lobby = lobbyOptional.get();
            final Player player = lobbyRequest.getPlayer();

            if (isPlayerNotInLobby(lobby, player)) {
                return;
            }

            if (isLobbyDroppable(lobby, player)) {
                lobbyManagement.dropLobby(lobby);
                LobbyDroppedServerInternalMessage message = new LobbyDroppedServerInternalMessage(lobby);
                post(message);
            } else {
                lobby.removePlayer(player);

                UserLeaveLobbyServerInternalMessage removeUserFromRoleInternalMessage = new UserLeaveLobbyServerInternalMessage(lobby);
                post(removeUserFromRoleInternalMessage);
            }

            callback.accept(lobby);
        }

    }

    /**
     * Checks if a player is not in the specified lobby.
     *
     * This method verifies whether the given player is absent from the specified
     * lobby's list of players.
     *
     * @param lobby The lobby to check for the player's presence.
     * @param player The player to check for absence in the lobby.
     * @return true if the player is not in the lobby; false otherwise.
     * @since 2024-09-23
     */
    public boolean isPlayerNotInLobby(Lobby lobby, Player player) {
        return !lobby.getPlayers().contains(player);
    }

    /**
     * Determines if the specified lobby can be dropped.
     *
     * This method checks whether the lobby can be removed based on the number of players
     * currently in the lobby.
     *
     * @param lobby The lobby to evaluate for droppability.
     * @param player The player being considered for the lobby drop condition.
     * @return true if the lobby can be dropped; false otherwise.
     * @since 2024-09-23
     */
    public boolean isLobbyDroppable(Lobby lobby, Player player) {
        return lobby.getPlayers().size() == 1 || (hasOnlyOneUserPlayer(lobby) && player instanceof UserPlayer);
    }

    /**
     * Checks if the specified lobby contains exactly one UserPlayer.
     *
     * This method iterates through the players in the given lobby and counts how many of them
     * are instances of UserPlayer. It returns true if there is exactly one UserPlayer in the lobby,
     * and false otherwise.
     *
     * @param lobby The Lobby to check for UserPlayer instances.
     * @return true if there is exactly one UserPlayer in the lobby; false otherwise.
     * @see Player
     * @see UserPlayer
     * @since 2024-10-06
     */
    private boolean hasOnlyOneUserPlayer(Lobby lobby) {
        final Set<Player> players = lobby.getPlayers();
        return players.stream()
                .filter(UserPlayer.class::isInstance)
                .count() == 1;
    }

    /**
     * Prepares a given ServerMessage to be send to all players in the lobby and
     * posts it on the EventBus
     *
     * @param lobby Name of the lobby the players are in
     * @param message the message to be send to the users
     * @see ServerMessage
     * @since 2019-10-08
     */
    public void sendToAllInLobby(Lobby lobby, ServerMessage message) {

        if (lobby != null) {
            message.setReceiver(authenticationService.getSessions(lobby.getUsers()));
            post(message);
        }

        // TODO: error handling not existing lobby
    }

    /**
     * Handles LobbyFindLobbiesRequests found on the EventBus
     *
     * <p>
     * If a {@link RetrieveAllLobbiesRequest} is detected on the EventBus, this method is called.
     * It creates a {@link RetrieveAllLobbiesResponse} containing all lobbies and posts it to the EventBus.
     * </p>
     *
     * @param retrieveAllLobbiesRequest The LobbyFindLobbiesRequest found on the EventBus
     * @see de.uol.swp.common.lobby.Lobby
     * @see RetrieveAllLobbiesRequest
     * @see RetrieveAllLobbiesResponse
     * @since 2024-08-24
     */
    @Subscribe
    public void onLobbyFindLobbiesRequest(final RetrieveAllLobbiesRequest retrieveAllLobbiesRequest) {
        final RetrieveAllLobbiesResponse retrieveAllLobbiesResponse = new RetrieveAllLobbiesResponse(lobbyManagement.getAllLobbies());
        retrieveAllLobbiesResponse.initWithMessage(retrieveAllLobbiesRequest);
        post(retrieveAllLobbiesResponse);
    }

    /**
     * Handles LobbyUpdateStatusRequests found on the EventBus
     *
     * <p>
     * If a {@link UpdateLobbyStatusRequest} is detected on the EventBus, this method is called.
     * It updates the status of a lobby and sends an {@link RetrieveAllLobbiesServerMessage} to all users.
     * </p>
     *
     * @param lobbyUpdateStatusRequest The LobbyUpdateStatusRequest found on the EventBus
     * @see de.uol.swp.common.lobby.Lobby
     * @see de.uol.swp.common.lobby.LobbyStatus
     * @see UpdateLobbyStatusRequest
     * @see RetrieveAllLobbiesServerMessage
     */
    @Subscribe
    public void onLobbyUpdateStatusRequest(UpdateLobbyStatusRequest lobbyUpdateStatusRequest) {
        Lobby lobby = lobbyUpdateStatusRequest.getLobby();

        if (lobbyManagement.getLobby(lobby).isPresent()) {
            LobbyStatus status = lobbyUpdateStatusRequest.getStatus();
            lobbyManagement.updateLobbyStatus(lobby, status);
        }

        RetrieveAllLobbiesServerMessage message = new RetrieveAllLobbiesServerMessage(lobbyManagement.getAllLobbies());
        sendToAll(message);
    }

    /**
     * Handles DifficultyUpdateRequests found on the EventBus
     *
     * When a DifficultyUpdateRequest is detected on the EventBus, this method updates
     * the difficulty setting in the specified lobby and broadcasts the change to all
     * users in that lobby.
     *
     * @param request The DifficultyUpdateRequest found on the EventBus
     * @since 2025-01-28
     */
    @Subscribe
    public void onDifficultyUpdateRequest(DifficultyUpdateRequest request) {
        Optional<Lobby> lobbyOptional = lobbyManagement.getLobby(request.getLobby());

        if (lobbyOptional.isPresent()) {
            final Lobby lobby = lobbyOptional.get();
            lobby.setNumberOfEpidemicCards(request.getNumberOfEpidemicCards());
            lobbyManagement.updateLobby(lobby);

            DifficultyUpdateServerMessage message = new DifficultyUpdateServerMessage(
                    lobby,
                    request.getNumberOfEpidemicCards()
            );
            sendToAllInLobby(lobby, message);
        }
    }

}
