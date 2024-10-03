package de.uol.swp.server.role;

import de.uol.swp.common.role.RoleCard;
import de.uol.swp.common.util.Color;
import lombok.Getter;

import java.util.Set;

/**
 * The {@code RoleManagement} class manages and tracks the assignment of roles to users in a specific lobby.
 * It provides methods to retrieve available roles, assign roles to users, and manage user-role associations
 * within the context of a lobby.
 *
 * @author Jannis Moehlenbrock
 * @since 2024-09-06
 */
@Getter
public class RoleManagement {

    private final RoleCard logistiker = new RoleCard("Logistiker", new Color(), null);
    private final RoleCard arzt = new RoleCard("Arzt", new Color(), null);
    private final RoleCard betriebsexperte = new RoleCard("Betriebsexperte", new Color(), null);
    private final RoleCard wissenschaftler = new RoleCard("Wissenschaftler", new Color(), null);
    private final RoleCard forscherin = new RoleCard("Forscherin", new Color(), null);

    /**
     * Returns a set of all available roles that can be assigned to users.
     *
     * @return a set containing all {@link RoleCard} objects representing the roles
     */
    public Set<RoleCard> getAllRoles() {
        return Set.of(
                arzt,
                logistiker,
                betriebsexperte,
                wissenschaftler,
                forscherin
        );
    }
}
