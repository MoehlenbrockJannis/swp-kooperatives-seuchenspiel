package de.uol.swp.server.role;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.role.RoleCard;
import de.uol.swp.common.role.RoleCardColor;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import lombok.Getter;

import java.util.*;

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

    /**
     * A mapping of lobbies to their respective users and their assigned roles.
     * This map stores which user has which role in a specific lobby.
     */
    private final Map<Lobby, Map<User, RoleCard>> userRolesInLobby = new HashMap<>();

    private final RoleCard logistiker = new RoleCard("Logistiker", new RoleCardColor(), null);
    private final RoleCard arzt = new RoleCard("Arzt", new RoleCardColor(), null);
    private final RoleCard betriebsexperte = new RoleCard("Betriebsexperte", new RoleCardColor(), null);
    private final RoleCard wissenschaftler = new RoleCard("Wissenschaftler", new RoleCardColor(), null);
    private final RoleCard forscherin = new RoleCard("Forscherin", new RoleCardColor(), null);

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

    /**
     * Retrieves the roles that are still available for assignment in a given lobby.
     * Any role already assigned to a user in the lobby is considered unavailable.
     *
     * @param lobby the {@link Lobby} for which to check available roles
     * @return a set of {@link RoleCard} objects representing the roles that are still available in the lobby
     */
    public Set<RoleCard> getAvailableRolesInLobby(final Lobby lobby) {
        final Set<RoleCard> roleCards = new HashSet<>(getAllRoles());
        if (userRolesInLobby.containsKey(lobby)) {
            final Collection<RoleCard> roleCardsUsed = userRolesInLobby.get(lobby).values();
            roleCards.removeAll(roleCardsUsed);
        }
        return roleCards;
    }

    /**
     * Assigns a specific role to a user within a specific lobby. If the user already has a role in that
     * lobby, the old role is replaced with the new one.
     *
     * @param lobby    the {@link Lobby} in which the role is assigned
     * @param user     the {@link User} to whom the role is assigned
     * @param roleCard the {@link RoleCard} representing the role to assign to the user
     */
    public void addUserRoleInLobby(Lobby lobby, User user, RoleCard roleCard) {
        final Map<User, RoleCard> userRoles = userRolesInLobby.computeIfAbsent(lobby, k -> new HashMap<>());
        userRoles.put(user, roleCard);
    }

    /**
     * Checks if a specific role is available for a user in the given lobby. A role is considered available if
     * it has not yet been assigned to another user in the lobby.
     *
     * @param lobby    the {@link Lobby} in which to check the role's availability
     * @param user     the {@link User} for whom the check is being made
     * @param roleCard the {@link RoleCard} representing the role to check
     * @return {@code true} if the role is available for the user, {@code false} otherwise
     */
    public boolean isRoleAvailableForUser(final Lobby lobby, final User user, final RoleCard roleCard) {
        Map<User, RoleCard> userRoles = userRolesInLobby.computeIfAbsent(lobby, k -> new HashMap<>());
        return !userRoles.containsValue(roleCard);
    }


    /**
     * Removes all role associations for the given lobby by its name.
     *
     * @param lobbyName the name of the lobby for which all roles should be removed
     */
    public void dropLobby(String lobbyName) {
        dropLobby(new LobbyDTO(lobbyName, new UserDTO("fake1", "fake2", "fake3")));
    }

    /**
     * Removes all role associations for the given lobby.
     *
     * @param lobby the {@link Lobby} for which all roles should be removed
     */
    public void dropLobby(Lobby lobby) {
        userRolesInLobby.remove(lobby);
    }

    /**
     * Removes the role assigned to a specific user in a given lobby. If the user has no assigned role,
     * the method has no effect.
     *
     * @param lobby the {@link Lobby} in which to remove the user's role
     * @param user  the {@link User} whose role should be removed
     */
    public void dropUserInLobby(final Lobby lobby, final User user) {
        if (userRolesInLobby.containsKey(lobby)) {
            userRolesInLobby.get(lobby).remove(user);
        }
    }
}
