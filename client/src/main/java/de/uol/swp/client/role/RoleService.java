package de.uol.swp.client.role;

import com.google.inject.Inject;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.role.RoleCard;
import de.uol.swp.common.role.request.RetrieveAllRolesRequest;
import de.uol.swp.common.role.request.RoleAssignmentRequest;
import de.uol.swp.common.user.User;
import org.greenrobot.eventbus.EventBus;

/**
 * This class contains all request methods that have something to do with the roles in the game
 */
public class RoleService {

    private final EventBus eventBus;

    @Inject
    public RoleService(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     *
     * Sends a request to retrieve all roles associated with a specified lobby.
     * @param lobby The lobby for which to retrieve roles.
     */
    public void sendRetrieveAllRolesRequest(Lobby lobby) {
        RetrieveAllRolesRequest retrieveAllRolesRequest = new RetrieveAllRolesRequest(lobby);
        eventBus.post(retrieveAllRolesRequest);
    }

    /**
     *
     * Sends a request to assign a specific role to a user within the specified lobby.
     * @param lobby The lobby in which the role assignment is made.
     * @param user The user to whom the role is being assigned.
     * @param roleCard The role card representing the assigned role.
     */
    public void sendRoleAssignmentRequest(Lobby lobby, User user, RoleCard roleCard) {
        RoleAssignmentRequest roleAssignmentRequest = new RoleAssignmentRequest(lobby, user, roleCard);
        eventBus.post(roleAssignmentRequest);
    }
}
