package de.uol.swp.server.role;

import com.google.inject.Inject;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.LobbyLeaveUserRequest;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.role.RoleCard;
import de.uol.swp.common.role.message.RolesAvailableMessage;
import de.uol.swp.common.role.response.RoleAssignedMessage;
import de.uol.swp.common.role.response.RoleAvailableMessage;
import de.uol.swp.common.role.response.RoleUnavailableMessage;
import de.uol.swp.common.role.response.RolesToComboBoxMessage;
import de.uol.swp.common.role.request.RoleAssignmentRequest;
import de.uol.swp.common.role.request.RolesToComboBoxRequest;
import de.uol.swp.common.user.User;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.lobby.LobbyService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Optional;

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
        final Lobby lobby = roleAssignmentRequest.getLobby();
        final User user = roleAssignmentRequest.getUser();
        final RoleCard roleCard = roleAssignmentRequest.getRoleCard();

        ResponseMessage message;
        RoleAssignedMessage roleAssignedMessage;
        if (roleManagement.isRoleAvailableForUser(lobby, user, roleCard)) {
            roleManagement.addUserRoleInLobby(lobby, user, roleCard);
            message = new RoleAvailableMessage();

            roleAssignedMessage = new RoleAssignedMessage(roleAssignmentRequest.getRoleCard().getName(), true);

            roleAssignedMessage.initWithMessage(roleAssignmentRequest);
            post(roleAssignedMessage);
        } else {
            message = new RoleUnavailableMessage();

            roleAssignedMessage = new RoleAssignedMessage(roleAssignmentRequest.getRoleCard().getName(), false);
            roleAssignedMessage.initWithMessage(roleAssignmentRequest);
            post(roleAssignedMessage);
        }

        message.initWithMessage(roleAssignmentRequest);
        post(message);


        sendAvailableRolesToClients(lobby);
    }

    /**
     * Handles a {@link RolesToComboBoxRequest} which requests the available roles to be rendered in a ComboBox
     * on the client side. This sends back all available roles.
     *
     * @param rolesToComboBoxRequest the request to render the roles in the ComboBox
     */
    @Subscribe
    public void onRolesSendToClient(RolesToComboBoxRequest rolesToComboBoxRequest) {
        RolesToComboBoxMessage rolesToComboBoxMessage = new RolesToComboBoxMessage(roleManagement.getAllRoles(), rolesToComboBoxRequest.getLobby());
        rolesToComboBoxMessage.initWithMessage(rolesToComboBoxRequest);
        post(rolesToComboBoxMessage);
    }

    /**
     * Handles a {@link LobbyLeaveUserRequest} which occurs when a user leaves a lobby.
     * It removes the user's role from the lobby and updates the available roles for other users in the lobby.
     *
     * @param roleLogoutRequest the request to log out the user from the lobby
     */
    @Subscribe
    public void onRoleLogoutRequest(LobbyLeaveUserRequest roleLogoutRequest) {
        String lobbyName = roleLogoutRequest.getLobbyName();

        Optional<Lobby> lobbyOptional;
        lobbyOptional = lobbyManagement.getLobby(lobbyName);
        System.out.println(lobbyManagement.getAllLobbies());

        if (lobbyOptional.isEmpty()) {
            roleManagement.dropLobby(roleLogoutRequest.getLobbyName());
            return;
        }

        Lobby lobby = lobbyOptional.get();

        roleManagement.dropUserInLobby(lobby, roleLogoutRequest.getUser());

        //sendAvailableRolesToClients(lobby);
    }

    /**
     * Sends the available roles in the given lobby to all clients in that lobby.
     *
     * @param lobby the {@link Lobby} to which the available roles are being sent
     */
    private void sendAvailableRolesToClients(final Lobby lobby) {
        final RolesAvailableMessage message = new RolesAvailableMessage(lobby, roleManagement.getAvailableRolesInLobby(lobby));
        lobbyService.sendToAllInLobby(lobby.getName(), message);
    }

}