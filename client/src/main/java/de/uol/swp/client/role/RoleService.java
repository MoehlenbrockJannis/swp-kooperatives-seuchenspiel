package de.uol.swp.client.role;

import com.google.inject.Inject;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.role.RoleCard;
import de.uol.swp.common.role.request.RoleAssignmentRequest;
import de.uol.swp.common.role.request.RetrieveAllRolesRequest;
import de.uol.swp.common.user.User;
import org.greenrobot.eventbus.EventBus;

/**
 * This class contains all request methods that have something to do with the roles in the game
 *
 * @author Jannis Moehlenbrock
 * @since 2024-09-07
 */
public class RoleService {

    private final EventBus eventBus;

    @Inject
    public RoleService(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void sendRolesToComboBoxRequest(Lobby lobby) {
        RetrieveAllRolesRequest retrieveAllRolesRequest = new RetrieveAllRolesRequest(lobby);
        eventBus.post(retrieveAllRolesRequest);
    }

    public void sendRoleAssignmentRequest(Lobby lobby, User user, RoleCard roleCard) {
        RoleAssignmentRequest roleAssignmentRequest = new RoleAssignmentRequest(lobby, user, roleCard);
        eventBus.post(roleAssignmentRequest);
    }

}
