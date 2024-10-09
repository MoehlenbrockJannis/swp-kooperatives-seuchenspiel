package de.uol.swp.server.role;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.role.RoleCard;
import de.uol.swp.common.user.User;
import de.uol.swp.common.util.RoleColors;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

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
    private final RoleCard logistiker = new RoleCard("Logistiker", RoleColors.LOGISTIKER_COLOR_PURPLE, null);
    private final RoleCard arzt = new RoleCard("Arzt", RoleColors.ARZT_COLOR_ORANGE, null);
    private final RoleCard betriebsexperte = new RoleCard("Betriebsexperte", RoleColors.BETRIEBSEXPERTE_COLOR_GREEN, null);
    private final RoleCard wissenschaftler = new RoleCard("Wissenschaftler", RoleColors.WISSENSCHAFTLER_COLOR_GRAY, null);
    private final RoleCard forscherin = new RoleCard("Forscherin", RoleColors.FORSCHERIN_COLOR_BROWN, null);

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

    public void assignRolesToPlayers(final Lobby lobby) {
        final Set<Player> players = lobby.getPlayers();

        final List<RoleCard> availableRolesList = getAvailableRoles(players);

        for (final Player player : players) {
            if (player.getRole() == null) {
                final RoleCard roleCard = availableRolesList.remove(0);
                player.setRole(roleCard);
            }
        }
    }

    public List<RoleCard> getAvailableRoles(final Collection<Player> players) {
        final List<RoleCard> usedRoles = players.stream()
                .map(Player::getRole)
                .toList();

        final Set<RoleCard> allRoles = getAllRoles();
        final List<RoleCard> availableRolesList = allRoles.stream()
                .filter(role -> !usedRoles.contains(role))
                .collect(Collectors.toList());
        Collections.shuffle(availableRolesList);

        return availableRolesList;
    }
}
