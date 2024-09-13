package de.uol.swp.server.lobby;

import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.lobby.response.CreateLobbyResponse;
import de.uol.swp.common.lobby.response.LobbyJoinUserResponse;
import de.uol.swp.common.lobby.response.LobbyJoinUserUserAlreadyInLobbyResponse;
import de.uol.swp.server.lobby.message.LobbyDroppedServerInternalMessage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyStatus;
import de.uol.swp.common.lobby.server_message.*;
import de.uol.swp.common.lobby.response.*;
import de.uol.swp.common.message.server.ServerMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.usermanagement.AuthenticationService;

import java.util.Optional;

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
     * @see CreateLobbyServerMessage
     * @since 2019-10-08
     */
    @Subscribe
    public void onCreateLobbyRequest(CreateLobbyRequest createLobbyRequest) {
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
     * @see LobbyJoinUserServerMessage
     * @since 2019-10-08
     */
    @Subscribe
    public void onLobbyJoinUserRequest(LobbyJoinUserRequest lobbyJoinUserRequest) {
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
            final LobbyJoinUserUserAlreadyInLobbyResponse response = new LobbyJoinUserUserAlreadyInLobbyResponse(lobby, user);
            response.initWithMessage(lobbyJoinUserRequest);
            post(response);
            return;
        }

        lobby.joinUser(user);

        final LobbyJoinUserResponse response = new LobbyJoinUserResponse(lobby, user);
        response.initWithMessage(lobbyJoinUserRequest);
        post(response);

        final LobbyJoinUserServerMessage userJoinedLobbyMessage = new LobbyJoinUserServerMessage(lobby, user.getWithoutPassword());
        sendToAllInLobby(lobby, userJoinedLobbyMessage);
    }

    /**
     * Handles LobbyLeaveUserRequests found on the EventBus
     *
     * If a LobbyLeaveUserRequest is detected on the EventBus, this method is called.
     * It removes a user from a Lobby stored in the LobbyManagement and sends a
     * UserLeftLobbyServerMessage to every user in the lobby.
     *
     * @param lobbyLeaveUserRequest The LobbyJoinUserRequest found on the EventBus
     * @see de.uol.swp.common.lobby.Lobby
     * @see LobbyLeaveUserServerMessage
     * @see de.uol.swp.server.lobby.message.LobbyDroppedServerInternalMessage
     * @see LobbyLeaveUserServerMessage
     * @see de.uol.swp.server.lobby.message.LobbyDroppedServerInternalMessage
     * @since 2019-10-08
     */
    @Subscribe
    public void onLobbyLeaveUserRequest(LobbyLeaveUserRequest lobbyLeaveUserRequest) {
        Optional<Lobby> lobbyOptional = lobbyManagement.getLobby(lobbyLeaveUserRequest.getLobby());

        if (lobbyOptional.isPresent()) {
            final Lobby lobby = lobbyOptional.get();

            if (lobby.getUsers().size() == 1) {
                lobbyManagement.dropLobby(lobby);
                LobbyDroppedServerInternalMessage message = new LobbyDroppedServerInternalMessage(lobby);
                post(message);
            } else {
                lobby.leaveUser(lobbyLeaveUserRequest.getUser());
            }

            sendToAllInLobby(lobby, new LobbyLeaveUserServerMessage(lobby, lobbyLeaveUserRequest.getUser()));
        }
        // TODO: error handling not existing lobby
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
     * If a {@link LobbyUpdateStatusRequest} is detected on the EventBus, this method is called.
     * It updates the status of a lobby and sends an {@link RetrieveAllLobbiesServerMessage} to all users.
     * </p>
     *
     * @param lobbyUpdateStatusRequest The LobbyUpdateStatusRequest found on the EventBus
     * @see de.uol.swp.common.lobby.Lobby
     * @see de.uol.swp.common.lobby.LobbyStatus
     * @see de.uol.swp.common.lobby.request.LobbyUpdateStatusRequest
     * @see RetrieveAllLobbiesServerMessage
     */
    @Subscribe
    public void onLobbyUpdateStatusRequest(LobbyUpdateStatusRequest lobbyUpdateStatusRequest) {
        Lobby lobby = lobbyUpdateStatusRequest.getLobby();

        if (lobbyManagement.getLobby(lobby).isPresent()) {
            LobbyStatus status = lobbyUpdateStatusRequest.getStatus();
            lobbyManagement.updateLobbyStatus(lobby, status);
        }

        RetrieveAllLobbiesServerMessage message = new RetrieveAllLobbiesServerMessage(lobbyManagement.getAllLobbies());
        sendToAll(message);
    }

}
