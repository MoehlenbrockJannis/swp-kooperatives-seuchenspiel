package de.uol.swp.server.role;

import com.google.inject.Inject;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.response.ResponseMessage;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.role.RoleCard;
import de.uol.swp.common.role.request.RetrieveAllAvailableRolesRequest;
import de.uol.swp.common.role.response.*;
import de.uol.swp.common.role.server_message.RetrieveAllAvailableRolesServerMessage;
import de.uol.swp.common.role.request.RoleAssignmentRequest;
import de.uol.swp.common.role.request.RetrieveAllRolesRequest;
import de.uol.swp.common.user.User;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.lobby.LobbyService;
import de.uol.swp.server.role.message.UserLeaveLobbyServerInternalMessage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The {@code RoleService} class handles role-related operations within the server.
 * It listens to role assignment requests, manages the roles of users within lobbies,
 * and communicates the available roles to clients.
 *
 * The class is responsible for:
 * - Assigning roles to users in a lobby
 * - Checking role availability
 * - Updating the lobby about available roles when a user leaves
 * - Rendering roles in a ComboBox on the client-side
 *
 * @author Jannis Moehlenbrock
 * @since 2024-09-06
 */
public class RoleService extends AbstractService {

    private final LobbyManagement lobbyManagement;
    private final LobbyService lobbyService;
    private final RoleManagement roleManagement;

    /**
     * Constructs a new {@code RoleService} with the provided dependencies.
     *
     * @param bus            the {@link EventBus} used to subscribe and post messages
     * @param lobbyManagement the {@link LobbyManagement} for managing lobbies
     * @param lobbyService    the {@link LobbyService} for managing lobby-related communications
     * @param roleManagement  the {@link RoleManagement} for handling role-related logic
     * @since 2019-10-08
     */
    @Inject
    public RoleService(EventBus bus, LobbyManagement lobbyManagement, LobbyService lobbyService, RoleManagement roleManagement) {
        super(bus);
        this.lobbyManagement = lobbyManagement;
        this.lobbyService = lobbyService;
        this.roleManagement = roleManagement;
    }

    /**
     * Handles a {@link RoleAssignmentRequest} from a client. This request contains the lobby,
     * the user, and the role the user wants to be assigned. The service checks whether the role
     * is available and either assigns it or sends a failure message.
     *
     * @param roleAssignmentRequest the request containing the user, lobby, and role information
     */
    @Subscribe
    public void onRoleAssignmentRequest(RoleAssignmentRequest roleAssignmentRequest) {
        final Optional<Lobby> lobbyOptional = lobbyManagement.getLobby(roleAssignmentRequest.getLobby());
        if(lobbyOptional.isEmpty()) {
            return;
        }

        final Lobby lobby = lobbyOptional.get();
        final User user = roleAssignmentRequest.getUser();
        final RoleCard roleCard = roleAssignmentRequest.getRoleCard();
        final Player player = lobby.getPlayerForUser(user);

        ResponseMessage message;
        RoleAssignmentResponse roleAssignedMessage;

        if (isRoleAvailableForPlayerInLobby(lobby, roleCard)) {
            player.setRole(roleCard);
            lobbyManagement.updateLobby(lobby);

            message = new RoleAvailableResponse();

            roleAssignedMessage = new RoleAssignmentResponse(roleAssignmentRequest.getRoleCard(), true);
        } else {
            message = new RoleUnavailableResponse();

            roleAssignedMessage = new RoleAssignmentResponse(roleAssignmentRequest.getRoleCard(), false);
        }

        roleAssignedMessage.initWithMessage(roleAssignmentRequest);
        post(roleAssignedMessage);

        message.initWithMessage(roleAssignmentRequest);
        post(message);


        sendAvailableRolesToClients(lobby);
    }

