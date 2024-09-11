package de.uol.swp.server.lobby;

import de.uol.swp.common.lobby.response.LobbyCreatedResponse;
import de.uol.swp.common.lobby.response.LobbyJoinUserResponse;
import de.uol.swp.common.lobby.response.LobbyJoinUserUserAlreadyInLobbyResponse;
import de.uol.swp.server.lobby.message.LobbyDroppedServerInternalMessage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyStatus;
import de.uol.swp.common.lobby.message.*;
import de.uol.swp.common.lobby.request.LobbyFindLobbiesRequest;
import de.uol.swp.common.lobby.request.LobbyUpdateStatusRequest;
import de.uol.swp.common.lobby.response.*;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.usermanagement.AuthenticationService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
     * request and sends a LobbyCreatedMessage to every connected user
     *
     * @param createLobbyRequest The CreateLobbyRequest found on the EventBus
     * @see de.uol.swp.server.lobby.LobbyManagement#createLobby(String, User)
     * @see de.uol.swp.common.lobby.message.LobbyCreatedMessage
     * @since 2019-10-08
     */
    @Subscribe
    public void onCreateLobbyRequest(CreateLobbyRequest createLobbyRequest) {
        final Lobby lobby = lobbyManagement.createLobby(createLobbyRequest.getLobbyName(), createLobbyRequest.getOwner());

        final LobbyCreatedResponse response = new LobbyCreatedResponse(lobby);
        response.initWithMessage(createLobbyRequest);
        post(response);
    }

    /**
     * Handles LobbyJoinUserRequests found on the EventBus
     *
     * If a LobbyJoinUserRequest is detected on the EventBus, this method is called.
     * It adds a user to a Lobby stored in the LobbyManagement and sends a UserJoinedLobbyMessage
     * to every user in the lobby.
     *
     * @param lobbyJoinUserRequest The LobbyJoinUserRequest found on the EventBus
     * @see de.uol.swp.common.lobby.Lobby
     * @see de.uol.swp.common.lobby.message.UserJoinedLobbyMessage
     * @since 2019-10-08
     */
    @Subscribe
    public void onLobbyJoinUserRequest(LobbyJoinUserRequest lobbyJoinUserRequest) {
        Optional<Lobby> lobbyOptional = lobbyManagement.getLobby(lobbyJoinUserRequest.getLobbyName());

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

        final UserJoinedLobbyMessage userJoinedLobbyMessage = new UserJoinedLobbyMessage(lobby.getName(), user.getWithoutPassword());
        sendToAllInLobby(lobby.getName(), userJoinedLobbyMessage);
    }

    /**
     * Handles LobbyLeaveUserRequests found on the EventBus
     *
     * If a LobbyLeaveUserRequest is detected on the EventBus, this method is called.
     * It removes a user from a Lobby stored in the LobbyManagement and sends a
     * UserLeftLobbyMessage to every user in the lobby.
     *
     * @param lobbyLeaveUserRequest The LobbyJoinUserRequest found on the EventBus
     * @see de.uol.swp.common.lobby.Lobby
     * @see de.uol.swp.common.lobby.message.UserLeftLobbyMessage
     * @see de.uol.swp.common.lobby.message.LobbyDroppedServerInternalMessage
     * @since 2019-10-08
     */
    @Subscribe
    public void onLobbyLeaveUserRequest(LobbyLeaveUserRequest lobbyLeaveUserRequest) {
        Optional<Lobby> lobbyOptional = lobbyManagement.getLobby(lobbyLeaveUserRequest.getLobbyName());

        if (lobbyOptional.isPresent()) {
            final Lobby lobby = lobbyOptional.get();

            if (lobby.getUsers().size() == 1) {
                lobbyManagement.dropLobby(lobby.getName());
                LobbyDroppedServerInternalMessage message = new LobbyDroppedServerInternalMessage(lobby.getName());
                post(message);
            } else {
                lobby.leaveUser(lobbyLeaveUserRequest.getUser());
            }

            sendToAllInLobby(lobbyLeaveUserRequest.getLobbyName(), new UserLeftLobbyMessage(lobbyLeaveUserRequest.getLobbyName(), lobbyLeaveUserRequest.getUser()));
        }
        // TODO: error handling not existing lobby
    }

    /**
     * Prepares a given ServerMessage to be send to all players in the lobby and
     * posts it on the EventBus
     *
     * @param lobbyName Name of the lobby the players are in
     * @param message the message to be send to the users
     * @see de.uol.swp.common.message.ServerMessage
     * @since 2019-10-08
     */
    public void sendToAllInLobby(String lobbyName, ServerMessage message) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(lobbyName);

        if (lobby.isPresent()) {
            message.setReceiver(authenticationService.getSessions(lobby.get().getUsers()));
            post(message);
        }

        // TODO: error handling not existing lobby
    }

    /**
     * Handles LobbyFindLobbiesRequests found on the EventBus
     *
     * <p>
     * If a {@link LobbyFindLobbiesRequest} is detected on the EventBus, this method is called.
     * It creates a {@link LobbyFindLobbiesResponse} containing all lobbies and posts it to the EventBus.
     * </p>
     *
     * @param lobbyFindLobbiesRequest The LobbyFindLobbiesRequest found on the EventBus
     * @see de.uol.swp.common.lobby.Lobby
     * @see de.uol.swp.common.lobby.request.LobbyFindLobbiesRequest
     * @see de.uol.swp.common.lobby.response.LobbyFindLobbiesResponse
     * @since 2024-08-24
     */
    @Subscribe
    public void onLobbyFindLobbiesRequest(final LobbyFindLobbiesRequest lobbyFindLobbiesRequest) {
        final LobbyFindLobbiesResponse lobbyFindLobbiesResponse = new LobbyFindLobbiesResponse(lobbyManagement.getAllLobbies());
        lobbyFindLobbiesResponse.initWithMessage(lobbyFindLobbiesRequest);
        post(lobbyFindLobbiesResponse);
    }

    /**
     * Handles LobbyUpdateStatusRequests found on the EventBus
     *
     * <p>
     * If a {@link LobbyUpdateStatusRequest} is detected on the EventBus, this method is called.
     * It updates the status of a lobby and sends an {@link UpdatedLobbyListMessage} to all users.
     * </p>
     *
     * @param lobbyUpdateStatusRequest The LobbyUpdateStatusRequest found on the EventBus
     * @see de.uol.swp.common.lobby.Lobby
     * @see de.uol.swp.common.lobby.LobbyStatus
     * @see de.uol.swp.common.lobby.request.LobbyUpdateStatusRequest
     * @see de.uol.swp.common.lobby.message.UpdatedLobbyListMessage
     */
    @Subscribe
    public void onLobbyUpdateStatusRequest(LobbyUpdateStatusRequest lobbyUpdateStatusRequest) {
        String lobbyName = lobbyUpdateStatusRequest.getLobby().getName();

        if (lobbyManagement.getLobby(lobbyName).isPresent()) {
            LobbyStatus status = lobbyUpdateStatusRequest.getStatus();
            lobbyManagement.updateLobbyStatus(lobbyName, status);
        }

        UpdatedLobbyListMessage updatedLobbyListMessage = new UpdatedLobbyListMessage(lobbyManagement.getAllLobbies());
        updatedLobbyListMessage.initWithMessage(lobbyUpdateStatusRequest);
        post(updatedLobbyListMessage);
    }

}