    /**
     * Checks if a specific role card is available for assignment in a given lobby.
     *
     * This method iterates through all players in the lobby to determine if the
     * specified role card is already assigned to any player. The role is considered
     * available if no player currently holds it.
     *
     * @param lobby The lobby in which to check for role availability
     * @param roleCard The role card to check for availability
     * @return true if the role card is available (not assigned to any player),
     *         false otherwise
     */
    public boolean isRoleAvailableForPlayerInLobby(Lobby lobby, RoleCard roleCard) {
        Set<Player> players = lobby.getPlayers();
        for(final Player playerInLobby : players) {
            RoleCard role = playerInLobby.getRole();
            if(role != null && role.equals(roleCard) ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Handles a {@link RetrieveAllRolesRequest} which requests the available roles to be rendered in a ComboBox
     * on the client side. This sends back all available roles.
     *
     * @param retrieveAllRolesRequest the request to render the roles in the ComboBox
     */
    @Subscribe
    public void onRolesSendToClient(RetrieveAllRolesRequest retrieveAllRolesRequest) {
        RetrieveAllRolesResponse retrieveAllRolesResponse = new RetrieveAllRolesResponse(getAvailableRolesInLobby(retrieveAllRolesRequest.getLobby()), retrieveAllRolesRequest.getLobby());
        retrieveAllRolesResponse.initWithMessage(retrieveAllRolesRequest);
        post(retrieveAllRolesResponse);
    }

    /**
     * Sends the available roles in the given lobby to all clients in that lobby.
     *
     * @param lobby the {@link Lobby} to which the available roles are being sent
     */
    private void sendAvailableRolesToClients(final Lobby lobby) {
        final RetrieveAllAvailableRolesServerMessage message = new RetrieveAllAvailableRolesServerMessage(
                lobby,
                getAvailableRolesInLobby(lobby)
        );
        lobbyService.sendToAllInLobby(lobby, message);
    }

    /**
     * Retrieves the set of available role cards in a given lobby.
     *
     * This method determines which role cards are still available for assignment
     * by comparing all existing roles with those already assigned to players in the lobby.
     *
     * @param lobby The lobby for which to determine available role cards
     * @return A Set of RoleCard objects representing the roles that are not yet assigned in the lobby
     */
    public Set<RoleCard> getAvailableRolesInLobby(Lobby lobby) {
        Set<RoleCard> allRoles = roleManagement.getAllRoles();

        Set<RoleCard> assignedRoles = lobby.getPlayers().stream()
                .map(Player::getRole)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return allRoles.stream()
                .filter(role -> !assignedRoles.contains(role))
                .collect(Collectors.toSet());
    }

    /**
     * Handles the event when a user leaves a lobby, updating available roles.
     *
     * This method is triggered by a UserLeaveLobbyServerInternalMessage event.
     * It responds by sending updated available roles information to all clients in the affected lobby.
     *
     * @param userLeaveLobbyServerInternalMessage The message containing information about the user leaving the lobby
     */
    @Subscribe
    public void onRemoveUserFromRole(UserLeaveLobbyServerInternalMessage userLeaveLobbyServerInternalMessage) {
        sendAvailableRolesToClients(userLeaveLobbyServerInternalMessage.getLobby());
    }

    /**
     * Sends all free roles to the owner of the lobby and sends a message to all players in the lobby
     * @param retrieveAllAvailableRolesRequest with the lobby
     */
    @Subscribe
    public void onRetrieveAllAvailableRoles(RetrieveAllAvailableRolesRequest retrieveAllAvailableRolesRequest) {
        RetrieveAllAvailableRolesResponse retrieveAllAvailableRolesResponse =
                new RetrieveAllAvailableRolesResponse(getAvailableRolesInLobby(retrieveAllAvailableRolesRequest.getLobby())
                        , retrieveAllAvailableRolesRequest.getLobby());

        retrieveAllAvailableRolesResponse.initWithMessage(retrieveAllAvailableRolesRequest);
        post(retrieveAllAvailableRolesResponse);
    }
}